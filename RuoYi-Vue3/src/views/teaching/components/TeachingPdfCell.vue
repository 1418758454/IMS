<template>
  <div class="teaching-pdf-cell">
    <el-upload
      v-if="row.isEditing"
      class="teaching-pdf-upload"
      :auto-upload="false"
      :limit="1"
      accept=".pdf"
      :file-list="row._pdfFileList || []"
      :on-change="handleChange"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
    >
      <el-button size="small" type="primary" icon="Upload">选择PDF</el-button>
      <template #tip>
        <div class="el-upload__tip">仅支持单个PDF，不超过100MB</div>
      </template>
    </el-upload>

    <el-link
      v-if="row.pdfUrl"
      class="pdf-link"
      type="primary"
      :href="row.pdfUrl"
      target="_blank"
    >
      查看PDF
    </el-link>
    <span v-else-if="!row.isEditing">-</span>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';

export default {
  name: 'TeachingPdfCell',
  props: {
    row: {
      type: Object,
      required: true
    }
  },
  methods: {
    showPdfMessage(message, type = 'warning') {
      ElMessage({
        message,
        type,
        customClass: 'pdf-center-message'
      });
    },
    handleChange(file, fileList) {
      const rawFile = file.raw;
      const isPdf = rawFile && (rawFile.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf'));
      const isLt100M = rawFile && rawFile.size / 1024 / 1024 < 100;

      if (!isPdf) {
        this.showPdfMessage('仅支持PDF格式文件', 'error');
        this.clearFile();
        return;
      }
      if (!isLt100M) {
        this.showPdfMessage('文件大小不能超过100MB', 'error');
        this.clearFile();
        return;
      }

      this.row._pdfFile = rawFile;
      this.row._pdfFileList = fileList.slice(-1);
    },
    handleRemove() {
      this.clearFile();
    },
    handleExceed(files) {
      this.showPdfMessage('只能上传一个PDF文件，请先移除当前文件');
    },
    clearFile() {
      this.row._pdfFile = null;
      this.row._pdfFileList = [];
    }
  }
};
</script>

<style scoped>
.teaching-pdf-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  min-width: 132px;
}

.teaching-pdf-upload {
  width: 100%;
}

.el-upload__tip {
  margin-top: 4px;
  color: #909399;
  line-height: 1.3;
}

.pdf-link {
  font-size: 13px;
}
</style>
