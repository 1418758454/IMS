package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.EducationReform;
import java.util.List;

/**
 * 教改项目Service接口（移除单个工作量计算，保留总工作量计算）
 */
public interface EducationReformService extends IService<EducationReform> {
    // 分页查询（复用科技创新逻辑）
    IPage<EducationReform> getEducationReformPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    // 新增项目（直接保存前端工作量）
    boolean addEducationReform(EducationReform reform);

    // 批量删除（复用科技创新逻辑）
    boolean removeEducationReformByIds(List<Long> ids);

    // 计算模块总工作量（保留，累加前端输入的工作量）
    double countTotalWorkload(Long userId, Integer year);

    IPage<EducationReform> getEducationReformPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditEducationReform(Long id, String status, String remark);
}