package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.PracticeCourse;

import java.util.List;

/**
 * 本科教学实践服务接口
 * 继承MyBatis-Plus的IService，提供基础CRUD能力
 */
public interface PracticeCourseService extends IService<PracticeCourse> {

    /**
     * 分页查询教学实践列表（支持年份筛选）
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param userId   用户ID（当前登录教师）
     * @param year     授课年份（可选）
     * @param isAudit
     * @return 分页结果（包含教学实践列表和总条数）
     */
    IPage<PracticeCourse> getPracticeCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增教学实践
     * @param course 教学实践实体
     * @return 新增结果（true：成功，false：失败）
     */
    boolean addPracticeCourse(PracticeCourse course);

    /**
     * 批量删除教学实践
     * @param ids 教学实践ID列表
     * @return 删除结果（true：成功，false：失败）
     */
    boolean removePracticeCourseByIds(List<Long> ids);

    /**
     * 计算教学实践的工作量（复用实验课逻辑，字段替换为planDays和classCount）
     * @param userId 用户ID（用于查询职称系数）
     * @param course 教学实践实体（包含计划天数、班级数等信息）
     * @return 工作量（保留3位小数）
     */
    double countWorkload(Long userId, PracticeCourse course);

    /**
     * 计算并更新用户的教学实践总工作量（写入总工作量表的practice_course_workload字段）
     * @param userId 用户ID
     * @param year   授课年份
     * @return 总工作量
     */
    double countTotalWorkload(Long userId, Integer year);

    IPage<PracticeCourse> getPracticeCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditPracticeCourse(Long id, String status, String remark);
}