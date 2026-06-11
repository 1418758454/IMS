package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.ScienceInnovation;

import java.util.List;

/**
 * 科技创新服务接口（与实验课Service接口结构完全一致）
 */
public interface ScienceInnovationService extends IService<ScienceInnovation> {

    /**
     * 分页查询科技创新列表（支持年份筛选）
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param userId   用户ID（当前登录教师）
     * @param year     页面选中年份（用于筛选）
     * @param isAudit
     * @return 分页结果（包含项目列表和总条数）
     */
    IPage<ScienceInnovation> getScienceInnovationPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增科技创新项目
     * @param innovation 科技创新实体（包含项目名称、级别等信息）
     * @return 新增结果（true：成功，false：失败）
     */
    boolean addScienceInnovation(ScienceInnovation innovation);

    /**
     * 批量删除科技创新项目
     * @param ids 项目ID列表（支持批量删除）
     * @return 删除结果（true：成功，false：失败）
     */
    boolean removeScienceInnovationByIds(List<Long> ids);

    /**
     * 计算单个科技创新项目的工作量（核心逻辑，与实验课countWorkload对应）
     * @param userId     用户ID（用于查询职称等系数，预留扩展）
     * @param innovation 项目实体（包含级别等计算参数）
     * @return 项目工作量（保留三位小数）
     */
    double countWorkload(Long userId, ScienceInnovation innovation);

    /**
     * 计算并更新用户的科技创新总工作量（写入总工作量表）
     * @param userId 用户ID
     * @param year   页面选中年份
     * @return 总工作量
     */
    double countTotalWorkload(Long userId, Integer year);

    IPage<ScienceInnovation> getScienceInnovationPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditScienceInnovation(Long id, String status, String remark);
}