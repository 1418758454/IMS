package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.UndergraduateTheoryCourse;

import java.util.List;

/**
 * 本科理论课服务接口
 * 继承MyBatis-Plus的IService，提供基础CRUD能力
 */
public interface TheoryCourseService extends IService<UndergraduateTheoryCourse> {

    /**
     * 分页查询理论课列表（支持年份筛选）
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param userId   用户ID（当前登录教师）
     * @param year     授课年份（可选，如2023/2024）
     * @param isAudit
     * @return 分页结果（包含理论课列表和总条数）
     */
    IPage<UndergraduateTheoryCourse> getTheoryCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增理论课
     * @param course 理论课实体（包含课程名称、课时、人数等信息）
     * @return 新增结果（true：成功，false：失败）
     */
    boolean addTheoryCourse(UndergraduateTheoryCourse course);

    /**
     * 批量删除理论课
     * @param ids 理论课ID列表（支持批量删除多条记录）
     * @return 删除结果（true：成功，false：失败）
     */
    boolean removeTheoryCourseByIds(List<Long> ids);

    double countWorkload(Long id, UndergraduateTheoryCourse course);


    IPage<UndergraduateTheoryCourse> getTheoryCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditTheoryCourse(Long id, String status, String remark);
}