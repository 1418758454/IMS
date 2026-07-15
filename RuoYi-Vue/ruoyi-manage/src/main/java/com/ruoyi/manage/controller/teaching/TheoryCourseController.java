package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.WorkloadScope;
import com.ruoyi.manage.domain.research.ResearchSoftware;
import com.ruoyi.manage.domain.research.ResearchSubject;
import com.ruoyi.manage.domain.teaching.UndergraduateTheoryCourse;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.WorkloadRefreshService;
import com.ruoyi.manage.service.teaching.TeachingTaskScreenshotAttachmentService;
import com.ruoyi.manage.service.teaching.TheoryCourseService;
import com.ruoyi.manage.utils.AdminAuditUpdateUtils;
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
 * 本科理论课控制器
 * 处理前端理论课相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/teaching/theoryCourse") // 理论课模块接口根路径
public class TheoryCourseController {

    @Autowired
    private TheoryCourseService theoryCourseService; // 注入理论课Service
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTaskScreenshotAttachmentService taskScreenshotAttachmentService;
    @Autowired
    private WorkloadRefreshService workloadRefreshService;


    /**
     * 1. 获取理论课列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     授课年份筛选（可选，如2023/2024）
     * @return 分页结果（包含理论课列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getTheoryCourseList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer year, // 按年份筛选（对应表中year字段）
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Boolean isAudit // 新增：当前是否为审核页面
    ) {
        if("all".equals(userId)){
            List<BasicInformation> basicInformationList = basicInformationService.listByDept(deptId);
            List<String> userIds = basicInformationList.stream().map(BasicInformation::getUserId).collect(Collectors.toList());
            if(userIds.isEmpty()){
                IPage<UndergraduateTheoryCourse> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<UndergraduateTheoryCourse> page = theoryCourseService.getTheoryCoursePageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<UndergraduateTheoryCourse> page = theoryCourseService.getTheoryCoursePage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);

//        String userId = SecurityUtils.getUsername(); // 获取当前用户ID（需确保与用户表ID类型一致）
//        IPage<UndergraduateTheoryCourse> page = theoryCourseService.getTheoryCoursePage(pageNum, pageSize, userId, year);
//        return AjaxResult.success(page);
    }

    /**
     * 2. 新增理论课
     * @param course 理论课实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addTheoryCourse(@RequestBody UndergraduateTheoryCourse course) {
        String userId = SecurityUtils.getUsername();
        if (!taskScreenshotAttachmentService.hasAttachment(Long.valueOf(userId), course.getYear(), "theory")) {
            return AjaxResult.error("请先上传个人教学任务截图（PDF）后再新增课程");
        }
        String userName = basicInformationService.getByloginId(userId).getName();
        course.setUserId(Long.valueOf(userId));
        course.setUserName(userName);

        // 计算工作量
        // 计算当前行数据的工作量
        double workload = theoryCourseService.countWorkload(course.getUserId(),course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setCreateTime(course.getCreateTime() == null ? LocalDateTime.now() : course.getCreateTime());
        course.setUpdateTime(course.getUpdateTime() == null ? LocalDateTime.now() : course.getUpdateTime());

        boolean success = theoryCourseService.addTheoryCourse(course);
        if (success) {
            // 更新当前用户的当前年度的理论课模块的所有工作量
            workloadRefreshService.refreshTeaching(course.getUserId(), course.getYear());
            // System.out.println("总工作量：" + totalWorkload);
        }

        return success ? AjaxResult.success("新增理论课成功", course) : AjaxResult.error("新增理论课失败");
    }

    /**
     * 3. 删除理论课（支持批量删除）
     * @param ids 理论课ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteTheoryCourse(@RequestBody Long[] ids, @RequestParam(required = false) Integer year) {
        List<UndergraduateTheoryCourse> originals = theoryCourseService.listByIds(Arrays.asList(ids));
        boolean success = theoryCourseService.removeTheoryCourseByIds(Arrays.asList(ids));
        if (success) {
            // 删除成功，更新当前用户的理论课模块的所有工作量
            workloadRefreshService.refreshTeaching(originals.stream()
                    .map(item -> WorkloadScope.of(item.getUserId(), item.getYear()))
                    .collect(Collectors.toList()));
        }
        return success ? AjaxResult.success("删除理论课成功") : AjaxResult.error("删除理论课失败");
    }

    /**
     * 4. 更新理论课
     * @param course 理论课实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateTheoryCourse(@RequestBody UndergraduateTheoryCourse course,
            @RequestParam(defaultValue = "false") boolean auditEdit) {
        UndergraduateTheoryCourse originalCourse = theoryCourseService.getById(course.getId());
        String userId = SecurityUtils.getUsername();
        course.setUserId(Long.valueOf(userId)); // 设置当前用户ID
        AdminAuditUpdateUtils.preserve(originalCourse, course, auditEdit);
        // 计算工作量
        double workload = theoryCourseService.countWorkload(course.getUserId(),course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setUpdateTime(LocalDateTime.now());
        // 将审核状态修改为待审核
        course.setStatus("待审核");

        AdminAuditUpdateUtils.preserve(originalCourse, course, auditEdit);
        boolean success = theoryCourseService.updateById(course);
        if (success) {
            // 更新成功，更新当前用户的理论课模块的所有工作量
            workloadRefreshService.refreshTeaching(Arrays.asList(
                    WorkloadScope.of(originalCourse.getUserId(), originalCourse.getYear()),
                    WorkloadScope.of(course.getUserId(), course.getYear())
            ));
        }
        return success ? AjaxResult.success("更新理论课成功", course) : AjaxResult.error("更新理论课失败");
    }

    /**
     * 5. 审核理论课
     * @param course 理论课审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditTheoryCourse(@RequestBody UndergraduateTheoryCourse course) {
        try {
            UndergraduateTheoryCourse originalCourse = theoryCourseService.getById(course.getId());
            // 调用Service层审核方法
            boolean success = theoryCourseService.auditTheoryCourse(course.getId(), course.getStatus(), course.getRemark());

            if (success) {
                String message = "已通过".equals(course.getStatus()) ? "审核通过成功" : "退回修改成功";
                workloadRefreshService.refreshTeaching(originalCourse.getUserId(), originalCourse.getYear());
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
