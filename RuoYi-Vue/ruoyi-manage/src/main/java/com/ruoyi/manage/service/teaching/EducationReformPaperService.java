package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.EducationReformPaper;

import java.util.List;

/**
 * 教改论文Service接口（与科技创新Service接口结构完全一致）
 */
public interface EducationReformPaperService extends IService<EducationReformPaper> {

    /**
     * 分页查询教改论文列表（支持年份筛选）
     */
    IPage<EducationReformPaper> getEducationReformPaperPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    /**
     * 新增教改论文（处理发表日期年份提取）
     */
    boolean addEducationReformPaper(EducationReformPaper paper);

    /**
     * 批量删除教改论文
     */
    boolean removeEducationReformPaperByIds(List<Long> ids);

    /**
     * 计算并更新用户的教改论文总工作量（写入总工作量表）
     */


    double countWorkload(Long userId, EducationReformPaper paper);

    IPage<EducationReformPaper> getEducationReformPaperPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditEducationReformPaper(Long id, String status, String remark);
}