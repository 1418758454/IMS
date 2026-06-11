import request from '@/utils/request';

/**
 * 教改项目 API
 * 与科技创新模块API格式完全一致，仅路径和模块名调整
 */

// 1. 获取教改项目列表（分页+年份筛选）
export function getEducationReformList(params) {
  return request({
    url: '/manage/teaching/educationReform/list', // 模块路径：scienceInnovation → educationReform
    method: 'get',
    params // 分页参数：pageNum/pageSize/year（与科技创新一致）
  });
}

// 2. 新增教改项目
export function addEducationReform(data) {
  return request({
    url: '/manage/teaching/educationReform/add',
    method: 'post',
    data // 表单数据：projectName/endDate/level/workload等（适配教改项目字段）
  });
}

// 3. 更新教改项目
export function updateEducationReform(data) {
  return request({
    url: '/manage/teaching/educationReform/update',
    method: 'put',
    data // 含id的完整表单数据（与科技创新结构一致）
  });
}

// 4. 删除教改项目（支持单个/批量删除）
export function deleteEducationReform(ids, config) {
  return request({
    url: '/manage/teaching/educationReform/delete',
    method: 'delete',
    data: ids, // 批量删除时传递id数组
    ...config // 扩展配置（如请求头）
  });
}