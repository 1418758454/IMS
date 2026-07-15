package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.domain.teaching.ExperimentCourse;
import com.ruoyi.manage.mapper.teaching.ExperimentCourseMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.ExperimentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExperimentCourseServiceImpl extends ServiceImpl<ExperimentCourseMapper, ExperimentCourse> implements ExperimentCourseService {

    @Autowired
    private ExperimentCourseMapper experimentCourseMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;

    @Override
    public IPage<ExperimentCourse> getExperimentCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<ExperimentCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ExperimentCourse> queryWrapper = new QueryWrapper<>();
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
        return experimentCourseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<ExperimentCourse> getExperimentCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            experimentCourseMapper.chageUserName(userId, userName);
        });

        IPage<ExperimentCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ExperimentCourse> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return experimentCourseMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditExperimentCourse(Long id, String status, String remark) {
        ExperimentCourse course = this.getById(id);
        if (course == null) {
            return false;
        }

        // 更新审核状态和备注
        course.setStatus(status);
        course.setRemark(remark);
        course.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(course)){
//            // 更新总工作量
//            return true;
//        }
        return this.updateById(course);
    }


    @Override
    public boolean addExperimentCourse(ExperimentCourse course) {
        // 此处可添加数据校验逻辑（如课程名称非空、课时为正数等）
        return experimentCourseMapper.insert(course) > 0;
    }

    @Override
    public boolean removeExperimentCourseByIds(List<Long> ids) {
        // 实验课表无关联文件，直接删除数据库记录（无需文件删除逻辑）
        return experimentCourseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 计算实验课的工作量（复用理论课计算逻辑）
     * @param id     用户ID（用于查询职称系数）
     * @param course 实验课实体（包含课时、学生人数等信息）
     * @return 实验课的工作量
     */
    @Override
    public double countWorkload(Long id, ExperimentCourse course) {
        // 实验课工作量计算逻辑：与理论课完全一致
        // 公式：课程计划学时×〔1+(教学班人数-30)×1.0%〕×课程系数（人数>30时）
        // 或：课程计划学时×课程系数×职称系数（人数≤30时）

        // 课程系数：全英/丁颖班2.0，专业课1.8，其他1.0
        double courseCoefficient = course.getCourseType().equals("全英、丁颖班") ? 2.0 :
                        course.getCourseType().equals("专业实验课") ? 1.8 : 1.0;
        // 计算工作量
        double workload;
        if (course.getStudentCount() > 30) {
            workload = course.getTheoryHours() * (1 + (course.getStudentCount() - 30) * 0.01) * courseCoefficient;
        } else {
            workload = course.getTheoryHours() * courseCoefficient;
        }
        return Math.round(workload * 1000.0) / 1000.0;

    }

    /**
     * 计算并更新用户的实验课总工作量（写入总工作量表的practical_course_workload字段）
     */


}