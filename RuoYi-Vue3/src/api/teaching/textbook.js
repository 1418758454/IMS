import request from '@/utils/request';

/**
 * 出版教材 API
 */

// 1. 获取出版教材列表
export function getTextbookList(params) {
  return request({
    url: '/manage/teaching/textbook/list',
    method: 'get',
    params
  });
}

// 2. 新增出版教材
export function addTextbook(data) {
  return request({
    url: '/manage/teaching/textbook/add',
    method: 'post',
    data
  });
}

// 3. 更新出版教材
export function updateTextbook(data) {
  return request({
    url: '/manage/teaching/textbook/update',
    method: 'put',
    data
  });
}

// 4. 删除出版教材
export function deleteTextbook(ids, config) {
  return request({
    url: '/manage/teaching/textbook/delete',
    method: 'delete',
    data: ids,
    ...config
  });
}