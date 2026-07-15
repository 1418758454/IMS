package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import org.apache.ibatis.annotations.Param;

public interface WorkloadRefreshMapper {

    ResearchTotalWorkload aggregateResearch(@Param("userId") Long userId,
                                             @Param("year") String year);

    TeachingTotalWorkload aggregateTeaching(@Param("userId") Long userId,
                                             @Param("year") Integer year);

    int upsertResearch(ResearchTotalWorkload workload);

    int upsertTeaching(TeachingTotalWorkload workload);

    int deleteResearch(@Param("userId") Long userId, @Param("year") String year);

    int deleteTeaching(@Param("userId") Long userId, @Param("year") Integer year);
}
