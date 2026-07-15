package com.ruoyi.manage.service.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.research.ResearchPaper;

import java.util.List;

public interface PaperService extends IService<ResearchPaper> {

    /**
     * 分页查询论文列表（支持关键词搜索）
     */
    IPage<ResearchPaper> getPaperPage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit);

    /**
     * 新增论文
     */
    boolean addPaper(ResearchPaper paper);

    /**
     * 批量删除论文
     */
    boolean removePaperByIds(List<Long> list);


    IPage<ResearchPaper> getPaperPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit);

    boolean auditPaper(Long id, String status, String remark);


}