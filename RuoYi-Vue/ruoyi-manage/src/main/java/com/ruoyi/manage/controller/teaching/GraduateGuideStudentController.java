package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.GraduateGuideStudent;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.GraduateGuideStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 研究生指导学生Controller（工作量直接保存前端输入值）
 */
@RestController
@RequestMapping("/manage/teaching/graduateGuideStudent") // 接口路径与表名对应
public class GraduateGuideStudentController {

    @Autowired
    private GraduateGuideStudentService graduateGuideStudentService;
    @Autowired
    private BasicInformationService basicInformationService;

    // 1. 分页查询（复用架构，替换实体类）
    @GetMapping("/list")
    public AjaxResult getGraduateGuideStudentList(
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
                IPage<GraduateGuideStudent> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<GraduateGuideStudent> page = graduateGuideStudentService.getGraduateGuideStudentPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<GraduateGuideStudent> page = graduateGuideStudentService.getGraduateGuideStudentPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    // 2. 新增指导记录（直接保存前端工作量，无需处理日期提取年份）
    @PostMapping("/add")
    public AjaxResult addGraduateGuideStudent(@RequestBody GraduateGuideStudent guideStudent) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        guideStudent.setUserId(Long.valueOf(userId));
        guideStudent.setUserName(userName);

        // 审计字段默认值
        guideStudent.setCreateTime(guideStudent.getCreateTime() == null ? LocalDateTime.now() : guideStudent.getCreateTime());
        guideStudent.setUpdateTime(guideStudent.getUpdateTime() == null ? LocalDateTime.now() : guideStudent.getUpdateTime());

        boolean success = graduateGuideStudentService.addGraduateGuideStudent(guideStudent);
        if (success) {
            graduateGuideStudentService.countTotalWorkload(guideStudent.getUserId(), guideStudent.getYear()); // 更新总工作量
        }

        return success ? AjaxResult.success("新增成功", guideStudent) : AjaxResult.error("新增失败");
    }

    // 3. 更新指导记录（直接保存前端工作量，无需日期提取年份）
    @PutMapping("/update")
    public AjaxResult updateGraduateGuideStudent(@RequestBody GraduateGuideStudent guideStudent,
            @RequestParam(defaultValue = "false") boolean auditEdit) {
        String userId = SecurityUtils.getUsername();
        guideStudent.setUserId(Long.valueOf(userId));
        guideStudent.setUpdateTime(LocalDateTime.now());

        Integer originalYear = guideStudent.getYear(); // 原始年份（用于总工作量更新）
        // 将审核状态修改为待审核
        guideStudent.setStatus("待审核");

        com.ruoyi.manage.utils.AdminAuditUpdateUtils.preserve(graduateGuideStudentService.getById(guideStudent.getId()), guideStudent, auditEdit);
        boolean success = graduateGuideStudentService.updateById(guideStudent);

        if (success) {
            graduateGuideStudentService.countTotalWorkload(guideStudent.getUserId(), originalYear); // 更新总工作量
        }

        return success ? AjaxResult.success("更新成功", guideStudent) : AjaxResult.error("更新失败");
    }

    // 4. 批量删除（复用架构）
    @DeleteMapping("/delete")
    public AjaxResult deleteGraduateGuideStudent(@RequestBody Long[] ids, @RequestParam Integer year) {
        String userId = SecurityUtils.getUsername();
        List<Long> idList = Arrays.asList(ids);
        boolean success = graduateGuideStudentService.removeGraduateGuideStudentByIds(idList);

        if (success) {
            graduateGuideStudentService.countTotalWorkload(Long.valueOf(userId), year); // 更新总工作量
        }

        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 5. 审核研究生指导学生
     * @param guideStudent 研究生指导学生审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditGraduateGuideStudent(@RequestBody GraduateGuideStudent guideStudent) {
        try {
            // 调用Service层审核方法
            boolean success = graduateGuideStudentService.auditGraduateGuideStudent(guideStudent.getId(), guideStudent.getStatus(), guideStudent.getRemark());

            if (success) {
                String message = "已通过".equals(guideStudent.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = guideStudent.getId();
                guideStudent = graduateGuideStudentService.getById(id);
                graduateGuideStudentService.countTotalWorkload(guideStudent.getUserId(), guideStudent.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}
