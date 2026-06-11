package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.GraduateGuideStudent;
import java.util.List;

/**
 * 研究生指导学生Service接口
 */
public interface GraduateGuideStudentService extends IService<GraduateGuideStudent> {
    // 分页查询指导记录
    IPage<GraduateGuideStudent> getGraduateGuideStudentPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit);

    // 新增指导记录
    boolean addGraduateGuideStudent(GraduateGuideStudent guideStudent);

    // 批量删除指导记录
    boolean removeGraduateGuideStudentByIds(List<Long> ids);

    // 计算模块总工作量（累加前端输入的workload）
    double countTotalWorkload(Long userId, Integer year);

    IPage<GraduateGuideStudent> getGraduateGuideStudentPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit);

    boolean auditGraduateGuideStudent(Long id, String status, String remark);
}