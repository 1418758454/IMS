package com.ruoyi.manage.controller.research;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.service.research.ResearchTotalWorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/manage/research/totalWorkload")
public class ResearchTotalWorkloadController{
    @Autowired
    private ResearchTotalWorkloadService researchTotalWorkloadService;

    // 保存年度工作量汇总数据
    @PostMapping("/save")
    public AjaxResult save(@RequestBody ResearchTotalWorkload researchTotalWorkload){
        boolean success = researchTotalWorkloadService.save(researchTotalWorkload);
        return success ? AjaxResult.success() : AjaxResult.error();
    }

    // 获取年度工作量汇总数据
    @GetMapping("/getYearWorkloadSummary")
    public AjaxResult getYearWorkloadSummary(String year) {
        // 获取登录用户名
        String userId = SecurityUtils.getUsername();

        // 使用 QueryWrapper 构建查询条件
        QueryWrapper<ResearchTotalWorkload> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("year", year)
                .eq("user_id", BigDecimal.valueOf(Long.parseLong(userId)));

        // 根据年度和用户id查询年度工作量汇总数据
        ResearchTotalWorkload researchTotalWorkload = researchTotalWorkloadService.getOne(queryWrapper);

        return researchTotalWorkload != null ? AjaxResult.success(researchTotalWorkload) : AjaxResult.error();
    }

//    // 获取年度工作量汇总数据
//    @GetMapping("/getTotalWorkload")
//    public AjaxResult getTotalWorkload(String year) {
//        // 获取登录用户名
//        String userId = SecurityUtils.getUsername();
//
//        // 使用 QueryWrapper 构建查询条件
//        QueryWrapper<ResearchTotalWorkload> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("year", year)
//                .eq("user_id", BigDecimal.valueOf(Long.parseLong(userId)));
//
//        // 根据年度和用户id查询年度工作量汇总数据
//        ResearchTotalWorkload researchTotalWorkload = researchTotalWorkloadService.getOne(queryWrapper);
//
//        return researchTotalWorkload != null ? AjaxResult.success(researchTotalWorkload) : AjaxResult.error();
//    }

    /**
     * 获取科研工作量汇总数据
     * @param year 年份
     * @return 科研工作量汇总数据
     */
    @GetMapping("/getTotalWorkload")
    public AjaxResult getTotalWorkload( @RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) Integer deptId,
                                        @RequestParam(required = false) String userId,
                                        @RequestParam(required = false) String mode
    ) {
        if(("search").equals(mode)){
            ResearchTotalWorkload totalWorkload = researchTotalWorkloadService.getTotalWorkload(year, deptId, userId);
            return AjaxResult.success(totalWorkload);

        }else{
            userId = SecurityUtils.getUsername();
            ResearchTotalWorkload totalWorkload = researchTotalWorkloadService.getTotalWorkload(year, Long.valueOf(userId));
            return AjaxResult.success(totalWorkload);

        }

    }


}
