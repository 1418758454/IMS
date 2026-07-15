package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.GraduateGuideStudent;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.teaching.GraduateGuideStudentMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.GraduateGuideStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 研究生指导学生Service实现（工作量直接累加前端输入值）
 */
@Service
public class GraduateGuideStudentServiceImpl extends ServiceImpl<GraduateGuideStudentMapper, GraduateGuideStudent>
        implements GraduateGuideStudentService {

    @Autowired
    private GraduateGuideStudentMapper graduateGuideStudentMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;

    // 分页查询指导记录
    @Override
    public IPage<GraduateGuideStudent> getGraduateGuideStudentPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<GraduateGuideStudent> page = new Page<>(pageNum, pageSize);
        QueryWrapper<GraduateGuideStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (year != null) {
            queryWrapper.eq("year", year); // 按前端选中年份筛选
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
        }
        queryWrapper.orderByAsc("create_time");
        return graduateGuideStudentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<GraduateGuideStudent> getGraduateGuideStudentPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            graduateGuideStudentMapper.chageUserName(userId, userName);
        });

        IPage<GraduateGuideStudent> page = new Page<>(pageNum, pageSize);
        QueryWrapper<GraduateGuideStudent> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return graduateGuideStudentMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditGraduateGuideStudent(Long id, String status, String remark) {
        GraduateGuideStudent guideStudent = this.getById(id);
        if (guideStudent == null) {
            return false;
        }

        // 更新审核状态和备注
        guideStudent.setStatus(status);
        guideStudent.setRemark(remark);
        guideStudent.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(guideStudent)){
//            // 更新总工作量
//            return true;
//        }
        return this.updateById(guideStudent);
    }


    // 新增指导记录（直接保存前端传入的year和workload）
    @Override
    public boolean addGraduateGuideStudent(GraduateGuideStudent guideStudent) {
        // 无需处理年份提取（year由前端直接提供）
        return graduateGuideStudentMapper.insert(guideStudent) > 0;
    }

    // 批量删除指导记录
    @Override
    public boolean removeGraduateGuideStudentByIds(List<Long> ids) {
        return graduateGuideStudentMapper.deleteBatchIds(ids) > 0;
    }

    // 计算模块总工作量（直接累加前端输入的workload）

}