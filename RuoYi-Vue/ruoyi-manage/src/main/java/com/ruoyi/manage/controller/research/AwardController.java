package com.ruoyi.manage.controller.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.WorkloadScope;
import com.ruoyi.manage.domain.research.ResearchAward;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.WorkloadRefreshService;
import com.ruoyi.manage.service.research.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获奖模块控制器
 * 处理前端获奖相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/research/award") // 获奖模块接口根路径
public class AwardController {

    @Autowired
    private AwardService awardService; // 注入获奖Service

    @Autowired
    private BasicInformationService basicInformationService;

    @Autowired
    private WorkloadRefreshService workloadRefreshService;

    /**
     * 1. 获取获奖列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     年份筛选（可选，如2024/2025/2026）
     * @return 分页结果（包含获奖列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getAwardList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Boolean isAudit // 新增：当前是否为审核页面
    ) {
        if("all".equals(userId)){
            List<BasicInformation> basicInformationList = basicInformationService.listByDept(deptId);
            List<String> userIds = basicInformationList.stream().map(BasicInformation::getUserId).collect(Collectors.toList());
            System.out.println("用户id为:" + userIds);
            if(userIds.isEmpty()){
                IPage<ResearchAward> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<ResearchAward> page = awardService.getAwardPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<ResearchAward> page = awardService.getAwardPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增获奖
     * @param award 获奖实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addAward(@RequestBody ResearchAward award) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        award.setUserId(Long.valueOf(userId));
        award.setUserName(userName);
        award.setWorkload(award.getCoefficient().multiply(award.getRank())); // 获奖工作量计算（示例值）

        // 审计字段默认值（若前端未传递）
        award.setCreateTime(award.getCreateTime() == null ? LocalDateTime.now() : award.getCreateTime());
        award.setUpdateTime(award.getUpdateTime() == null ? LocalDateTime.now() : award.getUpdateTime());

        // 将审核状态修改为待审核
        award.setStatus("待审核");

        // 查看论文发表年份与当前页面年份是否一致
        boolean x = !award.getYear().equals(award.getAwardTime().toString().substring(0,4));
        boolean success = awardService.addAward(award); // 调用Service新增方法
        if (success) {
            workloadRefreshService.refreshResearch(award.getUserId(), award.getYear());
        }

        if(success && x){
            return AjaxResult.success("新增获奖成功,请选择"+award.getYear()+"年页面查看", award);
        }else {
            return success ? AjaxResult.success("新增获奖成功", award) : AjaxResult.error("新增失败");
        }
        
        }

    /**
     * 3. 删除获奖（支持批量删除）
     * @param ids 获奖ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteAward(@RequestBody Long[] ids) {
        List<ResearchAward> originals = awardService.listByIds(Arrays.asList(ids));
        boolean success = awardService.removeAwardByIds(Arrays.asList(ids));
        if (success) {
            workloadRefreshService.refreshResearch(originals.stream()
                    .map(item -> WorkloadScope.of(item.getUserId(), item.getYear()))
                    .collect(Collectors.toList()));
        }
        return success ? AjaxResult.success("删除获奖成功") : AjaxResult.error("删除获奖失败");
    }

    /**
     * 4. 更新获奖
     * @param award 获奖实体（包含ID和更新的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateAward(@RequestBody ResearchAward award,
            @RequestParam(defaultValue = "false") boolean auditEdit) {
        ResearchAward originalAward = awardService.getById(award.getId());
        String userId = SecurityUtils.getUsername();
        award.setUserId(Long.valueOf(userId));
        award.setUpdateTime(LocalDateTime.now());

        // 1. 重新计算工作量
        BigDecimal coefficient = award.getCoefficient();
        BigDecimal rank = award.getRank();
        BigDecimal workload = coefficient.multiply(rank);
        award.setWorkload(workload);

        // 2. 保存原始年份（关键：用于后续旧年份总工作量更新）
        Integer originalYear = Integer.valueOf(originalAward.getYear()); // 旧年份
        Integer newYear = Integer.valueOf(award.getAwardTime().toString().substring(0, 4)); // 从获奖时间提取新年份
        boolean yearChanged = !originalYear.equals(newYear); // 判断年份是否变更

        // 3. 更新年份字段为新年份
        award.setYear(String.valueOf(newYear));

        // 将审核状态修改为待审核
        award.setStatus("待审核");

        // 4. 执行数据库更新
//        Boolean success = awardService.updateById(award);
        boolean success = false;
        com.ruoyi.manage.utils.AdminAuditUpdateUtils.preserve(originalAward, award, auditEdit);
        if(awardService.updateById(award)){
            workloadRefreshService.refreshResearch(Arrays.asList(
                    WorkloadScope.of(originalAward.getUserId(), originalAward.getYear()),
                    WorkloadScope.of(award.getUserId(), award.getYear())
            ));
            success = true;
        }

        // 5. 若年份变更，则更新旧年份总工作量
        // 5. 返回提示信息
        if (success && yearChanged) {
            return AjaxResult.success("更新获奖成功,请选择" + newYear + "年页面查看", award);
        } else {
            return success ? AjaxResult.success("更新获奖成功", award) : AjaxResult.error("更新失败");
        }
    }

    /**
     * 5. 审核获奖
     * @param award 获奖审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditAward(@RequestBody ResearchAward award) {
        try {
            ResearchAward originalAward = awardService.getById(award.getId());
            // 调用Service层审核方法
            boolean success = awardService.auditAward(award.getId(), award.getStatus(), award.getRemark());

            if (success) {
                workloadRefreshService.refreshResearch(originalAward.getUserId(), originalAward.getYear());
                String message = "已通过".equals(award.getStatus()) ? "审核通过成功" : "退回修改成功";
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}
