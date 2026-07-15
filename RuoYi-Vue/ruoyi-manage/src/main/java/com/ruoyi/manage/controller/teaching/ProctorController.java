package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.WorkloadScope;
import com.ruoyi.manage.domain.teaching.Proctor;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.WorkloadRefreshService;
import com.ruoyi.manage.service.teaching.ProctorService;
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
 * 监考模块控制器
 * 处理前端监考相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/teaching/proctor") // 监考模块接口根路径
public class ProctorController {

    @Autowired
    private WorkloadRefreshService workloadRefreshService;

    @Autowired
    private ProctorService proctorService; // 注入监考Service
    @Autowired
    private BasicInformationService basicInformationService;

    /**
     * 1. 获取监考记录列表（分页+年份筛选）
     */
    @GetMapping("/list")
    public AjaxResult getProctorList(
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
                IPage<Proctor> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<Proctor> page = proctorService.getProctorPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<Proctor> page = proctorService.getProctorPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增监考记录
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addProctor(@RequestBody Proctor proctor) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        proctor.setUserId(Long.valueOf(userId));
        proctor.setUserName(userName);

        // 计算工作量（根据监考类型和次数）
        double workload = proctorService.countWorkload(proctor.getUserId(), proctor);
        proctor.setWorkload(BigDecimal.valueOf(workload));

        proctor.setCreateTime(LocalDateTime.now());
        proctor.setUpdateTime(LocalDateTime.now());

        boolean success = proctorService.addProctor(proctor);
        if (success) {
            workloadRefreshService.refreshTeaching(proctor.getUserId(), proctor.getYear());
        }

        return success ? AjaxResult.success("新增监考记录成功", proctor) : AjaxResult.error("新增监考记录失败");
    }

    /**
     * 3. 删除监考记录（支持批量删除）
     */
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteProctor(@RequestBody Long[] ids, @RequestParam(required = false) Integer year) {
        List<Proctor> originals = proctorService.listByIds(Arrays.asList(ids));
        boolean success = proctorService.removeProctorByIds(Arrays.asList(ids));
        if (success) {
            workloadRefreshService.refreshTeaching(originals.stream()
                    .map(item -> WorkloadScope.of(item.getUserId(), item.getYear()))
                    .collect(Collectors.toList()));
        }

        return success ? AjaxResult.success("删除监考记录成功") : AjaxResult.error("删除监考记录失败");
    }

    /**
     * 4. 更新监考记录
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateProctor(@RequestBody Proctor proctor,
            @RequestParam(defaultValue = "false") boolean auditEdit) {
        Proctor originalProctor = proctorService.getById(proctor.getId());
        String userId = SecurityUtils.getUsername();
        proctor.setUserId(Long.valueOf(userId));

        // 重新计算工作量
        double workload = proctorService.countWorkload(proctor.getUserId(), proctor);
        proctor.setWorkload(BigDecimal.valueOf(workload));

        proctor.setUpdateTime(LocalDateTime.now());
        // 将审核状态修改为待审核
        proctor.setStatus("待审核");

        com.ruoyi.manage.utils.AdminAuditUpdateUtils.preserve(originalProctor, proctor, auditEdit);
        boolean success = proctorService.updateById(proctor);
        if (success) {
            workloadRefreshService.refreshTeaching(Arrays.asList(
                    WorkloadScope.of(originalProctor.getUserId(), originalProctor.getYear()),
                    WorkloadScope.of(proctor.getUserId(), proctor.getYear())
            ));
        }

        return success ? AjaxResult.success("更新监考记录成功", proctor) : AjaxResult.error("更新监考记录失败");
    }

    /**
     * 5. 审核监考
     * @param proctor 监考审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditProctor(@RequestBody Proctor proctor) {
        try {
            Proctor originalProctor = proctorService.getById(proctor.getId());
            // 调用Service层审核方法
            boolean success = proctorService.auditProctor(proctor.getId(), proctor.getStatus(), proctor.getRemark());

            if (success) {
                String message = "已通过".equals(proctor.getStatus()) ? "审核通过成功" : "退回修改成功";
                workloadRefreshService.refreshTeaching(originalProctor.getUserId(), originalProctor.getYear());
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
