// src/api/statistics/teachingStatistics.js
import request from '@/utils/request'

// 获取教学统计列表
export function getTeachingStatistics(query) {
  return request({
    url: '/teaching/statistics/list',
    method: 'get',
    params: query
  })
}

// 导出教学统计数据
export function exportTeachingStatistics(data) {
  return request({
    url: '/teaching/statistics/export',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}