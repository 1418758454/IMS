package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.ThesisCourse;

import java.util.List;

/**
 * 本科毕业论文服务接口
 * 继承MyBatis-Plus的IService，提供基础CRUD能力
 */
public interface ThesisCourseService extends IService<ThesisCourse> {

    /**
     * 分页查询毕业论文记录（支持年份筛选）
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param userId   用户ID（当前登录教师）
     * @param year     授课年份（可选，如2023/2024）
     * @param isAudit
     * @return 分页结果（包含毕业论文列表和总条数）
     */
    IPage<ThesisCourse> getThesisCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增毕业论文记录
     * @param course 毕业论文实体（包含指导论文数、学生人数等信息）
     * @return 新增结果（true：成功，false：失败）
     */
    boolean addThesisCourse(ThesisCourse course);

    /**
     * 批量删除毕业论文记录
     * @param ids 毕业论文ID列表（支持批量删除多条记录）
     * @return 删除结果（true：成功，false：失败）
     */
    boolean removeThesisCourseByIds(List<Long> ids);

    /**
     * 计算毕业论文的工作量
     * @param id     用户ID（用于查询职称系数）
     * @param course 毕业论文实体（包含指导论文数、学生人数等信息）
     * @return 毕业论文的工作量
     */
    double countWorkload(Long id, ThesisCourse course);

    /**
     * 计算并更新用户的毕业论文总工作量（写入总工作量表）
     * @param userId 用户ID
     * @param year   授课年份
     * @return 总工作量
     */
    double countTotalWorkload(Long userId, Integer year);

    IPage<ThesisCourse> getThesisCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditThesisCourse(Long id, String status, String remark);
}