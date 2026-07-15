package com.ruoyi.manage.service.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.research.ResearchPatent;

import java.util.List;

public interface PatentService extends IService<ResearchPatent> {

    /**
     * 分页查询专利列表（支持关键词搜索）
     */
    IPage<ResearchPatent> getPatentPage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit);

    /**
     * 新增专利
     */
    boolean addPatent(ResearchPatent patent);

    /**
     * 批量删除专利
     */
    boolean removePatentByIds(List<Long> list);

    IPage<ResearchPatent> getPatentPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit);

    boolean auditPatent(Long id, String status, String remark);


}