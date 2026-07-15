<template>
  <div class="teaching-pdf-cell">
    <el-upload
      v-if="row.isEditing"
      v-evidence-file-opening
      class="teaching-pdf-upload"
      :auto-upload="false"
      :limit="1"
      accept=".pdf,.jpg,.jpeg,.png"
      :file-list="row._pdfFileList || []"
      :on-change="handleChange"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
    >
      <el-button size="small" type="primary" icon="Upload">选择证明材料</el-button>
      <template #tip>
        <div class="el-upload__tip">支持PDF、JPG、JPEG、PNG，单个文件不超过100MB</div>
      </template>
    </el-upload>

    <EvidenceFilePreview v-if="row.pdfUrl" :url="row.pdfUrl" />
    <span v-else-if="!row.isEditing">-</span>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';
import { validateEvidenceFile } from '@/utils/evidenceFile';

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
      const errorMessage = rawFile && validateEvidenceFile(rawFile);
      if (errorMessage) {
        this.showPdfMessage(errorMessage, 'error');
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
      this.showPdfMessage('只能上传一个证明材料文件，请先移除当前文件');
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
