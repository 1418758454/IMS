package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.Competition;
import java.util.List;

/**
 * 学科竞赛服务接口
 */
public interface CompetitionService extends IService<Competition> {

    /**
     * 分页查询竞赛列表
     */
    IPage<Competition> getCompetitionPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增竞赛
     */
    boolean addCompetition(Competition competition);

    /**
     * 批量删除竞赛
     */
    boolean removeCompetitionByIds(List<Long> ids);

    /**
     * 计算单条竞赛工作量
     */
    double countWorkload(Long userId, Competition competition);

    /**
     * 计算总工作量并更新到总表
     */
    double countTotalWorkload(Long userId, Integer year);

    IPage<Competition> getCompetitionPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditCompetition(Long id, String status, String remark);
}