package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.domain.teaching.ScienceInnovation;
import com.ruoyi.manage.mapper.teaching.ScienceInnovationMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.ScienceInnovationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 科技创新服务实现类（与实验课ServiceImpl逻辑完全对齐，仅调整工作量计算规则）
 */
@Service
public class ScienceInnovationServiceImpl extends ServiceImpl<ScienceInnovationMapper, ScienceInnovation> implements ScienceInnovationService {

    @Autowired
    private ScienceInnovationMapper scienceInnovationMapper;
    @Autowired
    private BasicInformationService basicInformationService; // 复用实验课的基础信息服务（获取用户名）
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper; // 总工作量表Mapper（与实验课共用）
    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public IPage<ScienceInnovation> getScienceInnovationPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<ScienceInnovation> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ScienceInnovation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 按当前登录用户筛选（与实验课一致）

        // 按页面选中年份筛选（与实验课year字段逻辑一致）
        if (year != null) {
            queryWrapper.eq("year", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
        }

        // 按创建时间升序排序（与实验课排序规则一致）
        queryWrapper.orderByAsc("create_time");
        return scienceInnovationMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<ScienceInnovation> getScienceInnovationPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            scienceInnovationMapper.chageUserName(userId, userName);
        });

        IPage<ScienceInnovation> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ScienceInnovation> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return scienceInnovationMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditScienceInnovation(Long id, String status, String remark) {
        ScienceInnovation innovation = this.getById(id);
        if (innovation == null) {
            return false;
        }

        // 更新审核状态和备注
        innovation.setStatus(status);
        innovation.setRemark(remark);
        innovation.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(innovation)){
//            // 更新总工作量
//            scienceInnovationService.countTotalWorkload(innovation.getUserId(), innovation.getYear());
//            return true;
//        }
        return this.updateById(innovation);
    }


    @Override
    public boolean addScienceInnovation(ScienceInnovation innovation) {
        innovation.setYear(innovation.getEndYear()); // 以结题年份作为保存的年份字段
        return scienceInnovationMapper.insert(innovation) > 0;
    }

    @Override
    public boolean removeScienceInnovationByIds(List<Long> ids) {
        List<ScienceInnovation> innovations = scienceInnovationMapper.selectBatchIds(ids);
        for (ScienceInnovation innovation : innovations) {
            if (StringUtils.isNotEmpty(innovation.getPdfUrl())) {
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(innovation.getPdfUrl());
                FileUtils.deleteFile(filePath);
            }
        }
        return scienceInnovationMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 核心：科技创新工作量计算规则（类比实验课的课程系数+学时+人数逻辑）
     * 规则：按项目级别设置固定工作量，保留三位小数（可根据实际需求调整系数）
     */
    @Override
    public double countWorkload(Long userId, ScienceInnovation innovation) {
        // 项目级别系数：国家级5.0，省部级3.0，校级1.0（可配置化，此处硬编码仅为示例）
        double levelCoefficient;
        switch (innovation.getLevel()) {
            case "国家级":
                levelCoefficient = 50.0;
                break;
            case "省部级":
                levelCoefficient = 30.0;
                break;
            case "校级":
                levelCoefficient = 15.0;
                break;
            default:
                levelCoefficient = 0.0; // 非法级别工作量为0
        }

        // 工作量公式：级别系数（无其他参数，可扩展职称系数等）
        double workload = levelCoefficient;

        // 保留三位小数（与实验课精度一致）
        return Math.round(workload * 1000.0) / 1000.0;
    }

    /**
     * 计算总工作量并更新到TeachingTotalWorkload表（与实验课逻辑完全一致，仅修改字段名）
     */
    @Override
    public double countTotalWorkload(Long userId, Integer year) {
        // 查询用户当年所有科技创新记录
        List<ScienceInnovation> allInnovations = scienceInnovationMapper.selectList(
                new QueryWrapper<ScienceInnovation>().eq("user_id", userId).eq("year", year)
        );

        // 查询用户当年已通过审核的科技创新记录
        List<ScienceInnovation> confirmedInnovations = scienceInnovationMapper.selectList(
                new QueryWrapper<ScienceInnovation>().eq("user_id", userId).eq("year", year).eq("status", "已通过")
        );

        // 计算预计总工作量（累加全部数据）
        double estimatedTotal = allInnovations.stream()
                .mapToDouble(innovation -> countWorkload(userId, innovation))
                .sum();

        // 计算已确认总工作量（只累加审核状态为"已通过"的数据）
        double confirmedTotal = confirmedInnovations.stream()
                .mapToDouble(innovation -> countWorkload(userId, innovation))
                .sum();

        // 3. 更新总工作量表（TeachingTotalWorkload）的科技创新字段（假设字段名为science_innovation_workload）
        BasicInformation basicInfo = basicInformationService.getById(userId);
        String username = basicInfo != null ? basicInfo.getName() : ""; // 获取用户姓名

        QueryWrapper<TeachingTotalWorkload> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("year", year);
        TeachingTotalWorkload totalWorkloadRecord = teachingTotalWorkloadMapper.selectOne(queryWrapper);

        if (totalWorkloadRecord == null) {
            // 新增总工作量记录
            totalWorkloadRecord = new TeachingTotalWorkload();
            totalWorkloadRecord.setUserId(BigDecimal.valueOf(userId));
            totalWorkloadRecord.setUserName(username);
            totalWorkloadRecord.setYear(year);
            totalWorkloadRecord.setScienceInnovationEstimatedWorkload(BigDecimal.valueOf(estimatedTotal));
            totalWorkloadRecord.setScienceInnovationConfirmedWorkload(BigDecimal.valueOf(confirmedTotal));
            teachingTotalWorkloadMapper.insert(totalWorkloadRecord);
        } else {
            // 更新已有记录
            totalWorkloadRecord.setScienceInnovationEstimatedWorkload(BigDecimal.valueOf(estimatedTotal));
            totalWorkloadRecord.setScienceInnovationConfirmedWorkload(BigDecimal.valueOf(confirmedTotal));
            teachingTotalWorkloadMapper.update(totalWorkloadRecord, queryWrapper);
        }

        return 0;
    }
}
