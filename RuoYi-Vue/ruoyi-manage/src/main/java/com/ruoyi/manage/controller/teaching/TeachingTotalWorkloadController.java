package com.ruoyi.manage.controller.teaching;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.service.teaching.TeachingTotalWorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage/teaching/totalWorkload")
public class TeachingTotalWorkloadController {

    @Autowired
    private TeachingTotalWorkloadService teachingTotalWorkloadService;

    /**
     * 获取教学工作量汇总数据
     * @param year 年份
     * @return 教学工作量汇总数据
     */
    @GetMapping("/getTotalWorkload")
    public AjaxResult getTotalWorkload( @RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) Integer deptId,
                                        @RequestParam(required = false) String userId,
                                        @RequestParam(required = false) String mode
    ) {
        if(("search").equals(mode)){
            TeachingTotalWorkload totalWorkload = teachingTotalWorkloadService.getTotalWorkload(year, deptId, userId);
            return AjaxResult.success(totalWorkload);

        }else{
            userId = SecurityUtils.getUsername();
            TeachingTotalWorkload totalWorkload = teachingTotalWorkloadService.getTotalWorkload(year, Long.valueOf(userId));
            return AjaxResult.success(totalWorkload);

        }

    }
}
