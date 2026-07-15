package com.ruoyi.manage.controller.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.WorkloadScope;
import com.ruoyi.manage.domain.research.ResearchPatent;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.WorkloadRefreshService;
import com.ruoyi.manage.service.research.PatentService;
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
 * 专利模块控制器
 * 处理前端专利相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/research/patent") // 专利模块接口根路径
public class PatentController {

    @Autowired
    private PatentService patentService; // 注入专利Service

    @Autowired
    private BasicInformationService basicInformationService;

    @Autowired
    private WorkloadRefreshService workloadRefreshService;

    /**
     * 1. 获取专利列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     年份筛选（可选，如2024/2025/2026）
     * @return 分页结果（包含专利列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getPatentList(
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
                IPage<ResearchPatent> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<ResearchPatent> page = patentService.getPatentPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<ResearchPatent> page = patentService.getPatentPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增专利
     * @param patent 专利实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addPatent(@RequestBody ResearchPatent patent) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        patent.setUserId(Long.valueOf(userId)); // 设置创建人ID
        patent.setUserName(userName);
        patent.setWorkload(patent.getCoefficient().multiply(patent.getRank())); // 专利工作量计算（示例值）

        // 审计字段默认值（若前端未传递）
        patent.setCreateTime(patent.getCreateTime() == null ? LocalDateTime.now() : patent.getCreateTime());
        patent.setUpdateTime(patent.getUpdateTime() == null ? LocalDateTime.now() : patent.getUpdateTime());

        // 将审核状态修改为待审核
        patent.setStatus("待审核");

        // 查看论文发表年份与当前页面年份是否一致
        boolean x = !patent.getYear().equals(patent.getAuthorizeTime().toString().substring(0,4));
        boolean success = patentService.addPatent(patent); // 调用Service新增方法
        if (success) {
            workloadRefreshService.refreshResearch(patent.getUserId(), patent.getYear());
        }

        if(success && x){
            return AjaxResult.success("新增专利成功,请选择"+patent.getYear()+"年页面查看", patent);
        }else {
            return success ? AjaxResult.success("新增专利成功", patent) : AjaxResult.error("新增失败");
        }
    }

    /**
     * 3. 删除专利（支持批量删除）
     * @param ids 专利ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deletePatent(@RequestBody Long[] ids) {
        List<ResearchPatent> originals = patentService.listByIds(Arrays.asList(ids));
        boolean success = patentService.removePatentByIds(Arrays.asList(ids));
        if (success) {
            workloadRefreshService.refreshResearch(originals.stream()
                    .map(item -> WorkloadScope.of(item.getUserId(), item.getYear()))
                    .collect(Collectors.toList()));
        }
        return success ? AjaxResult.success("删除专利成功") : AjaxResult.error("删除专利失败");
    }

    /**
     * 4. 更新专利
     * @param patent 专利实体（包含ID和更新的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updatePatent(@RequestBody ResearchPatent patent,
            @RequestParam(defaultValue = "false") boolean auditEdit) {
        ResearchPatent originalPatent = patentService.getById(patent.getId());
        String userId = SecurityUtils.getUsername();
        patent.setUserId(Long.valueOf(userId));
        patent.setUpdateTime(LocalDateTime.now());

        // 1. 重新计算工作量
        BigDecimal coefficient = patent.getCoefficient();
        BigDecimal rank = patent.getRank();
        BigDecimal workload = coefficient.multiply(rank);
        patent.setWorkload(workload);

        // 2. 保存原始年份（关键：用于后续旧年份总工作量更新）
        Integer originalYear = Integer.valueOf(originalPatent.getYear()); // 旧年份
        Integer newYear = Integer.valueOf(patent.getAuthorizeTime().toString().substring(0, 4)); // 从授权时间提取新年份
        boolean yearChanged = !originalYear.equals(newYear); // 判断年份是否变更

        // 3. 更新年份字段为新年份
        patent.setYear(String.valueOf(newYear));

        // 将审核状态修改为待审核
        patent.setStatus("待审核");

        // 4. 执行数据库更新
//        boolean success = patentService.updateById(patent);
        boolean success = false;
        com.ruoyi.manage.utils.AdminAuditUpdateUtils.preserve(originalPatent, patent, auditEdit);
        if(patentService.updateById(patent)){
            workloadRefreshService.refreshResearch(Arrays.asList(
                    WorkloadScope.of(originalPatent.getUserId(), originalPatent.getYear()),
                    WorkloadScope.of(patent.getUserId(), patent.getYear())
            ));
            success = true;
        }

        // 5. 返回提示信息
        if (success && yearChanged) {
            return AjaxResult.success("更新专利成功,请选择" + newYear + "年页面查看", patent);
        } else {
            return success ? AjaxResult.success("更新专利成功", patent) : AjaxResult.error("更新失败");
        }
    }

    /**
     * 审核专利
     * @param patent 专利审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditPatent(@RequestBody ResearchPatent patent) {
        try {
            ResearchPatent originalPatent = patentService.getById(patent.getId());
            // 调用Service层审核方法
            boolean success = patentService.auditPatent(patent.getId(), patent.getStatus(), patent.getRemark());

            if (success) {
                workloadRefreshService.refreshResearch(originalPatent.getUserId(), originalPatent.getYear());
                String message = "已通过".equals(patent.getStatus()) ? "审核通过成功" : "退回修改成功";
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
