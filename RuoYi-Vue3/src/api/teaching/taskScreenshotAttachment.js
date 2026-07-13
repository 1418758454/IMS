import request from '@/utils/request';

export function listTaskScreenshotAttachments(params) {
  return request({
    url: '/manage/teaching/task-screenshot/list',
    method: 'get',
    params
  });
}

export function uploadTaskScreenshotAttachment({ file, year, moduleType }) {
  const data = new FormData();
  data.append('file', file);
  data.append('year', year);
  data.append('moduleType', moduleType);

  return request({
    url: '/manage/teaching/task-screenshot/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data',
      repeatSubmit: false
    },
    timeout: 120000
  });
}

export function deleteTaskScreenshotAttachment(id) {
  return request({
    url: `/manage/teaching/task-screenshot/${id}`,
    method: 'delete'
  });
}
