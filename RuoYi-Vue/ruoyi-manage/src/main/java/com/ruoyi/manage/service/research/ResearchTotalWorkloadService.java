package com.ruoyi.manage.service.research;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;

public interface ResearchTotalWorkloadService extends IService<ResearchTotalWorkload> {
    boolean save(ResearchTotalWorkload researchTotalWorkload);

    ResearchTotalWorkload getTotalWorkload(Integer year, Long userId);

    ResearchTotalWorkload getTotalWorkload(Integer year, Integer deptId, String userId);
}
