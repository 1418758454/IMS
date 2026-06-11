package com.ruoyi.manage.controller.research;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchPaper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 论文模块控制器
 * 处理前端论文相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/research/paper") // 论文模块接口根路径
public class PaperController {

    @Autowired
    private PaperService paperService; // 注入论文Service

    @Autowired
    private BasicInformationService basicInformationService;

    /**
     * 1. 获取论文列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     年份筛选（可选，如2024/2025/2026）
     * @return 分页结果（包含论文列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getPaperList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Boolean isAudit // 新增：当前是否为审核页面
    ) {
        if("all".equals(userId)){
            List<BasicInformation> basicInformationList = basicInformationService.listByDept(deptId);
            List<String> userIds = basicInformationList.stream().map(BasicInformation::getUserId).collect(Collectors.toList());
            if(userIds.isEmpty()){
                IPage<ResearchPaper> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<ResearchPaper> page = paperService.getPaperPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<ResearchPaper> page = paperService.getPaperPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增论文
     * @param paper 论文实体（前端传递的表单数据）
     * @return 新增结果（成功/失败）
     */
    @PostMapping("/add")
    public AjaxResult addPaper(@RequestBody ResearchPaper paper) {
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        paper.setUserId(Long.valueOf(userId)); // 设置创建人ID
        paper.setUserName(userName);
        paper.setWorkload(paper.getCoefficient().multiply(paper.getRank())); // 论文工作量计算

        // 审计字段默认值（若前端未传递）
        paper.setCreateTime(paper.getCreateTime() == null ? LocalDateTime.now() : paper.getCreateTime());
        paper.setUpdateTime(paper.getUpdateTime() == null ? LocalDateTime.now() : paper.getUpdateTime());

        // 将审核状态修改为待审核
        paper.setStatus("待审核");

        // 查看论文发表年份与当前页面年份是否一致
        boolean x = !paper.getYear().equals(paper.getPublishTime().toString().substring(0,4));
        boolean success = paperService.addPaper(paper); // 调用Service新增方法

        if(success && x){
            return AjaxResult.success("新增论文成功,请选择"+paper.getYear()+"年页面查看", paper);
        }else {
            return success ? AjaxResult.success("新增论文成功", paper) : AjaxResult.error("新增失败");
        }
        
        }

    /**
     * 3. 删除论文（支持批量删除）
     * @param ids 论文ID数组（前端传递的选中行ID）
     * @return 删除结果（成功/失败）
     */
    @DeleteMapping("/delete")
    public AjaxResult deletePaper(@RequestBody Long[] ids) {
        boolean success = paperService.removePaperByIds(Arrays.asList(ids)); // 调用Service批量删除
        return success ? AjaxResult.success("删除论文成功") : AjaxResult.error("删除论文失败");
    }

    /**
     * 4. 更新论文
     * @param paper 论文实体（包含ID和更新的表单数据）
     * @return 更新结果（成功/失败）
     */
    @PutMapping("/update")
    public AjaxResult updatePaper(@RequestBody ResearchPaper paper) {
        String userId = SecurityUtils.getUsername();
        paper.setUserId(Long.valueOf(userId));
        paper.setUpdateTime(LocalDateTime.now());

        // 1. 重新计算工作量
        BigDecimal coefficient = paper.getCoefficient();
        BigDecimal rank = paper.getRank();
        BigDecimal workload = coefficient.multiply(rank);
        paper.setWorkload(workload);

        // 2. 保存原始年份（关键：用于后续旧年份总工作量更新）
        Integer originalYear = Integer.valueOf(paper.getYear()); // 旧年份
        Integer newYear = Integer.valueOf(paper.getPublishTime().toString().substring(0, 4)); // 从发表时间提取新年份
        boolean yearChanged = !originalYear.equals(newYear); // 判断年份是否变更

        // 3. 更新年份字段为新年份
        paper.setYear(String.valueOf(newYear));

        // 将审核状态修改为待审核
        paper.setStatus("待审核");

        // 4. 执行数据库更新
//        boolean success = paperService.updateById(paper);
        boolean success = false;
        if(paperService.updateById(paper)){
            // 计算模块总工作量
            paperService.countTotalConfirmedWorkload(paper.getUserId(), paper.getYear());
            paperService.countTotalEstimatedWorkload(paper.getUserId(), paper.getYear());
            success = true;
        }

        // 5. 返回提示信息
        if (success && yearChanged) {
            return AjaxResult.success("更新论文成功,请选择" + newYear + "年页面查看", paper);
        } else {
            return success ? AjaxResult.success("更新论文成功", paper) : AjaxResult.error("更新失败");
        }
    }

    /**
     * 5. 审核论文
     * @param paper 论文审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditPaper(@RequestBody ResearchPaper paper) {
        try {
            // 调用Service层审核方法
            boolean success = paperService.auditPaper(paper.getId(), paper.getStatus(), paper.getRemark());

            if (success) {
                String message = "已通过".equals(paper.getStatus()) ? "审核通过成功" : "退回修改成功";
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}