import request from '@/utils/request'; // 导入通用请求工具

/**
 * 获取论著列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @returns {Promise} - 响应数据（{ code: 200, data: { records: [], total: 0 } }）
 */
export function getMonographList(params) {
  return request({
    url: '/manage/research/monograph/list',
    method: 'get',
    params: params
  });
}

/**
 * 新增论著
 * @param {Object} data - 论著表单数据
 * @param {string} data.title - 论著名称（必填）
 * @param {string} data.publisher - 出版社（必填）
 * @param {string} data.publishTime - 出版时间（必填，格式：YYYY-MM-DD）
 * @param {number} data.rank - 排名（必填）
 * @param {string} [data.pdfUrl] - PDF文件URL（可选，上传后后端返回）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "新增成功" }）
 */
export function addMonograph(data) {
  return request({
    url: '/manage/research/monograph/add',
    method: 'post',
    data: data
  });
}

/**
 * 删除论著（批量删除）
 * @param {Array<number>} ids - 论著ID数组（必填，如 [1, 2, 3]）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "删除成功" }）
 */
export function deleteMonograph(ids) {
  return request({
    url: '/manage/research/monograph/delete',
    method: 'delete',
    data: ids
  });
}

/**
 * 修改论著
 * @param {Object} data - 论著表单数据
 * @param {string} data.title - 论著名称（必填）
 * @param {string} data.publisher - 出版社（必填）
 * @param {string} data.publishTime - 出版时间（必填，格式：YYYY-MM-DD）
 * @param {number} data.rank - 排名（必填）
 */

export function updateMonograph(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/research/monograph/update',
    method: 'put',
    data
  });
}
