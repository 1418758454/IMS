import request from '@/utils/request'
 
/**
 * 用户注册接口
 * @param {Object} data - 注册表单数据（包含基本信息字段）
 * @returns {Promise} - 返回Axios请求Promise对象
 */
export function registerUser(data) {
  return request({
    url: '/manage/information/register/save', // 后端注册接口URL（需与后端一致）
    method: 'post', // 请求方法：POST
    data: data ,// 请求体数据（表单字段）
  })
}

/**
 * 获取待审核注册信息列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 当前页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @returns {Promise<Object>} 分页结果，格式：{ rows: [], total: 0 }
 */
export function getReviewList(params) {
  return request({
    url: '/manage/information/register/pendingList', // 后端待审核列表接口路径（需后端新增）
    method: 'get',
    params: params // URL参数：pageNum、pageSize
  });
}
 
/**
 * 审核注册信息（通过/拒绝）
 * @param {Object} data - 审核参数
 * @param {number} data.id - 注册信息ID（对应数据库表主键，需后端返回）
 * @param {number} data.status - 审核状态（1=通过，2=拒绝）
 * @returns {Promise<Object>} 审核结果，格式：{ code: 200, msg: "操作成功" }
 */
export function reviewRegistration(data) {
  return request({
    url: '/manage/information/register/review', // 后端审核操作接口路径（需后端新增）
    method: 'post',
    data: data // 请求体参数：id、status、rejectReason
  });
}

/**
 * 更新注册信息（修改已提交的注册数据）
 * @param {Object} data - 更新参数（需包含唯一标识 `userId` 及待修改字段）
 * @param {string} data.userId - 工号（唯一标识，必填，不可修改）
 * @param {string} [data.name] - 姓名（选填，需修改时传递）
 * @param {string} [data.gender] - 性别（选填，需修改时传递，值为 '男' 或 '女'）
 * @param {string} [data.birthDate] - 出生年月（选填，需修改时传递，格式 'yyyy-MM-dd'）
 * @param {string} [data.politicalStatus] - 政治面貌（选填，需修改时传递）
 * @param {string} [data.title] - 职称（选填，需修改时传递）
 * @param {string} [data.currentPosition] - 现有岗位（选填，需修改时传递）
 * @param {string} [data.highestEducation] - 最高学历（选填，需修改时传递）
 * @param {string} [data.phone] - 电话（选填，需修改时传递，11位手机号）
 * @returns {Promise<Object>} - 后端响应结果，格式：{ code: 200, msg: "修改成功" }
 */
export function updateRegistration(data) {
  return request({
    url: '/manage/information/register/update', // 后端更新接口URL（需与后端一致）
    method: 'put', 
    data: data // 请求体参数（包含待修改的注册信息字段）
  });
}