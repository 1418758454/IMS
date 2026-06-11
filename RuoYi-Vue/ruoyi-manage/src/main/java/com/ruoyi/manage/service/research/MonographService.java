package com.ruoyi.manage.service.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.research.ResearchMonograph;

import java.util.List;

public interface MonographService extends IService<ResearchMonograph> {

    /**
     * 分页查询论著列表（支持关键词搜索）
     */
    IPage<ResearchMonograph> getMonographPage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit);

    /**
     * 新增论著
     */
    boolean addMonograph(ResearchMonograph monograph);

    /**
     * 批量删除论著
     */
    boolean removeMonographByIds(List<Long> list);

    IPage<ResearchMonograph> getMonographPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit);

    boolean auditMonograph(Long id, String status, String remark);

    void countTotalConfirmedWorkload(Long userId, String year);

    void countTotalEstimatedWorkload(Long userId, String year);
}