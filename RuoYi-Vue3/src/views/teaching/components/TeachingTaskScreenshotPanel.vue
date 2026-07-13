<template>
  <section class="task-screenshot-panel">
    <div class="task-screenshot-heading">
      <div>
        <strong>个人教学任务截图（PDF）</strong>
        <span v-if="hasAttachment" class="attachment-count">已上传 {{ attachments.length }} 份</span>
        <span v-else class="attachment-missing">尚未上传</span>
      </div>
      <el-upload
        v-if="editable"
        :auto-upload="false"
        :show-file-list="false"
        accept=".pdf"
        :disabled="uploading"
        :on-change="handleFileChange"
      >
        <el-button type="primary" size="small" icon="Upload" :loading="uploading">上传截图</el-button>
      </el-upload>
    </div>

    <el-alert
      v-if="editable && !hasAttachment"
      title="请先上传个人教学任务截图（PDF）后再新增课程"
      type="warning"
      :closable="false"
      show-icon
    />

    <div v-loading="loading" class="attachment-list">
      <div v-for="attachment in attachments" :key="attachment.id" class="attachment-item">
        <el-link :href="attachment.fileUrl" type="primary" target="_blank" :underline="false">
          {{ attachment.fileName }}
        </el-link>
        <span class="attachment-time">{{ formatTime(attachment.createTime) }}</span>
        <el-button
          v-if="editable"
          link
          type="danger"
          size="small"
          icon="Delete"
          @click="removeAttachment(attachment)"
        >
          删除
        </el-button>
      </div>
      <span v-if="!loading && attachments.length === 0" class="attachment-empty">上传后可新增本模块课程</span>
    </div>
  </section>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  deleteTaskScreenshotAttachment,
  listTaskScreenshotAttachments,
  uploadTaskScreenshotAttachment
} from '@/api/teaching/taskScreenshotAttachment';

const MAX_FILE_SIZE = 100 * 1024 * 1024;

export default {
  name: 'TeachingTaskScreenshotPanel',
  props: {
    year: {
      type: Number,
      required: true
    },
    moduleType: {
      type: String,
      required: true
    },
    editable: {
      type: Boolean,
      default: true
    }
  },
  emits: ['attachment-state-change'],
  data() {
    return {
      attachments: [],
      loading: false,
      uploading: false
    };
  },
  computed: {
    hasAttachment() {
      return this.attachments.length > 0;
    }
  },
  watch: {
    year: {
      immediate: true,
      handler() {
        this.loadAttachments();
      }
    },
    moduleType() {
      this.loadAttachments();
    }
  },
  methods: {
    async loadAttachments() {
      if (!this.year || !this.moduleType) {
        this.attachments = [];
        this.emitAttachmentState();
        return;
      }

      this.loading = true;
      try {
        const response = await listTaskScreenshotAttachments({
          year: this.year,
          moduleType: this.moduleType
        });
        this.attachments = Array.isArray(response.data) ? response.data : [];
      } catch (error) {
        this.attachments = [];
        ElMessage.error(error?.msg || '获取教学任务截图失败');
      } finally {
        this.loading = false;
        this.emitAttachmentState();
      }
    },
    async handleFileChange(file) {
      const rawFile = file.raw;
      const isPdf = rawFile && (rawFile.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf'));
      if (!isPdf) {
        ElMessage.error('仅支持PDF格式文件');
        return;
      }
      if (rawFile.size > MAX_FILE_SIZE) {
        ElMessage.error('文件大小不能超过100MB');
        return;
      }

      this.uploading = true;
      try {
        await uploadTaskScreenshotAttachment({
          file: rawFile,
          year: this.year,
          moduleType: this.moduleType
        });
        ElMessage.success('教学任务截图上传成功');
        await this.loadAttachments();
      } catch (error) {
        ElMessage.error(error?.msg || '教学任务截图上传失败');
      } finally {
        this.uploading = false;
      }
    },
    async removeAttachment(attachment) {
      try {
        await ElMessageBox.confirm(
          `确定删除“${attachment.fileName}”吗？删除后可能无法新增课程。`,
          '删除教学任务截图',
          {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );
        await deleteTaskScreenshotAttachment(attachment.id);
        ElMessage.success('教学任务截图已删除');
        await this.loadAttachments();
      } catch (error) {
        if (error !== 'cancel' && error !== 'close') {
          ElMessage.error(error?.msg || '删除教学任务截图失败');
        }
      }
    },
    emitAttachmentState() {
      this.$emit('attachment-state-change', this.hasAttachment);
    },
    formatTime(value) {
      return value ? String(value).replace('T', ' ') : '';
    }
  }
};
</script>

<style scoped>
.task-screenshot-panel {
  margin: 12px 20px;
  padding: 12px;
  border: 1px solid #dcdfe6;
  background: #fafafa;
}

.task-screenshot-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.attachment-count {
  margin-left: 10px;
  color: #409eff;
}

.attachment-missing {
  margin-left: 10px;
  color: #e6a23c;
}

.attachment-list {
  min-height: 24px;
  margin-top: 10px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 28px;
}

.attachment-time,
.attachment-empty {
  color: #909399;
  font-size: 13px;
}
</style>
