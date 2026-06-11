package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.GraduateTheoryCourse;

import java.util.List;

/**
 * 研究生理论课服务接口
 * 继承MyBatis-Plus的IService，提供基础CRUD能力
 */
public interface GraduateTheoryCourseService extends IService<GraduateTheoryCourse> {

    /**
     * 分页查询研究生理论课列表（支持年份筛选）
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param userId   用户ID（当前登录教师）
     * @param year     授课年份（可选，如2023/2024）
     * @param isAudit
     * @return 分页结果（包含理论课列表和总条数）
     */
    IPage<GraduateTheoryCourse> getGraduateTheoryCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增研究生理论课
     * @param course 研究生理论课实体（包含课程名称、课时、人数等信息）
     * @return 新增结果（true：成功，false：失败）
     */
    boolean addGraduateTheoryCourse(GraduateTheoryCourse course);

    /**
     * 批量删除研究生理论课
     * @param ids 研究生理论课ID列表（支持批量删除多条记录）
     * @return 删除结果（true：成功，false：失败）
     */
    boolean removeGraduateTheoryCourseByIds(List<Long> ids);

    /**
     * 计算单条研究生理论课工作量
     * @param id     用户ID
     * @param course 研究生理论课实体
     * @return 工作量数值
     */
    double countWorkload(Long id, GraduateTheoryCourse course);

    /**
     * 计算研究生理论课总工作量
     * @param userId 用户ID
     * @param year   授课年份
     * @return 总工作量数值
     */
    double countTotalWorkload(Long userId, Integer year);

    IPage<GraduateTheoryCourse> getGraduateTheoryCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditGraduateTheoryCourse(Long id, String status, String remark);
}