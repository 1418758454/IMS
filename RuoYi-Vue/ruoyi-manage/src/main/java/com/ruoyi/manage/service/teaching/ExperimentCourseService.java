package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.ExperimentCourse;

import java.util.List;

/**
 * 本科实验课服务接口
 * 继承MyBatis-Plus的IService，提供基础CRUD能力
 */
public interface ExperimentCourseService extends IService<ExperimentCourse> {

    /**
     * 分页查询实验课列表（支持年份筛选）
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param userId   用户ID（当前登录教师）
     * @param year     授课年份（可选，如2023/2024）
     * @param isAudit
     * @return 分页结果（包含实验课列表和总条数）
     */
    IPage<ExperimentCourse> getExperimentCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增实验课
     * @param course 实验课实体（包含课程名称、课时、人数等信息）
     * @return 新增结果（true：成功，false：失败）
     */
    boolean addExperimentCourse(ExperimentCourse course);

    /**
     * 批量删除实验课
     * @param ids 实验课ID列表（支持批量删除多条记录）
     * @return 删除结果（true：成功，false：失败）
     */
    boolean removeExperimentCourseByIds(List<Long> ids);

    /**
     * 计算实验课的工作量
     * @param id     用户ID（用于查询职称系数）
     * @param course 实验课实体（包含课时、学生人数等信息）
     * @return 实验课的工作量
     */
    double countWorkload(Long id, ExperimentCourse course);

    /**
     * 计算并更新用户的实验课总工作量（写入总工作量表）
     * @param userId 用户ID
     * @param year   授课年份
     * @return 总工作量
     */
    double countTotalWorkload(Long userId, Integer year);

    IPage<ExperimentCourse> getExperimentCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditExperimentCourse(Long id, String status, String remark);
}