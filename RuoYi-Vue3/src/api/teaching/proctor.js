import request from '@/utils/request';

/**
 * 监考模块 API
 * 与科技创新模块API格式完全一致，仅路径和模块名调整
 */

// 1. 获取监考记录列表（分页+年份筛选）
export function getProctorList(params) {
  return request({
    url: '/manage/teaching/proctor/list', // 后端接口路径，与Controller层对应
    method: 'get',
    params // 分页参数：pageNum/pageSize/year
  });
}

// 2. 新增监考记录
export function addProctor(data) {
  return request({
    url: '/manage/teaching/proctor/add',
    method: 'post',
    data // 表单数据：type/examCount等
  });
}

// 3. 更新监考记录
export function updateProctor(data) {
  return request({
    url: '/manage/teaching/proctor/update',
    method: 'put',
    data // 含id的完整表单数据
  });
}

// 4. 删除监考记录（支持单个删除，批量删除需后端适配）
export function deleteProctor(ids, config) {
  return request({
    url: '/manage/teaching/proctor/delete',
    method: 'delete',
    data: ids,
    ...config // DELETE请求批量删除时，数据通过data传递
  });
}