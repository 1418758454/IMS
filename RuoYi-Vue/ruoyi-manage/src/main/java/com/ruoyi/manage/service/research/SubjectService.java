package com.ruoyi.manage.service.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.research.ResearchSubject;

import java.util.List;

public interface SubjectService extends IService<ResearchSubject> {

    /**
     * 分页查询课题列表（支持关键词搜索）
     * */
    IPage<ResearchSubject> getSubjectPage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit);

    boolean addSubject(ResearchSubject subject);

    boolean removeSubjectByIds(List<Long> list);

    boolean updateSubject(ResearchSubject subject);

    IPage<ResearchSubject> getSubjectPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit);

    boolean auditSubject(Long id, String status, String remark);

    void countTotalConfirmedWorkload(Long userId, String year);

    void countTotalEstimatedWorkload(Long userId, String year);
}
