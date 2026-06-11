package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.domain.teaching.PracticeCourse;
import com.ruoyi.manage.mapper.teaching.PracticeCourseMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.PracticeCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PracticeCourseServiceImpl extends ServiceImpl<PracticeCourseMapper, PracticeCourse> implements PracticeCourseService {

    @Autowired
    private PracticeCourseMapper practiceCourseMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;

    @Override
    public IPage<PracticeCourse> getPracticeCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<PracticeCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PracticeCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 按当前用户筛选

        // 按年份筛选（若year不为空）
        if (year != null) {
            queryWrapper.eq("year", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
        }

        // 按创建时间升序排序
        queryWrapper.orderByAsc("create_time");
        return practiceCourseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<PracticeCourse> getPracticeCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            practiceCourseMapper.chageUserName(userId, userName);
        });

        IPage<PracticeCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PracticeCourse> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return practiceCourseMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditPracticeCourse(Long id, String status, String remark) {
        PracticeCourse course = this.getById(id);
        if (course == null) {
            return false;
        }

        // 更新审核状态和备注
        course.setStatus(status);
        course.setRemark(remark);
        course.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(course)){
//            // 更新总工作量
//            practiceCourseService.countTotalWorkload(course.getUserId(), course.getYear());
//            return true;
//        }
        return this.updateById(course);
    }


    @Override
    public boolean addPracticeCourse(PracticeCourse course) {
        // 数据校验（示例：课程名称非空、计划天数≥1、班级数≥1）
        if (course.getCourseName() == null || course.getPlanDays() < 1 || course.getClassCount() < 1) {
            return false;
        }
        return practiceCourseMapper.insert(course) > 0;
    }

    @Override
    public boolean removePracticeCourseByIds(List<Long> ids) {
        // 直接删除数据库记录（无关联文件）
        return practiceCourseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 工作量计算逻辑（核心：替换实验课的theoryHours为planDays，studentCount为classCount）
     */
    @Override
    public double countWorkload(Long userId, PracticeCourse course) {

        // 工作量公式：计划天数×3×班级数（若分组，每组≥15人的组按一个班级计算）每周按5天计算
        double workload;
        workload = course.getPlanDays() * 3 * course.getClassCount();
        // 保留3位小数
        return Math.round(workload * 1000.0) / 1000.0;
    }

    /**
     * 更新总工作量表的教学实践字段（practice_course_workload）
     */
    @Override
    public double countTotalWorkload(Long userId, Integer year) {
        // 查询用户当年所有实践教学记录
        List<PracticeCourse> allCourses = practiceCourseMapper.selectList(
                new QueryWrapper<PracticeCourse>().eq("user_id", userId).eq("year", year)
        );

        // 查询用户当年已通过审核的实践教学记录
        List<PracticeCourse> confirmedCourses = practiceCourseMapper.selectList(
                new QueryWrapper<PracticeCourse>().eq("user_id", userId).eq("year", year).eq("status", "已通过")
        );

        // 计算预计总工作量（累加全部数据）
        double estimatedTotal = allCourses.stream()
                .mapToDouble(course -> countWorkload(userId, course))
                .sum();

        // 计算已确认总工作量（只累加审核状态为"已通过"的数据）
        double confirmedTotal = confirmedCourses.stream()
                .mapToDouble(course -> countWorkload(userId, course))
                .sum();

        // 3. 更新总工作量表（teaching_total_workload）的practice_course_workload字段
        BasicInformation info = basicInformationService.getById(userId);
        String username = info != null ? info.getName() : "";
        QueryWrapper<TeachingTotalWorkload> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("year", year);
        TeachingTotalWorkload teachingTotalWorkload = teachingTotalWorkloadMapper.selectOne(queryWrapper);

        if (teachingTotalWorkload == null) {
            // 新增总工作量记录
            teachingTotalWorkload = new TeachingTotalWorkload();
            teachingTotalWorkload.setUserId(BigDecimal.valueOf(userId));
            teachingTotalWorkload.setUserName(username);
            teachingTotalWorkload.setYear(year);
            teachingTotalWorkload.setPracticalTeachingEstimatedWorkload(BigDecimal.valueOf(estimatedTotal));
            teachingTotalWorkload.setPracticalTeachingConfirmedWorkload(BigDecimal.valueOf(confirmedTotal));
            teachingTotalWorkloadMapper.insert(teachingTotalWorkload);
        } else {
            // 更新已有记录
            teachingTotalWorkload.setPracticalTeachingEstimatedWorkload(BigDecimal.valueOf(estimatedTotal));
            teachingTotalWorkload.setPracticalTeachingConfirmedWorkload(BigDecimal.valueOf(confirmedTotal));
            teachingTotalWorkloadMapper.update(teachingTotalWorkload, queryWrapper);
        }

        return 0;
    }
}