<template>
  <el-button link type="primary" icon="Document" @click="openViewer">查看任务截图</el-button>

  <el-dialog v-model="visible" title="个人教学任务截图（PDF）" width="560px">
    <div v-loading="loading" class="viewer-content">
      <el-empty v-if="!loading && attachments.length === 0" description="暂未上传任务截图" />
      <div v-for="attachment in attachments" :key="attachment.id" class="viewer-item">
        <el-link :href="attachment.fileUrl" type="primary" target="_blank">
          {{ attachment.fileName }}
        </el-link>
        <span>{{ formatTime(attachment.createTime) }}</span>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { ElMessage } from 'element-plus';
import { listTaskScreenshotAttachments } from '@/api/teaching/taskScreenshotAttachment';

export default {
  name: 'TeachingTaskScreenshotViewer',
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
    }
  },
  data() {
    return {
      visible: false,
      loading: false,
      attachments: []
    };
  },
  methods: {
    async openViewer() {
      this.visible = true;
      this.loading = true;
      try {
        const response = await listTaskScreenshotAttachments({
          userId: this.userId,
          year: this.year,
          moduleType: this.moduleType
        });
        this.attachments = Array.isArray(response.data) ? response.data : [];
      } catch (error) {
        this.attachments = [];
        ElMessage.error(error?.msg || '获取教学任务截图失败');
      } finally {
        this.loading = false;
      }
    },
    formatTime(value) {
      return value ? String(value).replace('T', ' ') : '';
    }
  }
};
</script>

<style scoped>
.viewer-content {
  min-height: 110px;
}

.viewer-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
  color: #909399;
  font-size: 13px;
}
</style>
