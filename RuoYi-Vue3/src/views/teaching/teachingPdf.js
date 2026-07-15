import { ElMessage } from 'element-plus';
import { uploadPdfFile } from '@/api/system/upload.js';
import { validateEvidenceFile } from '@/utils/evidenceFile';

function showPdfMessage(message, type = 'warning') {
  ElMessage({
    message,
    type,
    customClass: 'pdf-center-message'
  });
}

export async function prepareTeachingPdf(row, { required = true } = {}) {
  if (!row.pdfUrl && !row._pdfFile) {
    if (required) {
      showPdfMessage('请上传PDF或图片证明材料');
      return false;
    }
    return true;
  }

  if (row._pdfFile) {
    const errorMessage = validateEvidenceFile(row._pdfFile);
    if (errorMessage) {
      showPdfMessage(errorMessage, 'error');
      return false;
    }
    const response = await uploadPdfFile(row._pdfFile);
    row.pdfUrl = response.pdfUrl;
    showPdfMessage('证明材料上传成功', 'success');
  }

  return true;
}

export function cleanTeachingPdfFields(row) {
  delete row._pdfFile;
  delete row._pdfFileList;
}
