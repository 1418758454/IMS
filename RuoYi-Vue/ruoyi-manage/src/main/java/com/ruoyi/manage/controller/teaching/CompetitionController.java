package com.ruoyi.manage.controller.teaching;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchAward;
import com.ruoyi.manage.domain.teaching.Competition;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 本科教学（学科竞赛）控制器
 * 处理前端学科竞赛相关的HTTP请求
 */
@RestController
@RequestMapping("/manage/teaching/competition") // 学科竞赛模块接口根路径
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService; // 注入学科竞赛Service
    @Autowired
    private BasicInformationService basicInformationService; // 注入学生基本信息Service


    /**
     * 1. 获取学科竞赛列表（分页+条件查询）
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param year     选中年份筛选（可选，如2023/2024）
     * @return 分页结果（包含学科竞赛列表和总条数）
     */
    @GetMapping("/list")
    public AjaxResult getCompetitionList(
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
                IPage<Competition> page = new Page<>(pageNum, pageSize);
                return AjaxResult.success(page);
            }
            IPage<Competition> page = competitionService.getCompetitionPageByUserIds(pageNum, pageSize, userIds, year, isAudit);
            return AjaxResult.success(page);
        }

        if(userId == null || userId.isEmpty()){
            userId = SecurityUtils.getUsername();
        }

        IPage<Competition> page = competitionService.getCompetitionPage(pageNum, pageSize, userId, year, isAudit);
        return AjaxResult.success(page);
    }


    /**
     * 2. 新增学科竞赛
     * @param competition 学科竞赛实体（前端表单数据）
     * @return 新增结果
     */
    @PostMapping("/add")
    public AjaxResult addCompetition(@RequestBody Competition competition) {
        if (StringUtils.isEmpty(competition.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        String userName = basicInformationService.getByloginId(userId).getName();
        competition.setUserId(Long.valueOf(userId));
        competition.setUserName(userName);

        // 计算工作量（基于赛事等级和获奖级别）
        double workload = competitionService.countWorkload(competition.getUserId(), competition);
        competition.setWorkload(BigDecimal.valueOf(workload));

        competition.setCreateTime(LocalDateTime.now());
        competition.setUpdateTime(LocalDateTime.now());

        boolean success = competitionService.addCompetition(competition);
        if (success) {
            competitionService.countTotalWorkload(competition.getUserId(), competition.getYear());
        }
        return success ? AjaxResult.success("新增学科竞赛成功", competition) : AjaxResult.error("新增失败");
    }

    /**
     * 3. 批量删除学科竞赛
     * @param ids 竞赛ID数组
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteCompetition(@RequestBody Long[] ids, @RequestParam Integer year) {
        String userId = SecurityUtils.getUsername();
        boolean success = competitionService.removeCompetitionByIds(Arrays.asList(ids));
        if (success) {
            competitionService.countTotalWorkload(Long.valueOf(userId), year);
        }
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 4. 更新学科竞赛
     * @param competition 竞赛实体
     * @return 更新结果
     */
    @PutMapping("/update")
    public AjaxResult updateCompetition(@RequestBody Competition competition) {
        if (StringUtils.isEmpty(competition.getPdfUrl())) {
            return AjaxResult.error("\u8bf7\u4e0a\u4f20PDF\u8bc1\u660e\u6750\u6599");
        }
        String userId = SecurityUtils.getUsername();
        competition.setUserId(Long.valueOf(userId));

        double workload = competitionService.countWorkload(competition.getUserId(), competition);
        competition.setWorkload(BigDecimal.valueOf(workload));
        competition.setUpdateTime(LocalDateTime.now());
        // 将审核状态修改为待审核
        competition.setStatus("待审核");
        boolean success = competitionService.updateById(competition);
        if (success) {
            competitionService.countTotalWorkload(competition.getUserId(), competition.getYear());
        }
        return success ? AjaxResult.success("更新成功", competition) : AjaxResult.error("更新失败");
    }

    /**
     * 5. 审核学科竞赛
     * @param competition 学科竞赛审核信息（包含ID、状态和备注）
     * @return 审核结果（成功/失败）
     */
    @PutMapping("/audit")
    public AjaxResult auditCompetition(@RequestBody Competition competition) {
        try {
            // 调用Service层审核方法
            boolean success = competitionService.auditCompetition(competition.getId(), competition.getStatus(), competition.getRemark());

            if (success) {
                String message = "已通过".equals(competition.getStatus()) ? "审核通过成功" : "退回修改成功";
                long id  = competition.getId();
                // 根据id查找数据，得到学科竞赛实体类
                competition = competitionService.getById(id);
                competitionService.countTotalWorkload(competition.getUserId(), competition.getYear());
                return AjaxResult.success(message);
            } else {
                return AjaxResult.error("审核操作失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审核过程中发生错误: " + e.getMessage());
        }
    }

}
