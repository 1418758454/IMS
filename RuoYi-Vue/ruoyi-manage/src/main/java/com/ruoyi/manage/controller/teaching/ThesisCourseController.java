package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.ThesisCourse;

import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.ThesisCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 本科毕业论文控制器
 * 处理前端毕业论文相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/teaching/thesisCourse") // 毕业论文模块接口根路径
public class ThesisCourseController {

    @Autowired
    private ThesisCourseService thesisCourseService; // 注入毕业论文Service
    @Autowired
    private BasicInformationService basicInformationService; // 注入用户信息Service


    /**
     * 1. 获取毕业论文列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     授课年份筛选（可选，如2023/2024）
     * @return 分页结果（包含毕业论文列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getThesisCourseList(
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
                IPage<ThesisCourse> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<ThesisCourse> page = thesisCourseService.getThesisCoursePageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<ThesisCourse> page = thesisCourseService.getThesisCoursePage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增毕业论文记录
     * @param course 毕业论文实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    public AjaxResult addThesisCourse(@RequestBody ThesisCourse course) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        course.setUserId(Long.valueOf(userId));
        course.setUserName(userName);

        // 计算工作量（复用实验课计算逻辑框架，调整参数）
        double workload = thesisCourseService.countWorkload(course.getUserId(), course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setCreateTime(course.getCreateTime() == null ? LocalDateTime.now() : course.getCreateTime());
        course.setUpdateTime(course.getUpdateTime() == null ? LocalDateTime.now() : course.getUpdateTime());

        boolean success = thesisCourseService.addThesisCourse(course);
        if (success) {
            // 更新当前用户的当前年度的毕业论文模块的总工作量
            thesisCourseService.countTotalWorkload(course.getUserId(), course.getYear());
        }

        return success ? AjaxResult.success("新增毕业论文记录成功", course) : AjaxResult.error("新增毕业论文记录失败");
    }

    /**
     * 3. 删除毕业论文记录（支持批量删除）
     * @param ids 毕业论文ID数组
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteThesisCourse(@RequestBody Long[] ids, @RequestParam(required = false) Integer year) {
        String userId = SecurityUtils.getUsername();

        boolean success = thesisCourseService.removeThesisCourseByIds(Arrays.asList(ids));
        if (success) {
            // 删除成功，更新当前用户的毕业论文模块的总工作量
            thesisCourseService.countTotalWorkload(Long.valueOf(userId), year);
        }
        return success ? AjaxResult.success("删除毕业论文记录成功") : AjaxResult.error("删除毕业论文记录失败");
    }

    /**
     * 4. 更新毕业论文记录
     * @param course 毕业论文实体（前端传递的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    public AjaxResult updateThesisCourse(@RequestBody ThesisCourse course) {
        String userId = SecurityUtils.getUsername();
        course.setUserId(Long.valueOf(userId)); // 设置当前用户ID
        // 计算工作量（复用实验课计算逻辑框架，调整参数）
        double workload = thesisCourseService.countWorkload(course.getUserId(), course);
        course.setWorkload(BigDecimal.valueOf(workload));

        course.setUpdateTime(LocalDateTime.now());
        // 将审核状态修改为待审核
        course.setStatus("待审核");

        boolean success = thesisCourseService.updateById(course);
        if (success) {
            // 更新成功，更新当前用户的毕业论文模块的总工作量
            thesisCourseService.countTotalWorkload(course.getUserId(), course.getYear());
        }
        return success ? AjaxResult.success("更新毕业论文记录成功", course) : AjaxResult.error("更新毕业论文记录失败");
    }

    /**
     * 5. 审核毕业论文
     * @param course 毕业论文审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditThesisCourse(@RequestBody ThesisCourse course) {
        try {
            // 调用Service层审核方法
            boolean success = thesisCourseService.auditThesisCourse(course.getId(), course.getStatus(), course.getRemark());

            if (success) {
                String message = "已通过".equals(course.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = course.getId();
                course = thesisCourseService.getById(id);
                thesisCourseService.countTotalWorkload(course.getUserId(), course.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}