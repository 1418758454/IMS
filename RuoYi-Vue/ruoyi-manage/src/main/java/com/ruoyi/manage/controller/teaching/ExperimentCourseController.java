package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.ExperimentCourse;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.ExperimentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 本科实验课控制器
 * 处理前端实验课相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/teaching/experimentCourse") // 实验课模块接口根路径
public class ExperimentCourseController {

    @Autowired
    private ExperimentCourseService experimentCourseService; // 注入实验课Service
    @Autowired
    private BasicInformationService basicInformationService;


    /**
     * 1. 获取实验课列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     授课年份筛选（可选，如2023/2024）
     * @return 分页结果（包含实验课列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getExperimentCourseList(
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
                IPage<ExperimentCourse> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<ExperimentCourse> page = experimentCourseService.getExperimentCoursePageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<ExperimentCourse> page = experimentCourseService.getExperimentCoursePage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增实验课
     * @param course 实验课实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    public AjaxResult addExperimentCourse(@RequestBody ExperimentCourse course) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        course.setUserId(Long.valueOf(userId));
        course.setUserName(userName);

        // 计算工作量（复用理论课计算逻辑，保持一致）
        double workload = experimentCourseService.countWorkload(course.getUserId(), course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setCreateTime(course.getCreateTime() == null ? LocalDateTime.now() : course.getCreateTime());
        course.setUpdateTime(course.getUpdateTime() == null ? LocalDateTime.now() : course.getUpdateTime());

        boolean success = experimentCourseService.addExperimentCourse(course);
        if (success) {
            // 更新当前用户的当前年度的实验课模块的所有工作量
            experimentCourseService.countTotalWorkload(course.getUserId(), course.getYear());
        }

        return success ? AjaxResult.success("新增实验课成功", course) : AjaxResult.error("新增实验课失败");
    }

    /**
     * 3. 删除实验课（支持批量删除）
     * @param ids 实验课ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteExperimentCourse(@RequestBody Long[] ids, @RequestParam(required = false) Integer year) {
        String userId = SecurityUtils.getUsername();

        boolean success = experimentCourseService.removeExperimentCourseByIds(Arrays.asList(ids));
        if (success) {
            // 删除成功，更新当前用户的实验课模块的所有工作量
            experimentCourseService.countTotalWorkload(Long.valueOf(userId), year);
        }
        return success ? AjaxResult.success("删除实验课成功") : AjaxResult.error("删除实验课失败");
    }

    /**
     * 4. 更新实验课
     * @param course 实验课实体（前端传递的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    public AjaxResult updateExperimentCourse(@RequestBody ExperimentCourse course) {
        String userId = SecurityUtils.getUsername();
        course.setUserId(Long.valueOf(userId)); // 设置当前用户ID
        // 计算工作量（复用理论课计算逻辑，保持一致）
        double workload = experimentCourseService.countWorkload(course.getUserId(), course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setUpdateTime(LocalDateTime.now());
        // 将审核状态修改为待审核
        course.setStatus("待审核");

        boolean success = experimentCourseService.updateById(course); // todo
        if (success) {
            // 更新成功，更新当前用户的实验课模块的所有工作量
            experimentCourseService.countTotalWorkload(course.getUserId(), course.getYear());
        }
        return success ? AjaxResult.success("更新实验课成功", course) : AjaxResult.error("更新实验课失败");
    }

    /**
     * 5. 审核实验课
     * @param course 实验课审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditExperimentCourse(@RequestBody ExperimentCourse course) {
        try {
            // 调用Service层审核方法
            boolean success = experimentCourseService.auditExperimentCourse(course.getId(), course.getStatus(), course.getRemark());

            if (success) {
                String message = "已通过".equals(course.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = course.getId();
                course = experimentCourseService.getById(id);
                experimentCourseService.countTotalWorkload(course.getUserId(), course.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}