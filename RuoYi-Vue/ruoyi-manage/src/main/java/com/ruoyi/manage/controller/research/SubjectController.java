package com.ruoyi.manage.controller.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchSubject;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课题模块控制器
 * 处理前端课题相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/research/subject") // 接口根路径
public class SubjectController {

    @Autowired // 注入Service层
    private SubjectService subjectService;

    @Autowired
    private BasicInformationService basicInformationService;

    /**
     * 1. 获取课题列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     年份筛选（可选，如2024/2025/2026）
     * @return 分页结果（包含课题列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getSubjectList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String year, // 新增：年份参数（非必填）
            @RequestParam(required = false) String userId, // 如果前端有返回id，则使用id查询，否则使用登录用户名查询
            @RequestParam(required = false) Long deptId, // 新增：部门ID参数
            @RequestParam(required = false) Boolean isAudit // 新增：当前是否为审核页面
    ) {
//        Boolean isAudit = true; // 将审核页面与普通科研页区分开，没有传入id的即为普通页面
        System.out.println("当前页面是否为审核页面："+isAudit);
        if("all".equals(userId)){ // 如果用户id为all，则查询该部门所有用户的课题数据
            // 获取该部门所有用户ID列表
            List<BasicInformation> basicInformationList = basicInformationService.listByDept(deptId);
            // 将查到的用户信息的id提取到userIds列表中
            List<String> userIds = basicInformationList.stream().map(BasicInformation::getUserId).collect(Collectors.toList());
//            System.out.println("用户id为:" + userIds);
            if(userIds.isEmpty()){
                // 如果该部门没有用户，返回空数据
                IPage<ResearchSubject> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            // 查询该部门所有用户的课题数据
            IPage<ResearchSubject> page = subjectService.getSubjectPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        // 如果没有传入userId，则根据登录用户名查询登录用户课题列表
        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        // 调用Service层分页查询方法
        IPage<ResearchSubject> page = subjectService.getSubjectPage(pageNum, pageSize, userId,year, isAudit);
        return AjaxResult.success(page); // 返回统一响应格式
    }

    /**
     * 2. 新增课题
     * @param subject 课题实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    public AjaxResult addSubject(@RequestBody ResearchSubject subject) {
        // 获取登录用户名
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        subject.setUserId(Long.valueOf(userId)); // 设置创建人ID
        subject.setUserName(userName);

        //计算工作量
        BigDecimal money = subject.getMoney();
        BigDecimal coefficient = subject.getCoefficient(); // 获取系数
        BigDecimal rank = subject.getRank(); // 获取排名比例
        BigDecimal workload = money.multiply(coefficient).multiply(rank);
        subject.setWorkload(workload);

        // 审计字段默认值（若前端未传递）
        subject.setCreateTime(subject.getCreateTime() == null ? LocalDateTime.now() : subject.getCreateTime());
        subject.setUpdateTime(subject.getUpdateTime() == null ? LocalDateTime.now() : subject.getUpdateTime());

        // 将审核状态修改为待审核
        subject.setStatus("待审核");

        // 给登录用户新增课题
        boolean success = subjectService.addSubject(subject);
        return success ? AjaxResult.success("新增课题成功") : AjaxResult.error("新增课题失败");

    }

    /**
     * 3. 删除课题（支持批量删除）
     * @param ids 课题ID数组（前端传递的选中行ID）
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteSubject(@RequestBody Long[] ids) {
        boolean success = subjectService.removeSubjectByIds(Arrays.asList(ids));
        return success ? AjaxResult.success("删除课题成功") : AjaxResult.error("删除课题失败");
    }

    /**
     * 4. 更新课题
     * @param subject 课题实体（包含ID和更新的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    public AjaxResult updateSubject(@RequestBody ResearchSubject subject) {
        System.out.println("更新课题:"+subject.getUserId());
        // 获取登录用户名
        String userId = SecurityUtils.getUsername();
        subject.setUserId(Long.valueOf(userId));

        // 重新计算工作量
        BigDecimal money = subject.getMoney();
        BigDecimal coefficient = subject.getCoefficient(); // 获取系数
        BigDecimal rank = subject.getRank(); // 获取排名比例
        BigDecimal workload = money.multiply(coefficient).multiply(rank);
        subject.setWorkload(workload);

        // 更新审计字段
        subject.setUpdateTime(LocalDateTime.now());

        // 将审核状态修改为待审核
        subject.setStatus("待审核");

        // 更新课题信息
        boolean success = subjectService.updateSubject(subject);
        return success ? AjaxResult.success("更新课题成功") : AjaxResult.error("更新课题失败");
    }

    /**
     * 5. 审核课题
     * @param subject 课题审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditSubject(@RequestBody ResearchSubject subject) {
        try {
            // 获取审核人（当前登录用户）
//            String auditor = SecurityUtils.getUsername();

            // 调用Service层审核方法
            boolean success = subjectService.auditSubject(subject.getId(), subject.getStatus(), subject.getRemark());

            if (success) {
                String message = "已通过".equals(subject.getStatus()) ? "审核通过成功" : "退回修改成功";
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}