import request from '@/utils/request'

export function exportResearchStatistics(data) {
  return request({
    url: '/manage/statistics/export/research',
    method: 'post',
    data,
    responseType: 'blob',
    timeout: 600000
  })
}

export function exportTeachingStatistics(data) {
  return request({
    url: '/manage/statistics/export/teaching',
    method: 'post',
    data,
    responseType: 'blob',
    timeout: 600000
  })
}
