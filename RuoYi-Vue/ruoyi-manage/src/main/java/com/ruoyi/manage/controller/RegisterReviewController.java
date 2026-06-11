package com.ruoyi.manage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.domain.RegisterInformation;
import com.ruoyi.manage.service.RegisterReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/manage/information/register")
public class RegisterReviewController extends BaseController {

    @Autowired
    private RegisterReviewService registerReviewService; // 注入独立的审核Service

    /**
     * 获取待审核列表（分页）
     */
    @GetMapping("/pendingList")
    public AjaxResult getPendingList(Integer pageNum, Integer pageSize, String keyword) {
        Page<RegisterInformation> page = new Page<>(pageNum, pageSize);
//        IPage<RegisterInformation> result = registerReviewService.getPendingList(page);
        IPage<RegisterInformation> result = registerReviewService.getPendingList(page, keyword);
        return AjaxResult.success(result);
    }

    /**
     * 审核操作（通过/拒绝）
     */
    @PostMapping("/review")
    public AjaxResult review(@RequestBody Map<String, Object> params) {
        Long id = Long.parseLong(params.get("id").toString());
        Integer status = Integer.parseInt(params.get("status").toString());
//        String rejectReason = (String) params.get("rejectReason");
        registerReviewService.review(id, status);
        return AjaxResult.success("审核操作成功");
    }

    /**
     * 审核操作（修改）
     */
    @PutMapping("/update")
    public AjaxResult update(@RequestBody RegisterInformation registerInfo) {
        registerReviewService.updataRegisterInfo(registerInfo);
        return AjaxResult.success("修改操作成功");
    }
}