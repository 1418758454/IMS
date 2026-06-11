package com.ruoyi.manage.controller.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchMonograph;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.MonographService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 论著模块控制器
 * 处理前端论著相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/research/monograph") // 论著模块接口根路径
public class MonographController {

    @Autowired
    private MonographService monographService; // 注入论著Service

    @Autowired
    private BasicInformationService basicInformationService; // 注入基本信息Service

    /**
     * 1. 获取论著列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     年份筛选（可选，如2024/2025/2026）
     * @return 分页结果（包含论著列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getMonographList(
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
                IPage<ResearchMonograph> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<ResearchMonograph> page = monographService.getMonographPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<ResearchMonograph> page = monographService.getMonographPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增论著
     * @param monograph 论著实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    public AjaxResult addMonograph(@RequestBody ResearchMonograph monograph) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        monograph.setUserId(Long.valueOf(userId));
        monograph.setUserName(userName);

        monograph.setWorkload(monograph.getCoefficient().multiply(monograph.getRank())); // 论著工作量计算（示例值）

        // 审计字段默认值（若前端未传递）
        monograph.setCreateTime(monograph.getCreateTime() == null ? LocalDateTime.now() : monograph.getCreateTime());
        monograph.setUpdateTime(monograph.getUpdateTime() == null ? LocalDateTime.now() : monograph.getUpdateTime());

        // 将审核状态修改为待审核
        monograph.setStatus("待审核");

        // 查看论文发表年份与当前页面年份是否一致
        boolean x = !monograph.getYear().equals(monograph.getPublishTime().toString().substring(0,4));
        boolean success = monographService.addMonograph(monograph); // 调用Service新增方法

        if(success && x){
            return AjaxResult.success("新增论著成功,请选择"+monograph.getYear()+"年页面查看", monograph);
        }else {
            return success ? AjaxResult.success("新增论著成功", monograph) : AjaxResult.error("新增失败");
        }

    }

    /**
     * 3. 删除论著（支持批量删除）
     * @param ids 论著ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteMonograph(@RequestBody Long[] ids) {
        boolean success = monographService.removeMonographByIds(Arrays.asList(ids));
        return success ? AjaxResult.success("删除论著成功") : AjaxResult.error("删除论著失败");
    }

    /**
     * 4. 更新论著
     * @param monograph 论著实体（包含ID和更新的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    public AjaxResult updateMonograph(@RequestBody ResearchMonograph monograph) {
        String userId = SecurityUtils.getUsername();
        monograph.setUserId(Long.valueOf(userId));
        monograph.setUpdateTime(LocalDateTime.now());

        // 1. 重新计算工作量
        BigDecimal coefficient = monograph.getCoefficient();
        BigDecimal rank = monograph.getRank();
        BigDecimal workload = coefficient.multiply(rank);
        monograph.setWorkload(workload);

        // 2. 保存原始年份（关键：用于后续旧年份总工作量更新）
        Integer originalYear = Integer.valueOf(monograph.getYear()); // 旧年份
        Integer newYear = Integer.valueOf(monograph.getPublishTime().toString().substring(0, 4)); // 从出版时间提取新年份
        boolean yearChanged = !originalYear.equals(newYear); // 判断年份是否变更

        // 3. 更新年份字段为新年份
        monograph.setYear(String.valueOf(newYear));

        // 将审核状态修改为待审核
        monograph.setStatus("待审核");

        // 4. 执行数据库更新
//        boolean success = monographService.updateById(monograph);
        boolean success = false;
        if(monographService.updateById(monograph)){
            // 计算模块总工作量
            monographService.countTotalConfirmedWorkload(monograph.getUserId(), monograph.getYear());
            monographService.countTotalEstimatedWorkload(monograph.getUserId(), monograph.getYear());
            success = true;
        }

        // 5. 返回提示信息
        if (success && yearChanged) {
            return AjaxResult.success("更新论著成功,请选择" + newYear + "年页面查看", monograph);
        } else {
            return success ? AjaxResult.success("更新论著成功", monograph) : AjaxResult.error("更新失败");
        }
    }

    /**
     * 5. 审核论著
     * @param monograph 论著审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditMonograph(@RequestBody ResearchMonograph monograph) {
        try {
            // 调用Service层审核方法
            boolean success = monographService.auditMonograph(monograph.getId(), monograph.getStatus(), monograph.getRemark());

            if (success) {
                String message = "已通过".equals(monograph.getStatus()) ? "审核通过成功" : "退回修改成功";
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}