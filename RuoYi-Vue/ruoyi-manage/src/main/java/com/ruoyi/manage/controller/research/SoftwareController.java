package com.ruoyi.manage.controller.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.WorkloadScope;
import com.ruoyi.manage.domain.research.ResearchSoftware;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.WorkloadRefreshService;
import com.ruoyi.manage.service.research.SoftwareService;
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
 * 软著模块控制器
 * 处理前端软著相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/research/software") // 软著模块接口根路径
public class SoftwareController {

    private static final BigDecimal DEFAULT_SOFTWARE_COEFFICIENT = new BigDecimal("2.00");

    @Autowired
    private SoftwareService softwareService; // 注入软著Service

    @Autowired
    private BasicInformationService basicInformationService;

    @Autowired
    private WorkloadRefreshService workloadRefreshService;

    /**
     * 1. 获取软著列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     年份筛选（可选，如2024/2025/2026）
     * @return 分页结果（包含软著列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getSoftwareList(
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
            if(userIds.isEmpty()){
                IPage<ResearchSoftware> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<ResearchSoftware> page = softwareService.getSoftwarePageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<ResearchSoftware> page = softwareService.getSoftwarePage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增软著
     * @param software 软著实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addSoftware(@RequestBody ResearchSoftware software) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        software.setUserId(Long.valueOf(userId)); // 设置创建人ID
        software.setUserName(userName);
        software.setCoefficient(DEFAULT_SOFTWARE_COEFFICIENT);
        software.setWorkload(software.getRank().multiply(software.getCoefficient()));

        // 审计字段默认值（若前端未传递）
        software.setCreateTime(software.getCreateTime() == null ? LocalDateTime.now() : software.getCreateTime());
        software.setUpdateTime(software.getUpdateTime() == null ? LocalDateTime.now() : software.getUpdateTime());

        // 将审核状态修改为待审核
        software.setStatus("待审核");

        // 查看论文发表年份与当前页面年份是否一致
        boolean x = !software.getYear().equals(software.getAuthorizeTime().toString().substring(0,4));
        boolean success = softwareService.addSoftware(software); // 调用Service新增方法
        if (success) {
            workloadRefreshService.refreshResearch(software.getUserId(), software.getYear());
        }

        if(success && x){
            return AjaxResult.success("新增软著成功,请选择"+software.getYear()+"年页面查看", software);
        }else {
            return success ? AjaxResult.success("新增软著成功", software) : AjaxResult.error("新增失败");
        }
        
    }

    /**
     * 3. 删除软著（支持批量删除）
     * @param ids 软著ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteSoftware(@RequestBody Long[] ids) {
        List<ResearchSoftware> originals = softwareService.listByIds(Arrays.asList(ids));
        boolean success = softwareService.removeSoftwareByIds(Arrays.asList(ids));
        if (success) {
            workloadRefreshService.refreshResearch(originals.stream()
                    .map(item -> WorkloadScope.of(item.getUserId(), item.getYear()))
                    .collect(Collectors.toList()));
        }
        return success ? AjaxResult.success("删除软著成功") : AjaxResult.error("删除软著失败");
    }

    /**
     * 4. 更新软著
     * @param software 软著实体（包含ID和更新的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateSoftware(@RequestBody ResearchSoftware software,
            @RequestParam(defaultValue = "false") boolean auditEdit) {
        String userId = SecurityUtils.getUsername();
        software.setUserId(Long.valueOf(userId));
        software.setUpdateTime(LocalDateTime.now());

        ResearchSoftware originalSoftware = softwareService.getById(software.getId());
        BigDecimal coefficient = originalSoftware != null && originalSoftware.getCoefficient() != null
                ? originalSoftware.getCoefficient()
                : DEFAULT_SOFTWARE_COEFFICIENT;
        software.setCoefficient(coefficient);

        // 1. 重新计算工作量
        BigDecimal rank = software.getRank();
        BigDecimal workload = rank.multiply(coefficient);
        software.setWorkload(workload);

        // 2. 保存原始年份（关键：用于后续旧年份总工作量更新）
        Integer originalYear = Integer.valueOf(originalSoftware.getYear()); // 旧年份
        Integer newYear = Integer.valueOf(software.getAuthorizeTime().toString().substring(0, 4)); // 从授权时间提取新年份
        boolean yearChanged = !originalYear.equals(newYear); // 判断年份是否变更

        // 3. 更新年份字段为新年份
        software.setYear(String.valueOf(newYear));

        // 将审核状态修改为待审核
        software.setStatus("待审核");

        // 4. 执行数据库更新
//        boolean success = softwareService.updateById(software);
        boolean success = false;
        com.ruoyi.manage.utils.AdminAuditUpdateUtils.preserve(originalSoftware, software, auditEdit);
        if(softwareService.updateById(software)){
            workloadRefreshService.refreshResearch(Arrays.asList(
                    WorkloadScope.of(originalSoftware.getUserId(), originalSoftware.getYear()),
                    WorkloadScope.of(software.getUserId(), software.getYear())
            ));
            success = true;
        }

        // 5. 返回提示信息
        if (success && yearChanged) {
            return AjaxResult.success("更新软著成功,请选择" + newYear + "年页面查看", software);
        } else {
            return success ? AjaxResult.success("更新软著成功", software) : AjaxResult.error("更新失败");
        }
    }

    /**
     * 5. 审核软著
     * @param software 软著审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditSoftware(@RequestBody ResearchSoftware software) {
        try {
            ResearchSoftware originalSoftware = softwareService.getById(software.getId());
            // 调用Service层审核方法
            boolean success = softwareService.auditSoftware(software.getId(), software.getStatus(), software.getRemark());

            if (success) {
                workloadRefreshService.refreshResearch(originalSoftware.getUserId(), originalSoftware.getYear());
                String message = "已通过".equals(software.getStatus()) ? "审核通过成功" : "退回修改成功";
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
