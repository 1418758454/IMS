import request from '@/utils/request';

/**
 * 毕业论文模块API接口（与实验课模块格式完全一致）
 */

// 1. 获取毕业论文列表（分页+条件查询）
export function getThesisList(params) {
  return request({
    url: '/manage/teaching/thesisCourse/list',
    method: 'get',
    // params: {
    //   pageNum: params.pageNum || 1, // 默认第1页
    //   pageSize: params.pageSize || 1000, // 默认查询全部（表格无分页）
    //   year: params.year, // 授课年份（必传）
    //   userId: params.userId // 当前登录用户ID（后端通过SecurityUtils获取时可省略）
    // }
    params: params
  });
}

// 2. 新增毕业论文记录
export function addThesis(data) {
  return request({
    url: '/manage/teaching/thesisCourse/add',
    method: 'post',
    // data: {
    //   thesisCount: data.thesisCount, // 指导完成毕业论文数（必传）
    //   studentCount: data.studentCount, // 同时指导人数（必传）
    //   isPassInspection: data.isPassInspection, // 是否通过抽查（必传：pass/fail）
    //   workload: data.workload, // 工作量（前端计算后传递）
    //   year: data.year, // 授课年份（必传）
    //   userId: data.userId // 当前用户ID（后端通过SecurityUtils获取时可省略）
    // }
    data: data
  });
}

// 3. 更新毕业论文记录
export function updateThesis(data, auditEdit = false) {
  return request({
    params: { auditEdit },
    url: '/manage/teaching/thesisCourse/update',
    method: 'put',
    data: data
  });
}


// 4. 删除毕业论文记录（支持批量删除）
// export function deleteThesis(params) {
//   return request({
//     url: '/manage/teaching/thesisCourse/delete',
//     method: 'delete',
//     params: {
//       ids: params.ids.join(','), // 记录ID数组（逗号分隔字符串）
//       year: params.year // 授课年份（用于更新总工作量）
//     }
//   });
// }
export function deleteThesis(ids,config) {
  return request({
    url: '/manage/teaching/thesisCourse/delete',
    method: 'delete',
    data: ids,
    ...config // DELETE请求批量删除时，数据通过data传递（与科研API保持一致）
  });
}
