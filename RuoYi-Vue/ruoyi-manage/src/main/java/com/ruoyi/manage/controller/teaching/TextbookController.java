package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.WorkloadScope;
import com.ruoyi.manage.domain.teaching.Textbook;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.WorkloadRefreshService;
import com.ruoyi.manage.service.teaching.TextbookService;
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
 * 出版教材控制器
 * 处理前端出版教材相关的HTTP请求（提取出版时间年份，与科技创新结题年份逻辑一致）
 */
@RestController
@RequestMapping("/manage/teaching/textbook") // 接口根路径
public class TextbookController {

    @Autowired
    private WorkloadRefreshService workloadRefreshService;

    @Autowired
    private TextbookService textbookService;
    @Autowired
    private BasicInformationService basicInformationService;

    /**
     * 1. 获取出版教材列表（分页+年份筛选）
     */
    @GetMapping("/list")
    public AjaxResult getTextbookList(
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
                IPage<Textbook> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<Textbook> page = textbookService.getTextbookPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<Textbook> page = textbookService.getTextbookPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增出版教材（提取出版时间年份作为year字段）
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addTextbook(@RequestBody Textbook textbook) {
        if (StringUtils.isEmpty(textbook.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        textbook.setUserId(Long.valueOf(userId));
        textbook.setUserName(userName);


        // 自动计算工作量（复用科技创新计算逻辑结构）
        double workload = textbookService.countWorkload(textbook.getUserId(), textbook);
        textbook.setWorkload(BigDecimal.valueOf(workload));

        // 审计字段默认值
        textbook.setCreateTime(textbook.getCreateTime() == null ? LocalDateTime.now() : textbook.getCreateTime());
        textbook.setUpdateTime(textbook.getUpdateTime() == null ? LocalDateTime.now() : textbook.getUpdateTime());

        // 判断页面年份与出版年份是否一致（用于提示信息）
        Integer publishYear = textbook.getPublishDate().getYear();
        boolean yearMismatch = !textbook.getYear().equals(publishYear);
        boolean success = textbookService.addTextbook(textbook);

        if (success) {
            workloadRefreshService.refreshTeaching(textbook.getUserId(), publishYear);
        }

        if (success && yearMismatch) {
            return AjaxResult.success("新增成功，请选择" + publishYear + "年页面查看", textbook);
        } else {
            return success ? AjaxResult.success("新增出版教材成功", textbook) : AjaxResult.error("新增失败");
        }
    }

    /**
     * 3. 删除出版教材（支持批量删除）
     */
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteTextbook(@RequestBody Long[] ids, @RequestParam(required = false) Integer year) {
        List<Textbook> originals = textbookService.listByIds(Arrays.asList(ids));
        boolean success = textbookService.removeTextbookByIds(Arrays.asList(ids));
        if (success) {
            workloadRefreshService.refreshTeaching(originals.stream()
                    .map(item -> WorkloadScope.of(item.getUserId(), item.getYear()))
                    .collect(Collectors.toList()));
        }
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 4. 更新出版教材（重新提取出版时间年份）
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateTextbook(@RequestBody Textbook textbook,
            @RequestParam(defaultValue = "false") boolean auditEdit) {
        Textbook originalTextbook = textbookService.getById(textbook.getId());
        if (StringUtils.isEmpty(textbook.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        textbook.setUserId(Long.valueOf(userId));
        textbook.setUpdateTime(LocalDateTime.now());

        // 重新计算工作量
        double workload = textbookService.countWorkload(textbook.getUserId(), textbook);
        textbook.setWorkload(BigDecimal.valueOf(workload));

        // 提取出版时间年份（核心逻辑）
        Integer originalYear = originalTextbook.getYear(); // 旧年份
        Integer newYear = textbook.getPublishDate().getYear(); // 从出版时间提取新年份
        boolean yearChanged = !originalYear.equals(newYear);

        // 更新年份字段
        textbook.setYear(newYear);
        // 将审核状态修改为待审核
        textbook.setStatus("待审核");

        com.ruoyi.manage.utils.AdminAuditUpdateUtils.preserve(originalTextbook, textbook, auditEdit);
        boolean success = textbookService.updateById(textbook);
        if (success) {
            // 年份变更时更新新旧年份总工作量
            if (yearChanged) {
                workloadRefreshService.refreshTeaching(originalTextbook.getUserId(), originalYear);
                workloadRefreshService.refreshTeaching(textbook.getUserId(), newYear);
            } else {
                workloadRefreshService.refreshTeaching(textbook.getUserId(), newYear);
            }
        }

        if (success && yearChanged) {
            return AjaxResult.success("更新成功，请切换至" + newYear + "年页面查看", textbook);
        } else {
            return success ? AjaxResult.success("更新成功", textbook) : AjaxResult.error("更新失败");
        }
    }

    /**
     * 5. 审核出版教材
     * @param textbook 出版教材审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult auditTextbook(@RequestBody Textbook textbook) {
        try {
            Textbook originalTextbook = textbookService.getById(textbook.getId());
            // 调用Service层审核方法
            boolean success = textbookService.auditTextbook(textbook.getId(), textbook.getStatus(), textbook.getRemark());

            if (success) {
                String message = "已通过".equals(textbook.getStatus()) ? "审核通过成功" : "退回修改成功";
                workloadRefreshService.refreshTeaching(originalTextbook.getUserId(), originalTextbook.getYear());
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
