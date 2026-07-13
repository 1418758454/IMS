package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.GraduateTheoryCourse;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.GraduateTheoryCourseService;
import com.ruoyi.manage.service.teaching.TeachingTaskScreenshotAttachmentService;
import com.ruoyi.manage.utils.AdminAuditUpdateUtils;
import org.apache.ibatis.annotations.DeleteProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 研究生理论课控制器
 * 处理前端研究生理论课相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/teaching/graduateTheoryCourse") // 研究生理论课模块接口根路径
public class GraduateTheoryCourseController {

    @Autowired
    private GraduateTheoryCourseService graduateTheoryCourseService; // 注入研究生理论课Service
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTaskScreenshotAttachmentService taskScreenshotAttachmentService;

    /**
     * 1. 获取研究生理论课列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     授课年份筛选（可选，如2023/2024）
     * @return 分页结果（包含理论课列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getGraduateTheoryCourseList(
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
                IPage<GraduateTheoryCourse> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<GraduateTheoryCourse> page = graduateTheoryCourseService.getGraduateTheoryCoursePageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<GraduateTheoryCourse> page = graduateTheoryCourseService.getGraduateTheoryCoursePage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增研究生理论课
     * @param course 研究生理论课实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    public AjaxResult addGraduateTheoryCourse(@RequestBody GraduateTheoryCourse course) {
        String userId = SecurityUtils.getUsername();
        if (!taskScreenshotAttachmentService.hasAttachment(Long.valueOf(userId), course.getYear(), "graduate_theory")) {
            return AjaxResult.error("请先上传个人教学任务截图（PDF）后再新增课程");
        }
        String userName = basicInformationService.getByloginId(userId).getName();
        course.setUserId(Long.valueOf(userId));
        course.setUserName(userName);

        double workload = graduateTheoryCourseService.countWorkload(course.getUserId(), course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setCreateTime(course.getCreateTime() == null ? LocalDateTime.now() : course.getCreateTime());
        course.setUpdateTime(course.getUpdateTime() == null ? LocalDateTime.now() : course.getUpdateTime());

        boolean success = graduateTheoryCourseService.addGraduateTheoryCourse(course);
        if (success) {
            graduateTheoryCourseService.countTotalWorkload(course.getUserId(), course.getYear());
        }

        return success ? AjaxResult.success("新增研究生理论课成功", course) : AjaxResult.error("新增研究生理论课失败");
    }

    /**
     * 3. 删除研究生理论课（支持批量删除）
     * @param ids 研究生理论课ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteGraduateTheoryCourse(@RequestBody Long[] ids, @RequestParam(required = false) Integer year) {
        String userId = SecurityUtils.getUsername();

        boolean success = graduateTheoryCourseService.removeGraduateTheoryCourseByIds(Arrays.asList(ids));
        if (success) {
            graduateTheoryCourseService.countTotalWorkload(Long.valueOf(userId), year);
        }
        return success ? AjaxResult.success("删除研究生理论课成功") : AjaxResult.error("删除研究生理论课失败");
    }

    /**
     * 4. 更新研究生理论课
     * @param course 研究生理论课实体（前端传递的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    public AjaxResult updateGraduateTheoryCourse(@RequestBody GraduateTheoryCourse course,
            @RequestParam(defaultValue = "false") boolean auditEdit) {
        String userId = SecurityUtils.getUsername();
        course.setUserId(Long.valueOf(userId));
        AdminAuditUpdateUtils.preserve(graduateTheoryCourseService.getById(course.getId()), course, auditEdit);
        double workload = graduateTheoryCourseService.countWorkload(course.getUserId(), course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setUpdateTime(LocalDateTime.now());
        // 将审核状态修改为待审核
        course.setStatus("待审核");

        AdminAuditUpdateUtils.preserve(graduateTheoryCourseService.getById(course.getId()), course, auditEdit);
        boolean success = graduateTheoryCourseService.updateById(course);
        if (success) {
            graduateTheoryCourseService.countTotalWorkload(course.getUserId(), course.getYear());
        }
        return success ? AjaxResult.success("更新研究生理论课成功", course) : AjaxResult.error("更新研究生理论课失败");
    }

    /**
     * 5. 审核研究生理论课
     * @param course 研究生理论课审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditGraduateTheoryCourse(@RequestBody GraduateTheoryCourse course) {
        try {
            // 调用Service层审核方法
            boolean success = graduateTheoryCourseService.auditGraduateTheoryCourse(course.getId(), course.getStatus(), course.getRemark());

            if (success) {
                String message = "已通过".equals(course.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = course.getId();
                course = graduateTheoryCourseService.getById(id);
                graduateTheoryCourseService.countTotalWorkload(course.getUserId(), course.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}
