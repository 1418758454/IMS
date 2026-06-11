package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchSoftware;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.domain.teaching.UndergraduateTheoryCourse;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.mapper.teaching.TheoryCourseMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.TheoryCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TheoryCourseServiceImpl extends ServiceImpl<TheoryCourseMapper, UndergraduateTheoryCourse> implements TheoryCourseService {

    @Autowired
    private TheoryCourseMapper theoryCourseMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;

    @Override
    public IPage<UndergraduateTheoryCourse> getTheoryCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<UndergraduateTheoryCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UndergraduateTheoryCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 按用户ID筛选（当前登录教师）

        // 条件筛选：按授课年份（若year不为空）
        if (year != null) {
            queryWrapper.eq("year", year);
        }

        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
        }

        // 按创建时间先后顺序
        queryWrapper.orderByAsc("create_time");
        return theoryCourseMapper.selectPage(page, queryWrapper);

    }

    @Override
    public IPage<UndergraduateTheoryCourse> getTheoryCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            theoryCourseMapper.chageUserName(userId, userName);
        });

        IPage<UndergraduateTheoryCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UndergraduateTheoryCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds); // 按用户ID筛选（当前登录教师）

        // 条件筛选：按授课年份（若year不为空）
        if (year != null) {
            queryWrapper.eq("year", year);
        }

        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }

        // 按创建时间先后顺序
        queryWrapper.orderByAsc("create_time");
        return theoryCourseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean auditTheoryCourse(Long id, String status, String remark) {
        UndergraduateTheoryCourse course = this.getById(id);
        if (course == null) {
            return false;
        }

        // 更新审核状态和备注
        course.setStatus(status);
        course.setRemark(remark);
        course.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(course)){
//            // 更新总工作量
//            countTotalWorkload(course.getUserId(), course.getYear());
//            return true;
//        }
        return this.updateById(course);
    }


    @Override
    public boolean addTheoryCourse(UndergraduateTheoryCourse course) {
        // 此处可添加数据校验逻辑（如课程名称非空、课时为正数等）
        return theoryCourseMapper.insert(course) > 0;
    }

    @Override
    public boolean removeTheoryCourseByIds(List<Long> ids) {
        // 理论课表无关联文件，直接删除数据库记录（无需文件删除逻辑）
        return theoryCourseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 计算理论课的 workload
     *
     * @param id     理论课ID
     * @param course
     * @return 理论课的 workload
     */
    @Override
    public double countWorkload(Long id, UndergraduateTheoryCourse course) {
        // 理论课工作量计算逻辑：如果课程人数大于30则公式为课程计划学时×〔1+(教学班人数-30)×1.5%〕×课程系数×职称系数，否则为课程计划学时×课程系数×职称系数
        // 查询数据库的基本信息表中提取用户职称
        BasicInformation basicInformation = basicInformationService.getById(id);
        String title = basicInformation.getTitle();
        // 如果职称为教授，系数为1.4，副教授为1.2，讲师为1.0，否则为0.9
        double titleCoefficient = "教授".equals(title) ? 1.4 : "副教授".equals(title) ? 1.2 : "讲师".equals(title) ? 1.0 : 0.9;
        // 如果课程名包含全英或丁颖班，则课程系数为2.0，包含数学分析或者高等代数则课程系数为2.2，包含专业课为1.8，其他则都为1.0
        double courseCoefficient = course.getCourseType().equals("全英、丁颖班")? 2.0 : course.getCourseType().equals("数分高代") ? 2.2 : course.getCourseType().equals("专业课") ? 1.8 : 1.0;
        // 计算工作量
//        double workload = course.getStudentCount() > 30 ? course.getTheoryHours() * (1 + (course.getStudentCount() - 30) * 0.015) * courseCoefficient * titleCoefficient : course.getTheoryHours() * courseCoefficient * titleCoefficient;
//        return workload;
        double workload;
        if (course.getStudentCount() > 30) {
            workload = course.getTheoryHours() * (1 + (course.getStudentCount() - 30) * 0.015) * courseCoefficient* titleCoefficient;
        } else {
            workload = course.getTheoryHours() * courseCoefficient* titleCoefficient;
        }
        return Math.round(workload * 1000.0) / 1000.0;

    }

    @Override
    public double countTotalWorkload(Long userId, Integer year) {
        // 查询用户当年所有理论课记录
        List<UndergraduateTheoryCourse> allCourses = theoryCourseMapper.selectList(
                new QueryWrapper<UndergraduateTheoryCourse>().eq("user_id", userId).eq("year", year)
        );

        // 查询用户当年已通过审核的理论课记录
        List<UndergraduateTheoryCourse> confirmedCourses = theoryCourseMapper.selectList(
                new QueryWrapper<UndergraduateTheoryCourse>().eq("user_id", userId).eq("year", year).eq("status", "已通过")
        );

        // 计算预计总工作量（累加全部数据）
        double estimatedTotal = allCourses.stream()
                .mapToDouble(theoryCourse -> countWorkload(userId, theoryCourse))
                .sum();

        // 计算已确认总工作量（只累加审核状态为"已通过"的数据）
        double confirmedTotal = confirmedCourses.stream()
                .mapToDouble(theoryCourse -> countWorkload(userId, theoryCourse))
                .sum();

        // 根据userid将totalWorkload写入数据库表teaching_undergraduate_theory_course中
        // 先检查是否有对应userid和年份的记录，有则更新，无则插入
        String username = basicInformationService.getById(userId).getName();
        QueryWrapper<TeachingTotalWorkload> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("year", year);
        TeachingTotalWorkload teachingTotalWorkload = teachingTotalWorkloadMapper.selectOne(queryWrapper);
        if (teachingTotalWorkload == null) {
            teachingTotalWorkload = new TeachingTotalWorkload();
            teachingTotalWorkload.setUserId(BigDecimal.valueOf(userId));
            teachingTotalWorkload.setUserName(username);
            teachingTotalWorkload.setYear(year);
            teachingTotalWorkload.setTheoryCourseEstimatedWorkload(BigDecimal.valueOf(estimatedTotal));
            teachingTotalWorkload.setTheoryCourseConfirmedWorkload(BigDecimal.valueOf(confirmedTotal));
            teachingTotalWorkloadMapper.insert(teachingTotalWorkload);
        } else {
//            teachingTotalWorkload.setTheoryCourseWorkload(BigDecimal.valueOf(totalWorkload));
//            teachingTotalWorkloadMapper.updateById(teachingTotalWorkload);
            teachingTotalWorkload.setTheoryCourseEstimatedWorkload(BigDecimal.valueOf(estimatedTotal));
            teachingTotalWorkload.setTheoryCourseConfirmedWorkload(BigDecimal.valueOf(confirmedTotal));
            teachingTotalWorkloadMapper.update(teachingTotalWorkload,
                    new QueryWrapper<TeachingTotalWorkload>()
                            .eq("user_id", userId)
                            .eq("year", year));
        }
//        teachingTotalWorkloadMapper.updateTheoryById(userId, BigDecimal.valueOf(totalWorkload), year);
        return 0;
    }


}