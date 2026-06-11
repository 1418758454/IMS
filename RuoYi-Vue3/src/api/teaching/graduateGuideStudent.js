import request from '@/utils/request';

/**
 * 研究生指导学生模块 API
 * 与教改项目API结构一致，仅路径和模块名调整
 */

// 1. 获取指导学生列表（分页+年份筛选）
export function getGraduateGuideStudentList(params) {
  return request({
    url: '/manage/teaching/graduateGuideStudent/list', // 模块路径：graduateStudent
    method: 'get',
    params // 分页参数：pageNum/pageSize/year
  });
}

// 2. 新增指导学生记录
export function addGraduateGuideStudent(data) {
  return request({
    url: '/manage/teaching/graduateGuideStudent/add',
    method: 'post',
    data // 表单数据：studentCount/studentType/workload/coefficient等
  });
}

// 3. 更新指导学生记录
export function updateGraduateGuideStudent(data) {
  return request({
    url: '/manage/teaching/graduateGuideStudent/update',
    method: 'put',
    data // 含id的完整表单数据
  });
}

// 4. 删除指导学生记录（支持单个/批量删除）
export function deleteGraduateGuideStudent(ids, config) {
  return request({
    url: '/manage/teaching/graduateGuideStudent/delete',
    method: 'delete',
    data: ids, // 批量删除时传递id数组
    ...config
  });
}