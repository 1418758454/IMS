package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.BasicInformationMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.teaching.TeachingTotalWorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeachingTotalWorkloadServiceImpl extends ServiceImpl<TeachingTotalWorkloadMapper, TeachingTotalWorkload> implements TeachingTotalWorkloadService {

    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;
    @Autowired
    private BasicInformationMapper basicInformationMapper;

    @Override
    public TeachingTotalWorkload getTotalWorkload(Integer year, Long userId) {
        // 根据年份和用户ID查询教学汇总表的数据
        TeachingTotalWorkload totalWorkload = teachingTotalWorkloadMapper.selectOne(new QueryWrapper<TeachingTotalWorkload>().eq("year", year).eq("user_id", userId));
        if(totalWorkload == null){
            // 将各列数据设置为0
            totalWorkload = new TeachingTotalWorkload();
            totalWorkload.setUserId(BigDecimal.valueOf(userId));
//            totalWorkload.setUserName(totalWorkload.getName());
            totalWorkload.setYear(year);
            totalWorkload.setTheoryCourseEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setTheoryCourseConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setExperimentCourseEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setExperimentCourseConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setPracticalTeachingEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setPracticalTeachingConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setThesisCourseEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setThesisCourseConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setScienceInnovationEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setScienceInnovationConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setCompetitionEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setCompetitionConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setTextbookEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setTextbookConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setEducationReformEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setEducationReformConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setEducationReformPaperEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setEducationReformPaperConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setProctorEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setProctorConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setGraduateTheoryCourseEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setGraduateTheoryCourseConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setGraduateGuideStudentEstimatedWorkload(BigDecimal.ZERO);
            totalWorkload.setGraduateGuideStudentConfirmedWorkload(BigDecimal.ZERO);
            totalWorkload.setTotalWorkload(BigDecimal.ZERO);
            return totalWorkload;
        }else {
            // 累加各项工作得到教学总工作量
            BigDecimal total = BigDecimal.ZERO
                    .add(totalWorkload.getTheoryCourseConfirmedWorkload() != null ? totalWorkload.getTheoryCourseConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getExperimentCourseConfirmedWorkload() != null ? totalWorkload.getExperimentCourseConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getPracticalTeachingConfirmedWorkload() != null ? totalWorkload.getPracticalTeachingConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getThesisCourseConfirmedWorkload() != null ? totalWorkload.getThesisCourseConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getScienceInnovationConfirmedWorkload() != null ? totalWorkload.getScienceInnovationConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getCompetitionConfirmedWorkload() != null ? totalWorkload.getCompetitionConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getTextbookConfirmedWorkload() != null ? totalWorkload.getTextbookConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getEducationReformConfirmedWorkload() != null ? totalWorkload.getEducationReformConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getEducationReformPaperConfirmedWorkload() != null ? totalWorkload.getEducationReformPaperConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getProctorConfirmedWorkload() != null ? totalWorkload.getProctorConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getGraduateTheoryCourseConfirmedWorkload() != null ? totalWorkload.getGraduateTheoryCourseConfirmedWorkload() : BigDecimal.ZERO)
                    .add(totalWorkload.getGraduateGuideStudentConfirmedWorkload() != null ? totalWorkload.getGraduateGuideStudentConfirmedWorkload() : BigDecimal.ZERO);
            totalWorkload.setTotalWorkload(total);
            // 将总工作量写入数据库
            teachingTotalWorkloadMapper.updateById(totalWorkload);
            return totalWorkload;
        }

    }

    @Override
    public TeachingTotalWorkload getTotalWorkload(Integer year, Integer deptId, String userId) {
        // 当userId为"all"时的处理逻辑
        if ("all".equals(userId)) {
            // 创建一个新的汇总对象用于存储累加结果
            TeachingTotalWorkload totalSummary = new TeachingTotalWorkload();
            totalSummary.setYear(year);
            totalSummary.setTotalWorkload(BigDecimal.ZERO);

            // 初始化所有字段为ZERO
            totalSummary.setTheoryCourseEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setTheoryCourseConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setExperimentCourseEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setExperimentCourseConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setPracticalTeachingEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setPracticalTeachingConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setThesisCourseEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setThesisCourseConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setScienceInnovationEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setScienceInnovationConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setCompetitionEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setCompetitionConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setTextbookEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setTextbookConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setEducationReformEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setEducationReformConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setEducationReformPaperEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setEducationReformPaperConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setProctorEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setProctorConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setGraduateTheoryCourseEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setGraduateTheoryCourseConfirmedWorkload(BigDecimal.ZERO);
            totalSummary.setGraduateGuideStudentEstimatedWorkload(BigDecimal.ZERO);
            totalSummary.setGraduateGuideStudentConfirmedWorkload(BigDecimal.ZERO);

            QueryWrapper<TeachingTotalWorkload> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("year", year);

            // 如果deptId不为"all"，则按部门筛选
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
                    default:
                        break;
                    case 3:
                        deptName = "统计";
                        break;
                    case 4:
                        deptName = "大学教学部";
                        break;
                }
                List<BasicInformation> basicInformationList = basicInformationMapper.selectList(new QueryWrapper<BasicInformation>().eq("department", deptName));
//                List<String> userNames = basicInformationList.stream().map(BasicInformation::getName).collect(Collectors.toList());
                List<String> userIds = basicInformationList.stream().map(BasicInformation::getUserId).collect(Collectors.toList());
                queryWrapper.in("user_id", userIds);
            }

            // 查询符合条件的所有记录
            List<TeachingTotalWorkload> workloads = teachingTotalWorkloadMapper.selectList(queryWrapper);

            // 累加所有记录的字段值
            for (TeachingTotalWorkload workload : workloads) {
                totalSummary.setTheoryCourseEstimatedWorkload(
                        totalSummary.getTheoryCourseEstimatedWorkload().add(
                                workload.getTheoryCourseEstimatedWorkload() != null ? workload.getTheoryCourseEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setTheoryCourseConfirmedWorkload(
                        totalSummary.getTheoryCourseConfirmedWorkload().add(
                                workload.getTheoryCourseConfirmedWorkload() != null ? workload.getTheoryCourseConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                // 对其他字段进行类似累加操作...
                totalSummary.setExperimentCourseEstimatedWorkload(
                        totalSummary.getExperimentCourseEstimatedWorkload().add(
                                workload.getExperimentCourseEstimatedWorkload() != null ? workload.getExperimentCourseEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setExperimentCourseConfirmedWorkload(
                        totalSummary.getExperimentCourseConfirmedWorkload().add(
                                workload.getExperimentCourseConfirmedWorkload() != null ? workload.getExperimentCourseConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setPracticalTeachingEstimatedWorkload(
                        totalSummary.getPracticalTeachingEstimatedWorkload().add(
                                workload.getPracticalTeachingEstimatedWorkload() != null ? workload.getPracticalTeachingEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setPracticalTeachingConfirmedWorkload(
                        totalSummary.getPracticalTeachingConfirmedWorkload().add(
                                workload.getPracticalTeachingConfirmedWorkload() != null ? workload.getPracticalTeachingConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setThesisCourseEstimatedWorkload(
                        totalSummary.getThesisCourseEstimatedWorkload().add(
                                workload.getThesisCourseEstimatedWorkload() != null ? workload.getThesisCourseEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setThesisCourseConfirmedWorkload(
                        totalSummary.getThesisCourseConfirmedWorkload().add(
                                workload.getThesisCourseConfirmedWorkload() != null ? workload.getThesisCourseConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setScienceInnovationEstimatedWorkload(
                        totalSummary.getScienceInnovationEstimatedWorkload().add(
                                workload.getScienceInnovationEstimatedWorkload() != null ? workload.getScienceInnovationEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setScienceInnovationConfirmedWorkload(
                        totalSummary.getScienceInnovationConfirmedWorkload().add(
                                workload.getScienceInnovationConfirmedWorkload() != null ? workload.getScienceInnovationConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setCompetitionEstimatedWorkload(
                        totalSummary.getCompetitionEstimatedWorkload().add(
                                workload.getCompetitionEstimatedWorkload() != null ? workload.getCompetitionEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setCompetitionConfirmedWorkload(
                        totalSummary.getCompetitionConfirmedWorkload().add(
                                workload.getCompetitionConfirmedWorkload() != null ? workload.getCompetitionConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setTextbookEstimatedWorkload(
                        totalSummary.getTextbookEstimatedWorkload().add(
                                workload.getTextbookEstimatedWorkload() != null ? workload.getTextbookEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setTextbookConfirmedWorkload(
                        totalSummary.getTextbookConfirmedWorkload().add(
                                workload.getTextbookConfirmedWorkload() != null ? workload.getTextbookConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setEducationReformEstimatedWorkload(
                        totalSummary.getEducationReformEstimatedWorkload().add(
                                workload.getEducationReformEstimatedWorkload() != null ? workload.getEducationReformEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setEducationReformConfirmedWorkload(
                        totalSummary.getEducationReformConfirmedWorkload().add(
                                workload.getEducationReformConfirmedWorkload() != null ? workload.getEducationReformConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setEducationReformPaperEstimatedWorkload(
                        totalSummary.getEducationReformPaperEstimatedWorkload().add(
                                workload.getEducationReformPaperEstimatedWorkload() != null ? workload.getEducationReformPaperEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setEducationReformPaperConfirmedWorkload(
                        totalSummary.getEducationReformPaperConfirmedWorkload().add(
                                workload.getEducationReformPaperConfirmedWorkload() != null ? workload.getEducationReformPaperConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setProctorEstimatedWorkload(
                        totalSummary.getProctorEstimatedWorkload().add(
                                workload.getProctorEstimatedWorkload() != null ? workload.getProctorEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setProctorConfirmedWorkload(
                        totalSummary.getProctorConfirmedWorkload().add(
                                workload.getProctorConfirmedWorkload() != null ? workload.getProctorConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setGraduateTheoryCourseEstimatedWorkload(
                        totalSummary.getGraduateTheoryCourseEstimatedWorkload().add(
                                workload.getGraduateTheoryCourseEstimatedWorkload() != null ? workload.getGraduateTheoryCourseEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setGraduateTheoryCourseConfirmedWorkload(
                        totalSummary.getGraduateTheoryCourseConfirmedWorkload().add(
                                workload.getGraduateTheoryCourseConfirmedWorkload() != null ? workload.getGraduateTheoryCourseConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setGraduateGuideStudentEstimatedWorkload(
                        totalSummary.getGraduateGuideStudentEstimatedWorkload().add(
                                workload.getGraduateGuideStudentEstimatedWorkload() != null ? workload.getGraduateGuideStudentEstimatedWorkload() : BigDecimal.ZERO
                        )
                );
                totalSummary.setGraduateGuideStudentConfirmedWorkload(
                        totalSummary.getGraduateGuideStudentConfirmedWorkload().add(
                                workload.getGraduateGuideStudentConfirmedWorkload() != null ? workload.getGraduateGuideStudentConfirmedWorkload() : BigDecimal.ZERO
                        )
                );
            }

            // 计算总工作量（使用已确认工作量）
            BigDecimal total = BigDecimal.ZERO
                    .add(totalSummary.getTheoryCourseConfirmedWorkload())
                    .add(totalSummary.getExperimentCourseConfirmedWorkload())
                    .add(totalSummary.getPracticalTeachingConfirmedWorkload())
                    .add(totalSummary.getThesisCourseConfirmedWorkload())
                    .add(totalSummary.getScienceInnovationConfirmedWorkload())
                    .add(totalSummary.getCompetitionConfirmedWorkload())
                    .add(totalSummary.getTextbookConfirmedWorkload())
                    .add(totalSummary.getEducationReformConfirmedWorkload())
                    .add(totalSummary.getEducationReformPaperConfirmedWorkload())
                    .add(totalSummary.getProctorConfirmedWorkload())
                    .add(totalSummary.getGraduateTheoryCourseConfirmedWorkload())
                    .add(totalSummary.getGraduateGuideStudentConfirmedWorkload());
            totalSummary.setTotalWorkload(total);

            return totalSummary;
        } else {
            // 原有的单用户查询逻辑
            TeachingTotalWorkload totalWorkload = teachingTotalWorkloadMapper.selectOne(
                    new QueryWrapper<TeachingTotalWorkload>().eq("year", year).eq("user_id", userId)
            );

            if (totalWorkload == null) {
                // 将各列数据设置为0
                totalWorkload = new TeachingTotalWorkload();
                totalWorkload.setUserId(BigDecimal.valueOf(Long.parseLong(userId)));
                totalWorkload.setYear(year);
                totalWorkload.setTheoryCourseEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setTheoryCourseConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setExperimentCourseEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setExperimentCourseConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setPracticalTeachingEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setPracticalTeachingConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setThesisCourseEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setThesisCourseConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setScienceInnovationEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setScienceInnovationConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setCompetitionEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setCompetitionConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setTextbookEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setTextbookConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setEducationReformEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setEducationReformConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setEducationReformPaperEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setEducationReformPaperConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setProctorEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setProctorConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setGraduateTheoryCourseEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setGraduateTheoryCourseConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setGraduateGuideStudentEstimatedWorkload(BigDecimal.ZERO);
                totalWorkload.setGraduateGuideStudentConfirmedWorkload(BigDecimal.ZERO);
                totalWorkload.setTotalWorkload(BigDecimal.ZERO);
                return totalWorkload;
            } else {
                // 累加各项工作得到教学总工作量
                BigDecimal total = BigDecimal.ZERO
                        .add(totalWorkload.getTheoryCourseConfirmedWorkload() != null ? totalWorkload.getTheoryCourseConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getExperimentCourseConfirmedWorkload() != null ? totalWorkload.getExperimentCourseConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getPracticalTeachingConfirmedWorkload() != null ? totalWorkload.getPracticalTeachingConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getThesisCourseConfirmedWorkload() != null ? totalWorkload.getThesisCourseConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getScienceInnovationConfirmedWorkload() != null ? totalWorkload.getScienceInnovationConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getCompetitionConfirmedWorkload() != null ? totalWorkload.getCompetitionConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getTextbookConfirmedWorkload() != null ? totalWorkload.getTextbookConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getEducationReformConfirmedWorkload() != null ? totalWorkload.getEducationReformConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getEducationReformPaperConfirmedWorkload() != null ? totalWorkload.getEducationReformPaperConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getProctorConfirmedWorkload() != null ? totalWorkload.getProctorConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getGraduateTheoryCourseConfirmedWorkload() != null ? totalWorkload.getGraduateTheoryCourseConfirmedWorkload() : BigDecimal.ZERO)
                        .add(totalWorkload.getGraduateGuideStudentConfirmedWorkload() != null ? totalWorkload.getGraduateGuideStudentConfirmedWorkload() : BigDecimal.ZERO);
                totalWorkload.setTotalWorkload(total);
                // 将总工作量写入数据库
                teachingTotalWorkloadMapper.updateById(totalWorkload);
                return totalWorkload;
            }
        }
    }
}
