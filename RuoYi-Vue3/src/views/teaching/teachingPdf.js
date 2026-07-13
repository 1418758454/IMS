import { ElMessage } from 'element-plus';
import { uploadPdfFile } from '@/api/system/upload.js';

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
      showPdfMessage('请上传PDF证明材料');
      return false;
    }
    return true;
  }

  if (row._pdfFile) {
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
