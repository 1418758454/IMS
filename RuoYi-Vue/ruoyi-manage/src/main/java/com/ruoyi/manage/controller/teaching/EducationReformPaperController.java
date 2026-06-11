package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.EducationReformPaper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.EducationReformPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教改论文控制器
 * 处理前端教改论文相关HTTP请求，重点提取发表日期年份（与科技创新结题年份逻辑一致）
 */
@RestController
@RequestMapping("/manage/teaching/educationReformPaper")
public class EducationReformPaperController {

    @Autowired
    private EducationReformPaperService educationReformPaperService;
    @Autowired
    private BasicInformationService basicInformationService;

    /**
     * 1. 获取教改论文列表（分页+年份筛选）
     * 与科技创新列表查询逻辑一致，按user_id和year筛选
     */
    @GetMapping("/list")
    public AjaxResult getEducationReformPaperList(
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
                IPage<EducationReformPaper> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<EducationReformPaper> page = educationReformPaperService.getEducationReformPaperPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<EducationReformPaper> page = educationReformPaperService.getEducationReformPaperPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增教改论文（提取发表日期年份，与科技创新endYear处理逻辑一致）
     */
    @PostMapping("/add")
    public AjaxResult addEducationReformPaper(@RequestBody EducationReformPaper paper) {
        if (StringUtils.isEmpty(paper.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        paper.setUserId(Long.valueOf(userId));
        paper.setUserName(userName);

        // 自动计算工作量（复用实验课的计算逻辑结构）
        double workload = educationReformPaperService.countWorkload(paper.getUserId(), paper);
        paper.setWorkload(BigDecimal.valueOf(workload));

        // 审计字段默认值
        paper.setCreateTime(LocalDateTime.now());
        paper.setUpdateTime(LocalDateTime.now());

        // 年份不一致时提示切换页面（复用科技创新的提示逻辑）
        int publishYear = paper.getPublishDate().getYear();
        boolean yearMismatch = !paper.getYear().equals(publishYear);

        boolean success = educationReformPaperService.addEducationReformPaper(paper);
        if (success) {
            // 新增后更新总工作量（与科技创新总表逻辑一致）
            educationReformPaperService.countTotalWorkload(paper.getUserId(), paper.getYear());
        }


        if (success && yearMismatch) {
            return AjaxResult.success("新增成功，请切换至" + publishYear + "年页面查看", paper);
        }
        return success ? AjaxResult.success("新增成功", paper) : AjaxResult.error("新增失败");
    }

    /**
     * 3. 更新教改论文（更新年份提取逻辑，与科技创新一致）
     */
    @PutMapping("/update")
    public AjaxResult updateEducationReformPaper(@RequestBody EducationReformPaper paper) {
        if (StringUtils.isEmpty(paper.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        paper.setUserId(Long.valueOf(userId));
        paper.setUpdateTime(LocalDateTime.now());

        // 自动计算工作量（复用实验课的计算逻辑结构）
        double workload = educationReformPaperService.countWorkload(paper.getUserId(), paper);
        paper.setWorkload(BigDecimal.valueOf(workload));

        // 核心：更新时重新提取发表日期年份（类比科技创新更新endYear逻辑）
        int publishYear = paper.getPublishDate().getYear();
        Integer originalYear = paper.getYear(); // 原始年份（用于判断是否需要跨年度更新总表）
        paper.setYear(publishYear);
        // 将审核状态修改为待审核
        paper.setStatus("待审核");

        boolean success = educationReformPaperService.updateById(paper);
        if (success) {
            // 年份变更时需更新新旧两个年度的总工作量（与科技创新总表逻辑一致）
            if (!originalYear.equals(publishYear)) {
                educationReformPaperService.countTotalWorkload(paper.getUserId(), originalYear); // 更新旧年份
            }
            educationReformPaperService.countTotalWorkload(paper.getUserId(), publishYear); // 更新新年份
        }

        boolean yearMismatch = !originalYear.equals(publishYear);
        if (success && yearMismatch) {
            return AjaxResult.success("更新成功，请切换至" + publishYear + "年页面查看", paper);
        }
        return success ? AjaxResult.success("更新成功", paper) : AjaxResult.error("更新失败");
    }

    /**
     * 4. 删除教改论文（支持批量删除，同步更新总工作量）
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteEducationReformPaper(@RequestBody Long[] ids, @RequestParam Integer year) {
        String userId = SecurityUtils.getUsername();
        boolean success = educationReformPaperService.removeEducationReformPaperByIds(Arrays.asList(ids));
        if (success) {
            educationReformPaperService.countTotalWorkload(Long.valueOf(userId), year);
        }
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 5. 审核教改论文
     * @param paper 教改论文审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditEducationReformPaper(@RequestBody EducationReformPaper paper) {
        try {
            // 调用Service层审核方法
            boolean success = educationReformPaperService.auditEducationReformPaper(paper.getId(), paper.getStatus(), paper.getRemark());

            if (success) {
                String message = "已通过".equals(paper.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = paper.getId();
                paper = educationReformPaperService.getById(id);
                educationReformPaperService.countTotalWorkload(paper.getUserId(), paper.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}
