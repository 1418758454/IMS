package com.ruoyi.manage.service.impl.research;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.mapper.BasicInformationMapper;
import com.ruoyi.manage.mapper.research.ResearchTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.ResearchTotalWorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResearchTotalWorkloadServiceImpl extends ServiceImpl<ResearchTotalWorkloadMapper, ResearchTotalWorkload> implements ResearchTotalWorkloadService {

    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private ResearchTotalWorkloadMapper researchTotalWorkloadMapper;
    @Autowired
    private BasicInformationMapper basicInformationMapper;

    @Override
    public boolean save(ResearchTotalWorkload researchTotalWorkload) {
        // 更新总工作量表
        // 使用工具获取用户id
        String userId = SecurityUtils.getUsername();
        String year = researchTotalWorkload.getYear();
        BasicInformation info = basicInformationService.getById(userId);
        String username = info.getName();
        researchTotalWorkload.setUserId(BigDecimal.valueOf(Long.parseLong(userId)));
        researchTotalWorkload.setUserName(username);


        QueryWrapper<ResearchTotalWorkload> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("year", year);
        // 根据年份和用户id查询科研总工作量表是否已有记录
        ResearchTotalWorkload totalWorkloadEntity = researchTotalWorkloadMapper.selectOne(queryWrapper);

        if (totalWorkloadEntity == null) { // 没有记录则新增
//            totalWorkloadEntity = new ResearchTotalWorkload();
//            totalWorkloadEntity.setUserId(userId);
//            totalWorkloadEntity.setUserName(info.getName());
//            totalWorkloadEntity.setYear(year);
//            totalWorkloadEntity.setProctorWorkload(BigDecimal.valueOf(totalWorkload)); // 监考模块字段

            researchTotalWorkloadMapper.insert(researchTotalWorkload);
            return true;
        } else { // 已有记录则更新
//            totalWorkloadEntity.setProctorWorkload(BigDecimal.valueOf(totalWorkload));
            researchTotalWorkloadMapper.update(researchTotalWorkload, queryWrapper);
            return true;
        }
    }

    // ResearchTotalWorkloadServiceImpl.java
    @Override
    public ResearchTotalWorkload getTotalWorkload(Integer year, Long userId) {
        // 根据年份和用户ID查询科研汇总表的数据
        ResearchTotalWorkload totalWorkload = researchTotalWorkloadMapper.selectOne(
                new QueryWrapper<ResearchTotalWorkload>().eq("year", year).eq("user_id", userId)
        );

        if (totalWorkload == null) {
            // 将各列数据设置为0
            totalWorkload = new ResearchTotalWorkload();
            totalWorkload.setUserId(BigDecimal.valueOf(userId));
            totalWorkload.setYear(String.valueOf(year));
            totalWorkload.setSubjectEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setSubjectConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setPaperEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setPaperConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setMonographEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setMonographConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setAwardEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setAwardConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setPatentEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setPatentConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setSoftwareEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setSoftwareConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setTotalWorkload(BigDecimal.ZERO);
            return totalWorkload;
        } else {
            // 累加各项工作得到科研总工作量（使用已确认工作量）
            BigDecimal total = BigDecimal.ZERO
                    .add(totalWorkload.getSubjectConfirmedWorkload() != null ? totalWorkload.getSubjectConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getPaperConfirmedWorkload() != null ? totalWorkload.getPaperConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getMonographConfirmedWorkload() != null ? totalWorkload.getMonographConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getAwardConfirmedWorkload() != null ? totalWorkload.getAwardConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getPatentConfirmedWorkload() != null ? totalWorkload.getPatentConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getSoftwareConfirmedWorkload() != null ? totalWorkload.getSoftwareConfirmedWorkload() : BigDecimal.ZERO);
            totalWorkload.setTotalWorkload(total);
            // 将总工作量写入数据库
            researchTotalWorkloadMapper.updateById(totalWorkload);
            return totalWorkload;
        }
    }

    @Override
    public ResearchTotalWorkload getTotalWorkload(Integer year, Integer deptId, String userId) {
        // 当userId为"all"时的处理逻辑
        if ("all".equals(userId)) {
            // 创建一个新的汇总对象用于存储累加结果
            ResearchTotalWorkload totalSummary = new ResearchTotalWorkload();
            totalSummary.setYear(String.valueOf(year));
            totalSummary.setTotalWorkload(BigDecimal.ZERO);

            // 初始化所有字段为ZERO
            totalSummary.setSubjectEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setSubjectConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setPaperEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setPaperConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setMonographEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setMonographConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setAwardEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setAwardConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setPatentEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setPatentConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setSoftwareEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setSoftwareConfirmedWorkload(BigDecimal.ZERO);

            QueryWrapper<ResearchTotalWorkload> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("year", year);

            // 如果deptId不为0，则按部门筛选
            if (deptId != 0) {
                // 查询BasicInformation表中匹配部门的用户名
                String deptName = "";
                switch (deptId) {
                    case 1:
                        deptName = "应用数学";
                        break;
                    case 2:
                        deptName = "信息与计算科学";
                        break;
                    case 3:
                        deptName = "统计";
                        break;
                    case 4:
                        deptName = "大学教学部";
                        break;
                    default:
                        break;
                }
                List<BasicInformation> basicInformationList = basicInformationMapper.selectList(
                        new QueryWrapper<BasicInformation>().eq("department", deptName)
                );
                List<String> userIds = basicInformationList.stream()
                        .map(BasicInformation::getUserId)
                        .collect(Collectors.toList());
                queryWrapper.in("user_id", userIds);
            }

            // 查询符合条件的所有记录
            List<ResearchTotalWorkload> workloads = researchTotalWorkloadMapper.selectList(queryWrapper);

            // 累加所有记录的字段值
            for (ResearchTotalWorkload workload : workloads) {
                totalSummary.setSubjectEstimatedWorkload(
                        totalSummary.getSubjectEstimatedWorkload().add(
                                workload.getSubjectEstimatedWorkload() != null ? workload.getSubjectEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setSubjectConfirmedWorkload(
                        totalSummary.getSubjectConfirmedWorkload().add(
                                workload.getSubjectConfirmedWorkload() != null ? workload.getSubjectConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setPaperEstimatedWorkload(
                        totalSummary.getPaperEstimatedWorkload().add(
                                workload.getPaperEstimatedWorkload() != null ? workload.getPaperEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setPaperConfirmedWorkload(
                        totalSummary.getPaperConfirmedWorkload().add(
                                workload.getPaperConfirmedWorkload() != null ? workload.getPaperConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setMonographEstimatedWorkload(
                        totalSummary.getMonographEstimatedWorkload().add(
                                workload.getMonographEstimatedWorkload() != null ? workload.getMonographEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setMonographConfirmedWorkload(
                        totalSummary.getMonographConfirmedWorkload().add(
                                workload.getMonographConfirmedWorkload() != null ? workload.getMonographConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setAwardEstimatedWorkload(
                        totalSummary.getAwardEstimatedWorkload().add(
                                workload.getAwardEstimatedWorkload() != null ? workload.getAwardEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setAwardConfirmedWorkload(
                        totalSummary.getAwardConfirmedWorkload().add(
                                workload.getAwardConfirmedWorkload() != null ? workload.getAwardConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setPatentEstimatedWorkload(
                        totalSummary.getPatentEstimatedWorkload().add(
                                workload.getPatentEstimatedWorkload() != null ? workload.getPatentEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setPatentConfirmedWorkload(
                        totalSummary.getPatentConfirmedWorkload().add(
                                workload.getPatentConfirmedWorkload() != null ? workload.getPatentConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setSoftwareEstimatedWorkload(
                        totalSummary.getSoftwareEstimatedWorkload().add(
                                workload.getSoftwareEstimatedWorkload() != null ? workload.getSoftwareEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setSoftwareConfirmedWorkload(
                        totalSummary.getSoftwareConfirmedWorkload().add(
                                workload.getSoftwareConfirmedWorkload() != null ? workload.getSoftwareConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
            }

            // 计算总工作量（使用已确认工作量）
            BigDecimal total = BigDecimal.ZERO
                    .add(totalSummary.getSubjectConfirmedWorkload())
                    .add(totalSummary.getPaperConfirmedWorkload())
                    .add(totalSummary.getMonographConfirmedWorkload())
                    .add(totalSummary.getAwardConfirmedWorkload())
                    .add(totalSummary.getPatentConfirmedWorkload())
                    .add(totalSummary.getSoftwareConfirmedWorkload());
            totalSummary.setTotalWorkload(total);

            return totalSummary;
        } else {
            // 原有的单用户查询逻辑
            ResearchTotalWorkload totalWorkload = researchTotalWorkloadMapper.selectOne(
                    new QueryWrapper<ResearchTotalWorkload>().eq("year", year).eq("user_id", userId)
            );

            if (totalWorkload == null) {
                // 将各列数据设置为0
                totalWorkload = new ResearchTotalWorkload();
                totalWorkload.setUserId(BigDecimal.valueOf(Long.parseLong(userId)));
                totalWorkload.setYear(String.valueOf(year));
                totalWorkload.setSubjectEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setSubjectConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setPaperEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setPaperConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setMonographEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setMonographConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setAwardEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setAwardConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setPatentEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setPatentConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setSoftwareEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setSoftwareConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setTotalWorkload(BigDecimal.ZERO);
                return totalWorkload;
            } else {
                // 累加各项工作得到科研总工作量（使用预计工作量）
                BigDecimal total = BigDecimal.ZERO
                        .add(totalWorkload.getSubjectConfirmedWorkload() != null ? totalWorkload.getSubjectConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getPaperConfirmedWorkload() != null ? totalWorkload.getPaperConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getMonographConfirmedWorkload() != null ? totalWorkload.getMonographConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getAwardConfirmedWorkload() != null ? totalWorkload.getAwardConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getPatentConfirmedWorkload() != null ? totalWorkload.getPatentConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getSoftwareConfirmedWorkload() != null ? totalWorkload.getSoftwareConfirmedWorkload() : BigDecimal.ZERO);
                totalWorkload.setTotalWorkload(total);
                // 将总工作量写入数据库
                researchTotalWorkloadMapper.updateById(totalWorkload);
                return totalWorkload;
            }
        }
    }


}
