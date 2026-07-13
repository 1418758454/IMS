import request from '@/utils/request';

/**
 * 教改论文模块 API
 * 与科技创新模块格式完全一致，仅调整路径和模块名
 */

// 1. 获取教改论文列表（分页+年份筛选）
export function getEducationReformPaperList(params) {
  return request({
    url: '/manage/teaching/educationReformPaper/list', // 路径调整为教改论文模块
    method: 'get',
    params // 分页参数：pageNum/pageSize/year（与科技创新一致）
  });
}

// 2. 新增教改论文
export function addEducationReformPaper(data) {
  return request({
    url: '/manage/teaching/educationReformPaper/add',
    method: 'post',
    data // 表单数据：paperName/publishDate/level/workload等
  });
}

// 3. 更新教改论文
export function updateEducationReformPaper(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/teaching/educationReformPaper/update',
    method: 'put',
    data // 含id的完整表单数据
  });
}

// 4. 删除教改论文（支持单个/批量删除）
export function deleteEducationReformPaper(ids, config) {
  return request({
    url: '/manage/teaching/educationReformPaper/delete',
    method: 'delete',
    data: ids,
    ...config // DELETE请求批量删除逻辑（复用科技创新模块）
  });
}
