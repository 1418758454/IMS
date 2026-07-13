import request from '@/utils/request'; // 导入通用请求工具

/**
 * 1. 获取课题列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @returns {Promise} - 响应数据（{ code: 200, data: { records: [], total: 0 } }）
 */
export function getSubjectList(params) {
  return request({
    url: '/manage/research/subject/list', // 后端课题列表接口URL
    method: 'get',
    params: params // URL参数（分页+搜索）
  });
}

/**
 * 2. 新增课题
 * @param {Object} data - 课题表单数据（与新增弹窗字段对应）
 * @param {string} data.projectName - 项目名称（必填）
 * @param {string} data.subjectType - 课题类型（必填，如“国家自然基金”）
 * @param {Array} data.executeTime - 执行时间范围（必填，如 ["2023-01", "2025-12"]）
 * @param {number} data.rank - 排名（必填）
 * @param {number} data.coefficient - 系数（必填，如1.0/0.8/0.5）
 * @param {string} [data.pdfUrl] - PDF文件URL（可选，上传后后端返回）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "新增成功" }）
 */
export function addSubject(data) {
  return request({
    url: '/manage/research/subject/add', // 后端课题新增接口URL
    method: 'post',
    data: data // 请求体数据（表单字段）
  });
}

/**
 * 3. 删除课题（批量删除）
 * @param {Array<number>} ids - 课题ID数组（必填，如 [1, 2, 3]）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "删除成功" }）
 */
export function deleteSubject(ids) {
  return request({
    url: '/manage/research/subject/delete', // 后端课题删除接口URL
    method: 'delete',
    data: ids // 请求体数据（ID数组）
  });
}

/**
 * 4. 修改课题
 * @param {Object} data - 课题表单数据（含ID）
 * @param {number} data.id - 课题ID（必填）
 * @param {string} data.projectName - 项目名称（必填）
 * @param {string} data.subjectType - 课题类型（必填，如“国家自然基金”）
 * @param {Array} data.executeTime - 执行时间范围
 */
export function updateSubject(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/research/subject/update',
    method: 'put',
    data // 含id的完整表单数据
  });
}
