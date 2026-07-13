import request from '@/utils/request'; // 导入通用请求工具

/**
 * 获取理论课列表（分页+年份筛选）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @param {number} params.year - 授课年份（必填，如2024）
 * @returns {Promise} - 响应数据（{ code: 200, data: { records: [], total: 0, size: 10, current: 1 } }）
 */
export function getTheoryCourseList(params) {
  return request({
    url: '/manage/teaching/theoryCourse/list',
    method: 'get',
    params: params // GET请求参数通过params传递
  });
}

/**
 * 新增理论课
 * @param {Object} data - 课程表单数据
 * @param {string} data.courseName - 课程名称（必填，如"高等数学"）
 * @param {string} data.studentClass - 授课学生（必填，如"2023级计算机1班"）
 * @param {string} data.courseType - 课程类型（必填，如"compulsory"对应必修课）
 * @param {number} data.theoryHours - 理论学时（必填，非负整数，如64）
 * @param {number} data.studentCount - 学生人数（必填，非负整数，如50）
 * @param {number} data.workload - 工作量（必填，如32.5）
 * @param {number} data.year - 授课年份（必填，如2024）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "新增成功", data: { id: 1, ... } }）
 */
export function addTheoryCourse(data) {
  return request({
    url: '/manage/teaching/theoryCourse/add',
    method: 'post',
    data: data // GET请求参数通过params传递
  });
}

/**
 * 更新理论课
 * @param {Object} data - 课程表单数据（含ID）
 * @param {number} data.id - 课程ID（必填，如1）
 * @param {string} data.courseName - 课程名称（必填）
 * @param {string} data.studentClass - 授课学生（必填）
 * @param {string} data.courseType - 课程类型（必填）
 * @param {number} data.theoryHours - 理论学时（必填）
 * @param {number} data.studentCount - 学生人数（必填）
 * @param {number} data.workload - 工作量（必填）
 * @param {number} data.year - 授课年份（必填）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "更新成功", data: { id: 1, ... } }）
 */
export function updateTheoryCourse(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/teaching/theoryCourse/update',
    method: 'put', // 遵循RESTful规范，更新用PUT方法
    data: data
  });
}

/**
 * 批量删除理论课
 * @param {Array<number>} ids - 课程ID数组（必填，如 [1, 2, 3]）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "删除成功" }）
 */
export function deleteTheoryCourse(ids,config) {
  return request({
    url: '/manage/teaching/theoryCourse/delete',
    method: 'delete',
    data: ids,
    ...config // DELETE请求批量删除时，数据通过data传递（与科研API保持一致）
  });
}
