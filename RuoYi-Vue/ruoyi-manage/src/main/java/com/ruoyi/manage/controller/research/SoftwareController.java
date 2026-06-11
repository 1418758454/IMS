package com.ruoyi.manage.controller.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchSoftware;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.SoftwareService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SoftwareService softwareService; // 注入软著Service

    @Autowired
    private BasicInformationService basicInformationService;

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
    public AjaxResult addSoftware(@RequestBody ResearchSoftware software) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        software.setUserId(Long.valueOf(userId)); // 设置创建人ID
        software.setUserName(userName);
        software.setWorkload(software.getRank().multiply(BigDecimal.valueOf(2.0))); // 软著工作量计算（示例值）

        // 审计字段默认值（若前端未传递）
        software.setCreateTime(software.getCreateTime() == null ? LocalDateTime.now() : software.getCreateTime());
        software.setUpdateTime(software.getUpdateTime() == null ? LocalDateTime.now() : software.getUpdateTime());

        // 将审核状态修改为待审核
        software.setStatus("待审核");

        // 查看论文发表年份与当前页面年份是否一致
        boolean x = !software.getYear().equals(software.getAuthorizeTime().toString().substring(0,4));
        boolean success = softwareService.addSoftware(software); // 调用Service新增方法

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
    public AjaxResult deleteSoftware(@RequestBody Long[] ids) {
        boolean success = softwareService.removeSoftwareByIds(Arrays.asList(ids));
        return success ? AjaxResult.success("删除软著成功") : AjaxResult.error("删除软著失败");
    }

    /**
     * 4. 更新软著
     * @param software 软著实体（包含ID和更新的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    public AjaxResult updateSoftware(@RequestBody ResearchSoftware software) {
        String userId = SecurityUtils.getUsername();
        software.setUserId(Long.valueOf(userId));
        software.setUpdateTime(LocalDateTime.now());

        // 1. 重新计算工作量
        BigDecimal rank = software.getRank();
        BigDecimal workload = rank.multiply(BigDecimal.valueOf(2.0)); // 软著固定系数为2.0
        software.setWorkload(workload);

        // 2. 保存原始年份（关键：用于后续旧年份总工作量更新）
        Integer originalYear = Integer.valueOf(software.getYear()); // 旧年份
        Integer newYear = Integer.valueOf(software.getAuthorizeTime().toString().substring(0, 4)); // 从授权时间提取新年份
        boolean yearChanged = !originalYear.equals(newYear); // 判断年份是否变更

        // 3. 更新年份字段为新年份
        software.setYear(String.valueOf(newYear));

        // 将审核状态修改为待审核
        software.setStatus("待审核");

        // 4. 执行数据库更新
//        boolean success = softwareService.updateById(software);
        boolean success = false;
        if(softwareService.updateById(software)){
            // 计算模块总工作量
            softwareService.countTotalConfirmedWorkload(software.getUserId(), software.getYear());
            softwareService.countTotalEstimatedWorkload(software.getUserId(), software.getYear());
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
    public AjaxResult auditSoftware(@RequestBody ResearchSoftware software) {
        try {
            // 调用Service层审核方法
            boolean success = softwareService.auditSoftware(software.getId(), software.getStatus(), software.getRemark());

            if (success) {
                String message = "已通过".equals(software.getStatus()) ? "审核通过成功" : "退回修改成功";
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}