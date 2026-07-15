package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.GraduateTheoryCourse;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.teaching.GraduateTheoryCourseMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.GraduateTheoryCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GraduateTheoryCourseServiceImpl extends ServiceImpl<GraduateTheoryCourseMapper, GraduateTheoryCourse> implements GraduateTheoryCourseService {

    @Autowired
    private GraduateTheoryCourseMapper graduateTheoryCourseMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;

    @Override
    public IPage<GraduateTheoryCourse> getGraduateTheoryCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<GraduateTheoryCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<GraduateTheoryCourse> queryWrapper = new QueryWrapper<>();
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
        return graduateTheoryCourseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<GraduateTheoryCourse> getGraduateTheoryCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            graduateTheoryCourseMapper.chageUserName(userId, userName);
        });

        IPage<GraduateTheoryCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<GraduateTheoryCourse> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return graduateTheoryCourseMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditGraduateTheoryCourse(Long id, String status, String remark) {
        GraduateTheoryCourse course = this.getById(id);
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
    public boolean addGraduateTheoryCourse(GraduateTheoryCourse course) {
        // 此处可添加数据校验逻辑（如课程名称非空、课时为正数等）
        return graduateTheoryCourseMapper.insert(course) > 0;
    }

    @Override
    public boolean removeGraduateTheoryCourseByIds(List<Long> ids) {
        // 理论课表无关联文件，直接删除数据库记录（无需文件删除逻辑）
        return graduateTheoryCourseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 计算研究生理论课工作量
     * 逻辑：与本科理论课一致，基于职称系数和课程系数计算
     */
    @Override
    public double countWorkload(Long id, GraduateTheoryCourse course) {
        BasicInformation basicInformation = basicInformationService.getById(id);
        String title = basicInformation.getTitle();
        double titleCoefficient = "教授".equals(title) ? 1.4 : "副教授".equals(title) ? 1.2 : "讲师".equals(title) ? 1.0 : 0.9;
        double courseCoefficient = course.getCourseType().equals("硕士课程")? 1.5 : course.getCourseType().equals("博士课程") ? 2 :
                course.getCourseType().equals("全英硕士课程") ? 3.0 : course.getCourseType().equals("全英博士课程") ? 4.0 :
                course.getCourseType().equals("硕士“金课”") ? 3.0 : course.getCourseType().equals("博士“金课”") ? 4.0 : 1.0;
        System.out.println("课程系数：" + courseCoefficient + course.getCourseType());

        double workload;
        if(course.getIsExperimentalCourse().equals("0")){ //todo 是否为01
            // 不是实验课的工作量计算
            if (course.getStudentCount() > 20) {
                workload = course.getTheoryHours() * (1 + (course.getStudentCount() - 20) * 0.015) * courseCoefficient * titleCoefficient;
            } else {
                workload = course.getTheoryHours() * courseCoefficient * titleCoefficient;
            }
        }else{
            // 是实验课的工作量计算
            if (course.getStudentCount() > 20) {
                workload = course.getTheoryHours() * (1 + (course.getStudentCount() - 20) * 0.01) * courseCoefficient;
            } else {
                workload = course.getTheoryHours() * courseCoefficient;
            }
        }
        return Math.round(workload * 1000.0) / 1000.0;
    }

    /**
     * 计算研究生理论课总工作量并更新到教学总工作量表
     */

}