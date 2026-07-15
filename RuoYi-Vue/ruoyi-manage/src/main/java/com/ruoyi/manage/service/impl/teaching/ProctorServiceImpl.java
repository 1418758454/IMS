package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.Proctor;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.teaching.ProctorMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.ProctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 监考模块服务实现类
 */
@Service
public class ProctorServiceImpl extends ServiceImpl<ProctorMapper, Proctor> implements ProctorService {

    @Autowired
    private ProctorMapper proctorMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;

    @Override
    public IPage<Proctor> getProctorPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<Proctor> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Proctor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 按当前用户筛选

        if (year != null) {
            queryWrapper.eq("year", year); // 按年份筛选
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
        }

        queryWrapper.orderByAsc("create_time"); // 按创建时间排序
        return proctorMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Proctor> getProctorPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            proctorMapper.chageUserName(userId, userName);
        });

        IPage<Proctor> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Proctor> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return proctorMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditProctor(Long id, String status, String remark) {
        Proctor proctor = this.getById(id);
        if (proctor == null) {
            return false;
        }

        // 更新审核状态和备注
        proctor.setStatus(status);
        proctor.setRemark(remark);
        proctor.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(proctor)){
//            // 更新总工作量
//            return true;
//        }
        return this.updateById(proctor);
    }


    @Override
    public boolean addProctor(Proctor proctor) {
        return proctorMapper.insert(proctor) > 0;
    }

    @Override
    public boolean removeProctorByIds(List<Long> ids) {
        return proctorMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 工作量计算
     */
    @Override
    public double countWorkload(Long userId, Proctor proctor) {
        // 工作量 = 监考次数 * 2
        double workload = proctor.getExamCount() * 2.0;
        return Math.round(workload * 1000.0) / 1000.0; // 保留三位小数
    }

    /**
     * 更新总工作量表中的监考模块工作量
     */

}