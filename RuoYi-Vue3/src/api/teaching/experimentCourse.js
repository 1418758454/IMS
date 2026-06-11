import request from '@/utils/request';

/**
 * 获取实验课列表（分页+年份筛选）
 * @param {Object} params - { pageNum, pageSize, year }
 * @returns {Promise} 响应数据
 */
export function getExperimentCourseList(params) {
  return request({
    url: '/manage/teaching/experimentCourse/list', // 模块名替换为experimentCourse
    method: 'get',
    params: params
  });
}

/**
 * 新增实验课
 * @param {Object} data - 实验课表单数据（字段与理论课一致）
 * @returns {Promise} 响应数据
 */
export function addExperimentCourse(data) {
  return request({
    url: '/manage/teaching/experimentCourse/add',
    method: 'post',
    data: data
  });
}

/**
 * 更新实验课
 * @param {Object} data - 含ID的实验课表单数据
 * @returns {Promise} 响应数据
 */
export function updateExperimentCourse(data) {
  return request({
    url: '/manage/teaching/experimentCourse/update',
    method: 'put',
    data: data
  });
}

/**
 * 批量删除实验课
 * @param {Array<number>} ids - 实验课ID数组
 * @param {Object} config - 额外配置（如params传递年份）
 * @returns {Promise} 响应数据
 */
export function deleteExperimentCourse(ids, config) {
  return request({
    url: '/manage/teaching/experimentCourse/delete',
    method: 'delete',
    data: ids,
    ...config
  });
}