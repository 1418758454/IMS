package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.teaching.EducationReform;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.EducationReformPaper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.EducationReformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


/**
 * 教改项目Controller（核心调整：移除工作量计算，直接保存前端输入值）
 */
@RestController
@RequestMapping("/manage/teaching/educationReform") // 接口路径：替换为教改项目
public class EducationReformController {

    @Autowired
    private EducationReformService educationReformService;
    @Autowired
    private BasicInformationService basicInformationService;


    // 1. 分页查询（复用科技创新逻辑，替换实体类）
    @GetMapping("/list")
    public AjaxResult getEducationReformList(
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
                IPage<EducationReform> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<EducationReform> page = educationReformService.getEducationReformPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<EducationReform> page = educationReformService.getEducationReformPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    // 2. 新增项目（直接保存前端工作量，从endDate提取年份）
    @PostMapping("/add")
    public AjaxResult addEducationReform(@RequestBody EducationReform reform) {
        if (StringUtils.isEmpty(reform.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        reform.setUserId(Long.valueOf(userId));
        reform.setUserName(userName);

        // 审计字段默认值
        reform.setCreateTime(reform.getCreateTime() == null ? LocalDateTime.now() : reform.getCreateTime());
        reform.setUpdateTime(reform.getUpdateTime() == null ? LocalDateTime.now() : reform.getUpdateTime());


        // 判断页面年份与出版年份是否一致（用于提示信息）
        Integer publishYear = reform.getEndDate().getYear();
        boolean yearMismatch = !reform.getYear().equals(publishYear);
        boolean success = educationReformService.addEducationReform(reform);

        if (success) {
            educationReformService.countTotalWorkload(reform.getUserId(), publishYear); // 更新总工作量
        }

        if (success && yearMismatch) {
            return AjaxResult.success("新增成功，请选择" + publishYear + "年页面查看", reform);
        } else {
            return success ? AjaxResult.success("新增成功", reform) : AjaxResult.error("新增失败");
        }
    }

    // 3. 更新项目（直接保存前端工作量，调整年份提取逻辑）
    @PutMapping("/update")
    public AjaxResult updateEducationReform(@RequestBody EducationReform reform) {
        if (StringUtils.isEmpty(reform.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        reform.setUserId(Long.valueOf(userId));
        reform.setUpdateTime(LocalDateTime.now());

        // 核心：从endDate提取年份（替换科技创新的endYear）
        Integer newYear = reform.getEndDate().getYear();
        Integer originalYear = reform.getYear(); // 原始年份（用于总工作量更新
        boolean yearChanged = !originalYear.equals(newYear);

        reform.setYear(newYear); // 更新年份字段
        // 将审核状态修改为待审核
        reform.setStatus("待审核");

        boolean success = educationReformService.updateById(reform);
        if (success) {
            // 年份变更时，更新原年份和新年份的总工作量
            if (!originalYear.equals(newYear)) {
                educationReformService.countTotalWorkload(reform.getUserId(), originalYear); // 原年份
                educationReformService.countTotalWorkload(reform.getUserId(), newYear);     // 新年份
            } else {
                educationReformService.countTotalWorkload(reform.getUserId(), newYear); // 未变更则仅更新当前年份
            }
        }

        if (success && yearChanged) {
            return AjaxResult.success("更新成功，请切换至" + newYear + "年页面查看", reform);
        } else {
            return success ? AjaxResult.success("更新成功", reform) : AjaxResult.error("更新失败");
        }
    }

    // 4. 批量删除（复用科技创新逻辑，替换Service方法）
    @DeleteMapping("/delete")
    public AjaxResult deleteEducationReform(@RequestBody Long[] ids, @RequestParam Integer year) {
        String userId = SecurityUtils.getUsername();
        List<Long> idList = Arrays.asList(ids);
        boolean success = educationReformService.removeEducationReformByIds(idList);

        if (success) {
            // 删除后更新总工作量
            educationReformService.countTotalWorkload(Long.valueOf(userId), year);
        }

        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 5. 审核教改论文
     * @param reform 教改论文审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditEducationReform(@RequestBody EducationReform reform) {
        try {
            // 调用Service层审核方法
            boolean success = educationReformService.auditEducationReform(reform.getId(), reform.getStatus(), reform.getRemark());

            if (success) {
                String message = "已通过".equals(reform.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = reform.getId();
                reform = educationReformService.getById(id);
                educationReformService.countTotalWorkload(reform.getUserId(), reform.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }
}
