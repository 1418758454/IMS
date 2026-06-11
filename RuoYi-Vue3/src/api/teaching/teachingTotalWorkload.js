// @/api/teaching/teachingTotalWorkload.js
import request from '@/utils/request'

/**
 * 获取教学工作量汇总数据
 * @param {Object} params 查询参数，包括year
 * @returns 教学工作量汇总数据
 */
export function getTeachingTotalWorkload(params) {
  return request({
    url: '/manage/teaching/totalWorkload/getTotalWorkload', 
    method: 'get',
    params
  })
}