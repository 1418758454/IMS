import request from '@/utils/request'; // 导入通用请求工具

/**
 * 获取获奖列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @returns {Promise} - 响应数据（{ code: 200, data: { records: [], total: 0 } }）
 */
export function getAwardList(params) {
  return request({
    url: '/manage/research/award/list',
    method: 'get',
    params: params
  });
}

/**
 * 新增获奖
 * @param {Object} data - 获奖表单数据
 * @param {string} data.name - 获奖名称（必填）
 * @param {string} data.organizer - 颁奖单位（必填）
 * @param {string} data.awardTime - 获奖时间（必填，格式：YYYY-MM-DD）
 * @param {string} data.level - 奖励级别（必填，如"省部级"）
 * @param {number} data.rank - 排名（必填）
 * @param {string} [data.pdfUrl] - PDF文件URL（可选，上传后后端返回）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "新增成功" }）
 */
export function addAward(data) {
  return request({
    url: '/manage/research/award/add',
    method: 'post',
    data: data
  });
}

/**
 * 删除获奖（批量删除）
 * @param {Array<number>} ids - 获奖ID数组（必填，如 [1, 2, 3]）
 * @returns {Promise} - 响应数据（{ code: 200, msg: "删除成功" }）
 */
export function deleteAward(ids) {
  return request({
    url: '/manage/research/award/delete',
    method: 'delete',
    data: ids
  });
}

/**
 * 修改获奖
 * @param {Object} data - 获奖表单数据
 * @param {string} data.name - 获奖名称（必填）
 * @param {string} data.organizer - 颁奖单位（必填）
 * @param {string} data.awardTime - 获奖时间（必填，格式：YYYY-MM-DD）
 * @param {string} data.level - 奖励级别（必填，如"省部级"）
 */

export function updateAward(data) {
  return request({
    url: '/manage/research/award/update',
    method: 'put',
    data
  });
}