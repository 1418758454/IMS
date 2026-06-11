package com.ruoyi.manage.service.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.research.ResearchSoftware;

import java.util.List;

public interface SoftwareService extends IService<ResearchSoftware> {

    /**
     * 分页查询软著列表（支持关键词搜索）
     */
    IPage<ResearchSoftware> getSoftwarePage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit);

    /**
     * 新增软著
     */
    boolean addSoftware(ResearchSoftware software);

    /**
     * 批量删除软著
     */
    boolean removeSoftwareByIds(List<Long> list);

    IPage<ResearchSoftware> getSoftwarePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit);

    boolean auditSoftware(Long id, String status, String remark);

    void countTotalConfirmedWorkload(Long userId, String year);

    void countTotalEstimatedWorkload(Long userId, String year);
}