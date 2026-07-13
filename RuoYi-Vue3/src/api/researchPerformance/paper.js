import request from '@/utils/request';

/**
 * 获取论文列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @returns {Promise} - 响应数据（{ code: 200, data: { records: [], total: 0 } }）
 */
export function getPaperList(params) {
  return request({
    url: '/manage/research/paper/list',
    method: 'get',
    params: params
  });
}

/**
 * 新增论文
 * @param {Object} data - 论文表单数据
 * @param {string} data.title - 论文名称（必填）
 * @param {string} data.journal - 出版刊物（必填）
 * @param {string} data.publishTime - 出版时间（必填，格式：YYYY-MM-DD）
 * @param {string} data.level - 论文级别（必填，如"SCI一区"）
 * @param {number} data.rank - 排名（必填）
 * @param {string} [data.pdfUrl] - PDF文件URL（可选，上传后后端返回）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "新增成功" }）
 */
export function addPaper(data) {
  return request({
    url: '/manage/research/paper/add',
    method: 'post',
    data: data
  });
}

/**
 * 删除论文（批量删除）
 * @param {Array<number>} ids - 论文ID数组（必填，如 [1, 2, 3]）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "删除成功" }）
 */
export function deletePaper(ids) {
  return request({
    url: '/manage/research/paper/delete',
    method: 'delete',
    data: ids
  });
}

/**
 * 修改论文
 * @param {Object} data - 论文表单数据
 * @param {string} data.title - 论文名称（必填）
 * @param {string} data.journal - 出版刊物（必填）
 * @param {string} data.publishTime - 出版时间（必填，格式：YYYY-MM-DD）
 * @param {string} data.level - 论文级别（必填，如"SCI一区"）
 */

export function updatePaper(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/research/paper/update',
    method: 'put',
    data
  });
}
