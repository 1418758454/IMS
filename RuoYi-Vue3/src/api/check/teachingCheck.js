import request from '@/utils/request'; // 导入通用请求工具

/**
 * 调用理论课模块审核接口
 */
export function auditUndergraduateTheory(data) {
  return request({
    url: '/manage/teaching/theoryCourse/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用实验课模块审核接口
 */
export function auditUndergraduateExperiment(data) {
  return request({
    url: '/manage/teaching/experimentCourse/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用实践教学模块审核接口
 */
export function auditPracticeTeaching(data) {
  return request({
    url: '/manage/teaching/practiceTeaching/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用毕业论文模块审核接口
 */
export function auditThesis(data) {
  return request({
    url: '/manage/teaching/thesisCourse/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用科技创新模块审核接口
 */
export function auditScienceInnovation(data) {
  return request({
    url: '/manage/teaching/scienceInnovation/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用学科竞赛模块审核接口
 */
export function auditCompetition(data) {
  return request({
    url: '/manage/teaching/competition/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用出版教材模块审核接口
 */
export function auditTextbook(data) {
  return request({
    url: '/manage/teaching/textbook/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用教改项目模块审核接口
 */
export function auditEducationReform(data) {
  return request({
    url: '/manage/teaching/educationReform/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用教改论文模块审核接口
 */
export function auditEducationReformPaper(data) {
  return request({
    url: '/manage/teaching/educationReformPaper/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用监考模块审核接口
 */
export function auditProctor(data) {
  return request({
    url: '/manage/teaching/proctor/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用研究生理论课模块审核接口
 */
export function auditGraduateTheoryCourse(data) {
  return request({
    url: '/manage/teaching/graduateTheoryCourse/audit',
    method: 'put',
    data: data
  })
}

/**
 * 调用研究生指导学生模块审核接口
 */
export function auditGraduateGuideStudent(data) {
  return request({
    url: '/manage/teaching/graduateGuideStudent/audit',
    method: 'put',
    data: data
  })
}