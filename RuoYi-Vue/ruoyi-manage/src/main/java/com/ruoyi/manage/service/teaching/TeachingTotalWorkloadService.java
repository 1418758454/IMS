package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;

public interface TeachingTotalWorkloadService extends IService<TeachingTotalWorkload> {

    TeachingTotalWorkload getTotalWorkload(Integer year, Long userId);

    TeachingTotalWorkload getTotalWorkload(Integer year, Integer deptId, String userId);
}
