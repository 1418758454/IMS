package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.PracticeCourse;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.PracticeCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 本科教学实践控制器
 * 处理前端教学实践相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/teaching/practiceTeaching") // 教学实践模块接口根路径
public class PracticeCourseController {

    @Autowired
    private PracticeCourseService practiceCourseService; // 注入教学实践Service
    @Autowired
    private BasicInformationService basicInformationService;


    /**
     * 1. 获取教学实践列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     授课年份筛选（可选，如2023/2024）
     * @return 分页结果（包含教学实践列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getPracticeCourseList(
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
                IPage<PracticeCourse> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<PracticeCourse> page = practiceCourseService.getPracticeCoursePageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<PracticeCourse> page = practiceCourseService.getPracticeCoursePage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增教学实践
     * @param course 教学实践实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    public AjaxResult addPracticeCourse(@RequestBody PracticeCourse course) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        course.setUserId(Long.valueOf(userId));
        course.setUserName(userName);

        // 计算工作量（复用实验课计算逻辑，字段替换为实践教学的planDays和classCount）
        double workload = practiceCourseService.countWorkload(course.getUserId(), course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setCreateTime(course.getCreateTime() == null ? LocalDateTime.now() : course.getCreateTime());
        course.setUpdateTime(course.getUpdateTime() == null ? LocalDateTime.now() : course.getUpdateTime());

        boolean success = practiceCourseService.addPracticeCourse(course);
        if (success) {
            // 更新当前用户的当前年度的教学实践模块总工作量
            practiceCourseService.countTotalWorkload(course.getUserId(), course.getYear());
        }

        return success ? AjaxResult.success("新增教学实践成功", course) : AjaxResult.error("新增教学实践失败");
    }

    /**
     * 3. 删除教学实践（支持批量删除）
     * @param ids 教学实践ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    public AjaxResult deletePracticeCourse(@RequestBody Long[] ids, @RequestParam(required = false) Integer year) {
        String userId = SecurityUtils.getUsername();

        boolean success = practiceCourseService.removePracticeCourseByIds(Arrays.asList(ids));
        if (success) {
            // 删除成功后更新总工作量
            practiceCourseService.countTotalWorkload(Long.valueOf(userId), year);
        }
        return success ? AjaxResult.success("删除教学实践成功") : AjaxResult.error("删除教学实践失败");
    }

    /**
     * 4. 更新教学实践
     * @param course 教学实践实体（前端传递的更新数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    public AjaxResult updatePracticeCourse(@RequestBody PracticeCourse course) {
        String userId = SecurityUtils.getUsername();
        course.setUserId(Long.valueOf(userId)); // 设置当前用户ID
        // 重新计算工作量（字段替换为planDays和classCount）
        double workload = practiceCourseService.countWorkload(course.getUserId(), course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setUpdateTime(LocalDateTime.now());
        // 将审核状态修改为待审核
        course.setStatus("待审核");

        boolean success = practiceCourseService.updateById(course);
        if (success) {
            // 更新总工作量
            practiceCourseService.countTotalWorkload(course.getUserId(), course.getYear());
        }
        return success ? AjaxResult.success("更新教学实践成功", course) : AjaxResult.error("更新教学实践失败");
    }

    /**
     * 5. 审核教学实践
     * @param course 教学实践审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditPracticeCourse(@RequestBody PracticeCourse course) {
        try {
            // 调用Service层审核方法
            boolean success = practiceCourseService.auditPracticeCourse(course.getId(), course.getStatus(), course.getRemark());

            if (success) {
                String message = "已通过".equals(course.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = course.getId();
                course = practiceCourseService.getById(id);
                practiceCourseService.countTotalWorkload(course.getUserId(), course.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}