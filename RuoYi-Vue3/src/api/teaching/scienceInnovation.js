import request from '@/utils/request';

/**
 * 科技创新项目 API
 * 与实验课模块API格式完全一致，仅路径和模块名调整
 */

// 1. 获取科技创新项目列表（分页+年份筛选）
export function getScienceInnovationList(params) {
  return request({
    url: '/manage/teaching/scienceInnovation/list', // 后端接口路径，与Controller层对应
    method: 'get',
    params // 分页参数：pageNum/pageSize/year
  });
}

// 2. 新增科技创新项目
export function addScienceInnovation(data) {
  return request({
    url: '/manage/teaching/scienceInnovation/add',
    method: 'post',
    data // 表单数据：projectName/endYear/projectLevel等
  });
}

// 3. 更新科技创新项目
export function updateScienceInnovation(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/teaching/scienceInnovation/update',
    method: 'put',
    data // 含id的完整表单数据
  });
}

// 4. 删除科技创新项目（支持单个删除，批量删除需后端适配）
export function deleteScienceInnovation(ids,config) {
  return request({
    url: '/manage/teaching/scienceInnovation/delete',
    method: 'delete',
    data: ids,
    ...config // DELETE请求批量删除时，数据通过data传递（与科研API保持一致）
  });
}
