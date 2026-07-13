import request from '@/utils/request';

/**
 * 获取实践教学列表（分页+年份筛选）
 * @param {Object} params - { pageNum, pageSize, year }
 * @returns {Promise} 响应数据
 */
export function getPracticeTeachingList(params) {
  return request({
    url: '/manage/teaching/practiceTeaching/list', // 模块名替换为practiceTeaching
    method: 'get',
    params: params
  });
}

/**
 * 新增实践教学
 * @param {Object} data - 实践教学表单数据
 * @param {string} data.courseName - 课程名称
 * @param {string} data.courseType - 课程类型（internship/training/course_design）
 * @param {number} data.planDays - 计划天数
 * @param {number} data.classCount - 班级数
 * @param {number} data.year - 授课年份
 * @returns {Promise} 响应数据
 */
export function addPracticeTeaching(data) {
  return request({
    url: '/manage/teaching/practiceTeaching/add',
    method: 'post',
    data: data
  });
}

/**
 * 更新实践教学
 * @param {Object} data - 含ID的实践教学表单数据
 * @param {number} data.id - 实践教学ID
 * @param {string} data.courseName - 课程名称
 * @param {string} data.courseType - 课程类型
 * @param {number} data.planDays - 计划天数
 * @param {number} data.classCount - 班级数
 * @returns {Promise} 响应数据
 */
export function updatePracticeTeaching(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/teaching/practiceTeaching/update',
    method: 'put',
    data: data
  });
}

/**
 * 批量删除实践教学
 * @param {Array<number>} ids - 实践教学ID数组
 * @param {Object} config - 额外配置（如params传递年份）
 * @returns {Promise} 响应数据
 */
export function deletePracticeTeaching(ids, config) {
  return request({
    url: '/manage/teaching/practiceTeaching/delete',
    method: 'delete',
    data: ids,
    ...config
  });
}
