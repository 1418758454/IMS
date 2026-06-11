package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.ScienceInnovation;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.ScienceInnovationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 科技创新控制器
 * 处理前端科技创新项目相关的HTTP请求（自动计算工作量，与实验课逻辑一致）
 */
@RestController
@RequestMapping("/manage/teaching/scienceInnovation") // 接口根路径（遵循教学模块命名规范）
public class ScienceInnovationController {

    @Autowired
    private ScienceInnovationService scienceInnovationService; // 注入科技创新Service
    @Autowired
    private BasicInformationService basicInformationService;


    /**
     * 1. 获取科技创新列表（分页+年份筛选）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     页面选中年份（可选，用于筛选）
     * @return 分页结果（包含项目列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getScienceInnovationList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Boolean isAudit
    ) {
        if("all".equals(userId)){
            List<BasicInformation> basicInformationList = basicInformationService.listByDept(deptId);
            List<String> userIds = basicInformationList.stream().map(BasicInformation::getUserId).collect(Collectors.toList());
            if(userIds.isEmpty()){
                IPage<ScienceInnovation> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<ScienceInnovation> page = scienceInnovationService.getScienceInnovationPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<ScienceInnovation> page = scienceInnovationService.getScienceInnovationPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增科技创新项目（自动计算工作量+更新总工作量）
     * @param innovation 科技创新实体（前端传递项目名称、级别等，无需传递工作量）
     * @return 新增结果
     */
    @PostMapping("/add")
    public AjaxResult addScienceInnovation(@RequestBody ScienceInnovation innovation) {
        if (StringUtils.isEmpty(innovation.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        innovation.setUserId(Long.valueOf(userId));
        innovation.setUserName(userName);

        // 自动计算工作量（复用实验课的计算逻辑结构，按科技创新规则计算）
        double workload = scienceInnovationService.countWorkload(innovation.getUserId(), innovation);
        innovation.setWorkload(BigDecimal.valueOf(workload));

        // 审计字段默认值（若前端未传递）
        innovation.setCreateTime(innovation.getCreateTime() == null ? LocalDateTime.now() : innovation.getCreateTime());
        innovation.setUpdateTime(innovation.getUpdateTime() == null ? LocalDateTime.now() : innovation.getUpdateTime());

        boolean x = !innovation.getYear().equals(innovation.getEndYear());
        boolean success = scienceInnovationService.addScienceInnovation(innovation);
        if (success) {
            // 新增成功后，更新当前用户当前年度的科技创新总工作量（与实验课总表逻辑一致）
            scienceInnovationService.countTotalWorkload(innovation.getUserId(), innovation.getYear());
        }

        if(success && x){
            return AjaxResult.success("新增科技创新项目成功,请选择"+innovation.getEndYear()+"年页面查看", innovation);
        }else {
            return success ? AjaxResult.success("新增科技创新项目成功", innovation) : AjaxResult.error("新增失败");
        }

    }

    /**
     * 3. 删除科技创新项目（支持批量删除，删除后更新总工作量）
     * @param ids 项目ID数组
     * @param year 页面选中年份（用于更新总工作量）
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteScienceInnovation(@RequestBody Long[] ids, @RequestParam(required = false) Integer year) {
        String userId = SecurityUtils.getUsername();

        boolean success = scienceInnovationService.removeScienceInnovationByIds(Arrays.asList(ids));
        if (success) {
            // 删除成功后，更新当前用户当前年度的科技创新总工作量
            scienceInnovationService.countTotalWorkload(Long.valueOf(userId), year);
        }
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 4. 更新科技创新项目（重新计算工作量+更新总工作量）
     * @param innovation 项目实体（含更新数据）
     * @return 更新结果
     */
    @PutMapping("/update")
    public AjaxResult updateScienceInnovation(@RequestBody ScienceInnovation innovation) {
        if (StringUtils.isEmpty(innovation.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        innovation.setUserId(Long.valueOf(userId));
        innovation.setUpdateTime(LocalDateTime.now());

        // 1. 重新计算工作量
        double workload = scienceInnovationService.countWorkload(innovation.getUserId(), innovation);
        innovation.setWorkload(BigDecimal.valueOf(workload));

        // 2. 保存原始年份（关键：用于后续旧年份总工作量更新）
        Integer originalYear = innovation.getYear(); // 旧年份
        Integer newYear = innovation.getEndYear();   // 新年份
        boolean yearChanged = !originalYear.equals(newYear); // 判断年份是否变更

        // 3. 更新年份字段为新年份
        innovation.setYear(newYear);
        // 将审核状态修改为待审核
        innovation.setStatus("待审核");

        // 4. 执行数据库更新
        boolean success = scienceInnovationService.updateById(innovation);

        if (success) {
            // 5. 年份变更时，同步更新旧年份和新年份的总工作量
            if (yearChanged) {
                // 旧年份：剔除已迁移数据
                scienceInnovationService.countTotalWorkload(innovation.getUserId(), originalYear);
                // 新年份：新增数据
                scienceInnovationService.countTotalWorkload(innovation.getUserId(), newYear);
            } else {
                // 年份未变更，仅更新当前年份
                scienceInnovationService.countTotalWorkload(innovation.getUserId(), newYear);
            }
        }

        // 6. 返回提示信息
        if (success && yearChanged) {
            return AjaxResult.success("更新成功，请切换至" + newYear + "年页面查看", innovation);
        } else {
            return success ? AjaxResult.success("更新成功", innovation) : AjaxResult.error("更新失败");
        }
    }


    /**
     * 5. 审核科技创新
     * @param innovation 科技创新审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditScienceInnovation(@RequestBody ScienceInnovation innovation) {
        try {
            // 调用Service层审核方法
            boolean success = scienceInnovationService.auditScienceInnovation(innovation.getId(), innovation.getStatus(), innovation.getRemark());

            if (success) {
                String message = "已通过".equals(innovation.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = innovation.getId();
                innovation = scienceInnovationService.getById(id);
                scienceInnovationService.countTotalWorkload(innovation.getUserId(), innovation.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}
