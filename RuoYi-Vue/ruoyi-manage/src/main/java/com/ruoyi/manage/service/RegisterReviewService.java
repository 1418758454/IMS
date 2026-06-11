package com.ruoyi.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.manage.domain.RegisterInformation;

/**
 * 注册审核 Service 接口（独立于基础信息Service，仅处理审核流程）
 */
public interface RegisterReviewService {

    /**
     * 分页获取待审核注册信息列表（仅查 status=0 的记录）
     * @param page 分页参数（页码、每页条数）
     * @return 分页结果（含待审核列表和总条数）
     */
//    IPage<RegisterInformation> getPendingList(Page<RegisterInformation> page);
    IPage<RegisterInformation> getPendingList(Page<RegisterInformation> page, String keyword);

    /**
     * 审核注册信息（通过/拒绝）
     * @param id 注册信息ID（注册表主键）
     * @param status 审核状态（1=通过，2=拒绝）
     */
    void review(Long id, Integer status);

    void updataRegisterInfo(RegisterInformation registerInfo);
}