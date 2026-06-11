import request from '@/utils/request'; // 导入通用请求工具

/**
 * 获取软著列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @returns {Promise} - 响应数据（{ code: 200, data: { records: [], total: 0 } }）
 */
export function getSoftwareList(params) {
  return request({
    url: '/manage/research/software/list',
    method: 'get',
    params: params
  });
}

/**
 * 新增软著
 * @param {Object} data - 软著表单数据
 * @param {string} data.name - 软著名称（必填）
 * @param {string} data.applyTime - 申请时间（必填，格式：YYYY-MM-DD）
 * @param {string} data.authorizeTime - 授权时间（必填，格式：YYYY-MM-DD）
 * @param {number} data.rank - 排名（必填）
 * @param {string} [data.pdfUrl] - PDF文件URL（可选，上传后后端返回）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "新增成功" }）
 */
export function addSoftware(data) {
  return request({
    url: '/manage/research/software/add',
    method: 'post',
    data: data
  });
}

/**
 * 删除软著（批量删除）
 * @param {Array<number>} ids - 软著ID数组（必填，如 [1, 2, 3]）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "删除成功" }）
 */
export function deleteSoftware(ids) {
  return request({
    url: '/manage/research/software/delete',
    method: 'delete',
    data: ids
  });
}

/**
 * 修改软著
 * @param {Object} data - 软著表单数据
 * @param {string} data.name - 软著名称（必填）
 * @param {string} data.applyTime - 申请时间（必填，格式：YYYY-MM-DD）
 * @param {string} data.authorizeTime - 授权时间（必填，格式：YYYY-MM-DD）
 * @param {number} data.rank - 排名（必填）
 */

export function updateSoftware(data) {
  return request({
    url: '/manage/research/software/update',
    method: 'put',
    data
  });
}