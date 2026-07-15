// @/api/system/upload.js
import { ElMessage } from 'element-plus';
import { reactive } from 'vue';
import request from '@/utils/request'
import { validateEvidenceFile } from '@/utils/evidenceFile';


/**
 * 上传前验证证明材料格式和大小
 * @param {File} file - 上传文件对象
 * @returns {boolean} 是否通过验证
 */
export const beforePdfUpload = (file) => {
  const errorMessage = validateEvidenceFile(file);
  if (errorMessage) {
    ElMessage.error(errorMessage);
    return false;
  }

  return true;
};

/**
 * 上传成功处理函数
 * @param {Object} response - 后端响应数据
 * @param {File} file - 上传文件对象
 * @returns {string|null} 成功返回文件路径，失败返回null
 */
export function handlePdfUpload(response, file, fileList) {
  if (response.code === 200) {
    this.formData.pdfUrl = response.data.url;  // ✅ 正确读取后端返回的url字段
    ElMessage.success(`证明材料上传成功: ${file.name}`);
  } else {
    ElMessage.error(`上传失败: ${response.msg || '服务器错误'}`);
  }
}

/**
 * 手动上传文件（如需自定义上传逻辑）
 * @param {File} file - 文件对象
 * @returns {Promise<Object>} 上传请求Promise
 */
export const uploadFileToServer = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  return new Promise((resolve, reject) => {
    fetch('/api/system/upload', {
      method: 'POST',
      body: formData,
    })
      .then(res => res.json())
      .then(data => {
        if (data.code === 200) resolve(data.data);
        else reject(data.msg);
      })
      .catch(err => reject(err));
  });
};

export function uploadPdfFile(file) {
  const formData = new FormData();
  formData.append('file', file);

  return request({
    url: '/system/user/profile/uploadPdf',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
      repeatSubmit: false
    },
    timeout: 120000
  });
}

// 定义上传请求头（携带Token）
const uploadHeaders = reactive({
  // 从localStorage获取Token，根据实际存储键名调整（如Authorization、token等）
  Authorization: 'Bearer ' + localStorage.getItem('token') 
  // 若Token存储键名为Authorization，则改为：localStorage.getItem('Authorization')
});
