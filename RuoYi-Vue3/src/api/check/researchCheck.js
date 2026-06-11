import request from '@/utils/request'; // 导入通用请求工具

/**
 * 根据部门ID获取用户列表
 * @param {Object} params - 查询参数
 * @param {number} params.deptId - 部门ID（必填）
 * @returns {Promise} - 响应数据（{ code: 200, data: [] }）
 */
export function getUserListByDept(params) {
  return request({
    url: '/system/user/listByDept', // 后端根据部门获取用户列表接口URL
    method: 'get',
    params: params // URL参数
  });
}

/**
 * 获取所有用户列表（可选，如果需要的话）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @returns {Promise} - 响应数据（{ code: 200, data: { records: [], total: 0 } }）
 */
export function getUserList(params) {
  return request({
    url: '/system/user/list', // 后端用户列表接口URL
    method: 'get',
    params: params // URL参数（分页+搜索）
  });
}

/**
 * 调用课题模块审核接口
 */
export function auditSubject(data) {
  return request({
    url: '/manage/research/subject/audit',
    method: 'put',
    data: data
  })
}


/**
 * 调用论文模块审核接口
 */
export function auditPaper(data) {
  return request({
    url: '/manage/research/paper/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用专著模块审核接口
 */
export function auditMonograph(data) {
  return request({
    url: '/manage/research/monograph/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用获奖模块审核接口
 */
export function auditAward(data) {
  return request({
    url: '/manage/research/award/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用专利模块审核接口
 */
export function auditPatent(data) {
  return request({
    url: '/manage/research/patent/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用软著模块审核接口
 */
export function auditSoftware(data) {
  return request({
    url: '/manage/research/software/audit',
    method: 'put',
    data: data
  })
}

