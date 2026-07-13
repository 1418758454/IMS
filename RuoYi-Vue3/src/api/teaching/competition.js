import request from '@/utils/request';

/**
 * 学科竞赛模块 API（与科技创新模块接口格式完全一致，仅路径和模块名调整）
 */

// 1. 获取学科竞赛列表（分页+年份筛选）
export function getCompetitionList(params) {
  return request({
    url: '/manage/teaching/competition/list', // 接口路径：competition（替换scienceInnovation）
    method: 'get',
    params // 分页参数：pageNum/pageSize/year（与科技创新模块一致）
  });
}

// 2. 新增学科竞赛项目
export function addCompetition(data) {
  return request({
    url: '/manage/teaching/competition/add',
    method: 'post',
    data // 表单数据：awardProjectName/competitionLevel/awardLevel/workload等
  });
}

// 3. 更新学科竞赛项目
export function updateCompetition(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/teaching/competition/update',
    method: 'put',
    data // 含id的完整表单数据（与新增字段一致）
  });
}

// 4. 删除学科竞赛项目（支持单个/批量删除）
export function deleteCompetition(ids, config) {
  return request({
    url: '/manage/teaching/competition/delete',
    method: 'delete',
    data: ids, // 删除id列表（单个id直接传值，批量传数组）
    ...config // 扩展参数（如请求头等，与科技创新模块一致）
  });
}
