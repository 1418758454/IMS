export const EVIDENCE_FILE_ACCEPT = '.pdf,.jpg,.jpeg,.png';
export const EVIDENCE_FILE_EXTENSIONS = ['pdf', 'jpg', 'jpeg', 'png'];
export const EVIDENCE_FILE_MAX_SIZE = 100 * 1024 * 1024;

export function getFileExtension(fileOrUrl) {
  const value = typeof fileOrUrl === 'string'
    ? fileOrUrl.split('?')[0]
    : fileOrUrl?.name || '';
  const name = value.split('/').pop() || '';
  const parts = name.split('.');
  return parts.length > 1 ? parts.pop().toLowerCase() : '';
}

export function isImageEvidenceFile(fileOrUrl) {
  return ['jpg', 'jpeg', 'png'].includes(getFileExtension(fileOrUrl));
}

export function isSupportedEvidenceFile(file) {
  const extension = getFileExtension(file);
  const mimeType = file?.type || '';
  return EVIDENCE_FILE_EXTENSIONS.includes(extension)
    && (extension !== 'pdf' || mimeType === '' || mimeType === 'application/pdf')
    && (!['jpg', 'jpeg', 'png'].includes(extension)
      || mimeType === ''
      || mimeType.startsWith('image/'));
}

export function validateEvidenceFile(file) {
  if (!isSupportedEvidenceFile(file)) {
    return '仅支持PDF、JPG、JPEG、PNG格式文件';
  }
  if (file.size > EVIDENCE_FILE_MAX_SIZE) {
    return '文件大小不能超过100MB';
  }
  return '';
}
