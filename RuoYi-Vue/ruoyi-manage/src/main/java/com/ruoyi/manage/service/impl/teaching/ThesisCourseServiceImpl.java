package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.domain.teaching.ThesisCourse;
import com.ruoyi.manage.mapper.teaching.ThesisCourseMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.ThesisCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThesisCourseServiceImpl extends ServiceImpl<ThesisCourseMapper, ThesisCourse> implements ThesisCourseService {

    @Autowired
    private ThesisCourseMapper thesisCourseMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;

    @Override
    public IPage<ThesisCourse> getThesisCoursePage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<ThesisCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ThesisCourse> queryWrapper = new QueryWrapper<>();
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
        return thesisCourseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<ThesisCourse> getThesisCoursePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            thesisCourseMapper.chageUserName(userId, userName);
        });

        IPage<ThesisCourse> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ThesisCourse> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return thesisCourseMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditThesisCourse(Long id, String status, String remark) {
        ThesisCourse course = this.getById(id);
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
    public boolean addThesisCourse(ThesisCourse course) {
        // 此处可添加数据校验逻辑（如指导论文数非空、学生人数为正数等）
        return thesisCourseMapper.insert(course) > 0;
    }

    @Override
    public boolean removeThesisCourseByIds(List<Long> ids) {
        // 毕业论文表无关联文件，直接删除数据库记录（无需文件删除逻辑）
        return thesisCourseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 计算毕业论文的工作量（复用实验课计算逻辑框架，调整业务参数）
     * @param id     用户ID（用于查询职称系数）
     * @param course 毕业论文实体（包含指导论文数、学生人数、抽查结果等信息）
     * @return 毕业论文的工作量
     */
    @Override
    public double countWorkload(Long id, ThesisCourse course) {
        // 毕业论文工作量计算逻辑：指导学生完成毕业论文数×15×质量系数,规模系数：同时指导人数10人以内的部分系数为1.0，10人以上的部分系数为0.8；以上系数累积即为质量系数。
        // 计算超过10人的部分：同时指导人数10人以内的部分系数为1.0，10人以上的部分系数为0.8
        Integer baseCount;
        Integer extraCount;
        if(course.getStudentCount() <= 10){
            baseCount = course.getStudentCount();
            extraCount = 0;
        }else {
            baseCount = 10;
            extraCount = course.getStudentCount() - 10;
        }
        // 计算深度系数：论文通过校、院抽查的系数为1，否则为0.5
        double inspectionCoefficient = "pass".equals(course.getIsPassInspection()) ? 1.0 : 0.5;

        // 4. 计算总工作量（保留三位小数，与实验课格式一致） todo 目前默认论文数等于指导人数
//        double totalWorkload = (baseCount * (1.0 + inspectionCoefficient) + extraCount * (0.8 + inspectionCoefficient)) * 15; //严格按照公式
        double totalWorkload = course.getStudentCount() * 15; //暂不计算系数

        return Math.round(totalWorkload * 1000.0) / 1000.0;
    }

    /**
     * 计算并更新用户的毕业论文总工作量（写入总工作量表的thesis_course_workload字段）
     */


}