<template>
  <div class="task-screenshot-viewer">
    <el-button link type="primary" icon="Document" @click="openViewer">查看任务截图</el-button>

    <el-drawer
      v-model="visible"
      class="task-screenshot-drawer"
      direction="rtl"
      size="min(620px, calc(100vw - 16px))"
      append-to-body
      destroy-on-close
      :modal="false"
      :lock-scroll="false"
      @closed="handleClosed"
    >
    <template #header>
      <div class="drawer-heading">
        <strong>个人教学任务截图（PDF/图片）</strong>
        <span>可保持材料打开并继续审核</span>
      </div>
    </template>

    <div class="record-context">
      <div class="context-item">
        <span>教师</span>
        <strong>{{ activeRecord?.userName || '-' }}</strong>
      </div>
      <div class="context-item context-item-wide">
        <span>课程</span>
        <strong>{{ activeRecord?.courseName || '-' }}</strong>
      </div>
      <div class="context-item">
        <span>审核状态</span>
        <el-tag :type="statusTagType" effect="plain">{{ activeRecord?.status || '-' }}</el-tag>
      </div>
    </div>

    <el-alert
      title="同一教师、同一年度和同一模块的课程共用这组证明材料"
      type="info"
      :closable="false"
      show-icon
      class="shared-material-tip"
    />

    <div v-loading="loading" class="viewer-content">
      <el-empty v-if="!loading && attachments.length === 0" description="暂未上传任务截图" />
      <div v-for="(attachment, index) in attachments" :key="attachment.id" class="viewer-item">
        <div class="viewer-item-title">证明材料{{ index + 1 }}</div>
        <div class="viewer-item-body">
          <EvidenceFilePreview
            :url="attachment.fileUrl"
            :pdf-label="attachment.fileName"
            :thumbnail-size="360"
          />
          <div class="viewer-item-info">
            <span class="viewer-file-name">{{ attachment.fileName }}</span>
            <span class="viewer-upload-time">上传时间：{{ formatTime(attachment.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="drawer-footer">
        <div class="record-navigation">
          <el-button icon="ArrowLeft" :disabled="!hasPrevious" @click="showPrevious">本页上一条</el-button>
          <span>{{ activeIndex + 1 }} / {{ recordCount }}</span>
          <el-button :disabled="!hasNext" @click="showNext">
            本页下一条
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div v-if="mode === 'check'" class="audit-actions">
          <el-button
            type="success"
            :disabled="activeRecord?.status === '已通过'"
            @click="$emit('audit', activeRecord)"
          >
            审核通过
          </el-button>
          <el-button
            type="warning"
            :disabled="activeRecord?.status === '退回修改'"
            @click="$emit('reject', activeRecord)"
          >
            退回修改
          </el-button>
          <el-button type="primary" icon="Edit" @click="editActiveRecord">修改</el-button>
          <el-button type="danger" icon="Delete" @click="deleteActiveRecord">删除</el-button>
        </div>
      </div>
    </template>
    </el-drawer>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';
import { ArrowRight } from '@element-plus/icons-vue';
import { listTaskScreenshotAttachments } from '@/api/teaching/taskScreenshotAttachment';

export default {
  name: 'TeachingTaskScreenshotViewer',
  components: { ArrowRight },
  props: {
    userId: {
      type: [String, Number],
      required: true
    },
    year: {
      type: Number,
      required: true
    },
    moduleType: {
      type: String,
      required: true
    },
    records: {
      type: Array,
      default: () => []
    },
    currentIndex: {
      type: Number,
      default: 0
    },
    mode: {
      type: String,
      default: ''
    }
  },
  emits: ['active-row-change', 'audit', 'reject', 'edit', 'delete'],
  data() {
    return {
      visible: false,
      loading: false,
      attachments: [],
      activeIndex: 0,
      attachmentCache: {},
      requestSequence: 0
    };
  },
  computed: {
    activeRecord() {
      return this.records[this.activeIndex] || {
        userId: this.userId,
        year: this.year
      };
    },
    recordCount() {
      return Math.max(this.records.length, 1);
    },
    hasPrevious() {
      return this.records.length > 0 && this.activeIndex > 0;
    },
    hasNext() {
      return this.records.length > 0 && this.activeIndex < this.records.length - 1;
    },
    statusTagType() {
      const typeMap = {
        已通过: 'success',
        退回修改: 'warning',
        待审核: 'primary'
      };
      return typeMap[this.activeRecord?.status] || 'info';
    }
  },
  methods: {
    async openViewer() {
      this.activeIndex = Math.min(Math.max(this.currentIndex, 0), this.recordCount - 1);
      this.visible = true;
      this.notifyActiveRow();
      await this.loadAttachments();
    },
    async loadAttachments() {
      const record = this.activeRecord;
      const userId = record?.userId ?? this.userId;
      const year = record?.year ?? this.year;
      const cacheKey = `${userId}_${year}_${this.moduleType}`;

      if (this.attachmentCache[cacheKey]) {
        this.attachments = this.attachmentCache[cacheKey];
        return;
      }

      const requestSequence = ++this.requestSequence;
      this.loading = true;
      try {
        const response = await listTaskScreenshotAttachments({
          userId,
          year,
          moduleType: this.moduleType
        });
        const attachments = Array.isArray(response.data) ? response.data : [];
        this.attachmentCache[cacheKey] = attachments;
        if (requestSequence === this.requestSequence) {
          this.attachments = attachments;
        }
      } catch (error) {
        if (requestSequence === this.requestSequence) {
          this.attachments = [];
          ElMessage.error(error?.msg || '获取教学任务截图失败');
        }
      } finally {
        if (requestSequence === this.requestSequence) {
          this.loading = false;
        }
      }
    },
    async switchRecord(index) {
      if (index < 0 || index >= this.records.length) return;
      this.activeIndex = index;
      this.attachments = [];
      this.notifyActiveRow();
      await this.loadAttachments();
    },
    showPrevious() {
      this.switchRecord(this.activeIndex - 1);
    },
    showNext() {
      this.switchRecord(this.activeIndex + 1);
    },
    editActiveRecord() {
      const record = this.activeRecord;
      this.visible = false;
      this.$emit('edit', record);
    },
    deleteActiveRecord() {
      const record = this.activeRecord;
      this.visible = false;
      this.$emit('delete', record);
    },
    notifyActiveRow() {
      this.$emit('active-row-change', this.activeRecord);
    },
    handleClosed() {
      this.requestSequence += 1;
      this.loading = false;
      this.$emit('active-row-change', null);
    },
    formatTime(value) {
      return value ? String(value).replace('T', ' ') : '';
    }
  }
};
</script>

<style scoped>
.task-screenshot-viewer {
  display: inline-block;
}

.drawer-heading {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 5px;
  color: #303133;
}

.drawer-heading strong {
  font-size: 18px;
}

.drawer-heading span {
  color: #909399;
  font-size: 13px;
}

.record-context {
  display: grid;
  grid-template-columns: minmax(100px, 0.8fr) minmax(160px, 1.6fr) minmax(100px, 0.8fr);
  gap: 12px;
  padding: 14px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #f8f9fb;
}

.context-item {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 6px;
}

.context-item > span {
  color: #909399;
  font-size: 12px;
}

.context-item > strong {
  overflow-wrap: anywhere;
  color: #303133;
  font-size: 14px;
}

.context-item .el-tag {
  width: fit-content;
}

.shared-material-tip {
  margin-top: 12px;
}

.viewer-content {
  min-height: 220px;
  padding-top: 8px;
}

.viewer-item {
  padding: 18px 0;
  border-bottom: 1px solid #dcdfe6;
}

.viewer-item:last-child {
  border-bottom: 0;
}

.viewer-item-title {
  margin-bottom: 14px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.viewer-item-body {
  display: flex;
  align-items: center;
  gap: 22px;
}

.viewer-item-info {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  font-size: 14px;
}

.viewer-file-name {
  overflow-wrap: anywhere;
  color: #606266;
  font-size: 15px;
}

.viewer-upload-time {
  color: #909399;
}

.drawer-footer {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.record-navigation,
.audit-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.record-navigation > span {
  min-width: 52px;
  color: #606266;
  text-align: center;
}

.audit-actions .el-button {
  min-width: 112px;
}

@media (max-width: 600px) {
  .record-context {
    grid-template-columns: 1fr 1fr;
  }

  .context-item-wide {
    grid-column: 1 / -1;
    grid-row: 2;
  }

  .viewer-item-body {
    align-items: flex-start;
    flex-direction: column;
  }

  .record-navigation {
    flex-wrap: wrap;
  }
}
</style>
