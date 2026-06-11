import request from '@/utils/request'; // 导入通用请求工具

/**
 * 获取专利列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @returns {Promise} - 响应数据（{ code: 200, data: { records: [], total: 0 } }）
 */
export function getPatentList(params) {
  return request({
    url: '/manage/research/patent/list',
    method: 'get',
    params: params
  });
}

/**
 * 新增专利
 * @param {Object} data - 专利表单数据
 * @param {string} data.name - 专利名称（必填）
 * @param {string} data.type - 专利类型（必填，如"发明专利"）
 * @param {string} data.applyTime - 申请时间（必填，格式：YYYY-MM-DD）
 * @param {string} data.authorizeTime - 授权时间（必填，格式：YYYY-MM-DD）
 * @param {number} data.rank - 排名（必填）
 * @param {string} [data.pdfUrl] - PDF文件URL（可选，上传后后端返回）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "新增成功" }）
 */
export function addPatent(data) {
  return request({
    url: '/manage/research/patent/add',
    method: 'post',
    data: data
  });
}

/**
 * 删除专利（批量删除）
 * @param {Array<number>} ids - 专利ID数组（必填，如 [1, 2, 3]）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "删除成功" }）
 */
export function deletePatent(ids) {
  return request({
    url: '/manage/research/patent/delete',
    method: 'delete',
    data: ids
  });
}

/**
 * 修改专利
 * @param {Object} data - 专利表单数据
 * @param {string} data.name - 专利名称（必填）
 * @param {string} data.type - 专利类型（必填，如"发明专利"）
 * @param {string} data.applyTime - 申请时间（必填，格式：YYYY-MM-DD）
 * @param {string} data.authorizeTime - 授权时间（必填，格式：YYYY-MM-
 */
export function updatePatent(data) {
  return request({
    url: '/manage/research/patent/update',
    method: 'put',
    data
  });
}