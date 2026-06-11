package com.ruoyi.manage.service.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.research.ResearchAward;

import java.util.List;

public interface AwardService extends IService<ResearchAward> {

    /**
     * 分页查询获奖列表（支持关键词搜索）
     */
    IPage<ResearchAward> getAwardPage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit);

    /**
     * 新增获奖
     */
    boolean addAward(ResearchAward award);

    /**
     * 批量删除获奖
     */
    boolean removeAwardByIds(List<Long> list);


    IPage<ResearchAward> getAwardPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit);

    boolean auditAward(Long id, String status, String remark);

    void countTotalConfirmedWorkload(Long userId, String year);

    void countTotalEstimatedWorkload(Long userId, String year);
}