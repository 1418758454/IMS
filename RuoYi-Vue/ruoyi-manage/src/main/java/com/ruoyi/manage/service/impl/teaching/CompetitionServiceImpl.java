package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.Competition;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.teaching.CompetitionMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    @Autowired
    private CompetitionMapper competitionMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper totalWorkloadMapper;
    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public IPage<Competition> getCompetitionPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<Competition> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Competition> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return competitionMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Competition> getCompetitionPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            competitionMapper.chageUserName(userId, userName);
        });

        IPage<Competition> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Competition> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return competitionMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditCompetition(Long id, String status, String remark) {
        Competition competition = this.getById(id);
        if (competition == null) {
            return false;
        }

        // 更新审核状态和备注
        competition.setStatus(status);
        competition.setRemark(remark);
        competition.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(competition)){
//            // 更新总工作量
//            competitionService.countTotalWorkload(competition.getUserId(), competition.getYear());
//            return true;
//        }
        return this.updateById(competition);
    }


    @Override
    public boolean addCompetition(Competition competition) {
        // 数据校验：项目名称、赛事等级、获奖级别非空
//        if (competition.getAwardProjectName() == null || competition.getCompetitionLevel() == null
//                || competition.getAwardLevel() == null) {
//            throw new RuntimeException("必填字段缺失");
//        }
        return competitionMapper.insert(competition) > 0;
    }

    @Override
    public boolean removeCompetitionByIds(List<Long> ids) {
        List<Competition> competitions = competitionMapper.selectBatchIds(ids);
        for (Competition competition : competitions) {
            if (StringUtils.isNotEmpty(competition.getPdfUrl())) {
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(competition.getPdfUrl());
                FileUtils.deleteFile(filePath);
            }
        }
        return competitionMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 工作量 = 竞赛项目系数
     * 竞赛项目系数：A级一等=60.0，B级一等=30.0，C级一等=5.0；A级二等=40.0，B级二等=15.0，C级二等=3.0；A级三等=20.0，B级三等=7、0，C级三等=2.0
     */
    @Override
    public double countWorkload(Long userId, Competition competition) {
        // 竞赛项目系数（根据级别和获奖等级确定）
        double baseCoeff = 0.0;
        if ("A级".equals(competition.getCompetitionLevel())) {
            if ("一等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 60.0;
            } else if ("二等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 40.0;
            } else if ("三等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 20.0;
            }
//            else {
//                baseCoeff = 60.0;
//            }
        } else if ("B级".equals(competition.getCompetitionLevel())) {
            if ("一等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 30.0;
            } else if ("二等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 15.0;
            } else if ("三等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 7.0;
            }
        } else if ("C级".equals(competition.getCompetitionLevel())) {
            if ("一等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 5.0;
            } else if ("二等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 3.0;
            } else if ("三等奖".equals(competition.getAwardLevel())) {
                baseCoeff = 2.0;
            }
        }

        return Math.round(baseCoeff * 1000) / 1000.0; // 保留三位小数
    }

    @Override
    public double countTotalWorkload(Long userId, Integer year) {
        // 查询用户当年所有竞赛记录
        List<Competition> allCompetitions = competitionMapper.selectList(
                new QueryWrapper<Competition>().eq("user_id", userId).eq("year", year)
        );

        // 查询用户当年已通过审核的竞赛记录
        List<Competition> confirmedCompetitions = competitionMapper.selectList(
                new QueryWrapper<Competition>().eq("user_id", userId).eq("year", year).eq("status", "已通过")
        );

        // 计算预计总工作量（累加全部数据）
        double estimatedTotal = allCompetitions.stream()
                .mapToDouble(c -> countWorkload(userId, c))
                .sum();

        // 计算已确认总工作量（只累加审核状态为"已通过"的数据）
        double confirmedTotal = confirmedCompetitions.stream()
                .mapToDouble(c -> countWorkload(userId, c))
                .sum();

        // 学科竞赛模块工作量200封顶
        estimatedTotal = Math.min(estimatedTotal, 200.0);
        confirmedTotal = Math.min(confirmedTotal, 200.0);

        // 更新总工作量表
        BasicInformation user = basicInformationService.getById(userId);
        TeachingTotalWorkload totalWorkload = totalWorkloadMapper.selectOne(
                new QueryWrapper<TeachingTotalWorkload>().eq("user_id", userId).eq("year", year)
        );

        if (totalWorkload == null) {
            totalWorkload = new TeachingTotalWorkload();
            totalWorkload.setUserId(BigDecimal.valueOf(userId));
            totalWorkload.setUserName(user.getName());
            totalWorkload.setYear(year);
            totalWorkload.setCompetitionEstimatedWorkload(BigDecimal.valueOf(estimatedTotal));
            totalWorkload.setCompetitionConfirmedWorkload(BigDecimal.valueOf(confirmedTotal));
            totalWorkloadMapper.insert(totalWorkload);
        } else {
            totalWorkload.setCompetitionEstimatedWorkload(BigDecimal.valueOf(estimatedTotal));
            totalWorkload.setCompetitionConfirmedWorkload(BigDecimal.valueOf(confirmedTotal));
            totalWorkloadMapper.update(totalWorkload, new QueryWrapper<TeachingTotalWorkload>()
                    .eq("user_id", userId).eq("year", year));
        }
        return 0;
    }


}
