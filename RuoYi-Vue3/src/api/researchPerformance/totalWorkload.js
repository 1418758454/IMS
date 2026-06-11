// 在适当的位置添加API调用函数
import request from '@/utils/request'

// 保存年度工作量汇总数据
export function saveYearWorkload(data) {
  return request({
    url: '/manage/research/totalWorkload/save',
    method: 'post',
    data: data
  })
}

// 获取年度工作量汇总数据
export function getYearWorkloadSummary(params) {
  return request({
    url: '/manage/research/totalWorkload/getYearWorkloadSummary',
    method: 'get',
    params: params
  })
}

/**
 * 获取科研工作量汇总数据
 * @param {Object} params 查询参数，包括year
 * @returns 科研工作量汇总数据
 */
export function getResearchTotalWorkload(params) {
  return request({
    url: '/manage/research/totalWorkload/getTotalWorkload', 
    method: 'get',
    params
  })
}