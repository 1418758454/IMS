import request from '@/utils/request';

/**
 * 获取研究生理论课列表（分页+年份筛选）
 * @param {Object} params - 查询参数（与本科一致）
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @param {number} params.year - 授课年份（必填）
 * @returns {Promise} - 响应数据（结构与本科一致）
 */
export function getGraduateTheoryCourseList(params) {
  return request({
    url: '/manage/teaching/graduateTheoryCourse/list', // 修改模块标识
    method: 'get',
    params: params
  });
}

/**
 * 新增研究生理论课
 * @param {Object} data - 课程表单数据（与本科字段完全一致）
 * @returns {Promise} - 响应数据
 */
export function addGraduateTheoryCourse(data) {
  return request({
    url: '/manage/teaching/graduateTheoryCourse/add', // 修改模块标识
    method: 'post',
    data: data
  });
}

/**
 * 更新研究生理论课
 * @param {Object} data - 课程表单数据（含ID）
 * @returns {Promise} - 响应数据
 */
export function updateGraduateTheoryCourse(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/teaching/graduateTheoryCourse/update', // 修改模块标识
    method: 'put',
    data: data
  });
}

/**
 * 批量删除研究生理论课
 * @param {Array<number>} ids - 课程ID数组
 * @returns {Promise} - 响应数据
 */
export function deleteGraduateTheoryCourse(ids, config) {
  return request({
    url: '/manage/teaching/graduateTheoryCourse/delete', // 修改模块标识
    method: 'delete',
    data: ids,
    ...config
  });
}
