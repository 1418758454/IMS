package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.EducationReform;
import com.ruoyi.manage.domain.teaching.EducationReformPaper;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.teaching.EducationReformMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.EducationReformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教改项目Service实现（核心调整：移除单个工作量计算，总工作量直接累加）
 */
@Service
public class EducationReformServiceImpl extends ServiceImpl<EducationReformMapper, EducationReform>
        implements EducationReformService {

    @Autowired
    private EducationReformMapper educationReformMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;
    @Autowired
    private RuoYiConfig ruoYiConfig;

    // 分页查询（与科技创新逻辑一致，替换实体类）
    @Override
    public IPage<EducationReform> getEducationReformPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<EducationReform> page = new Page<>(pageNum, pageSize);
        QueryWrapper<EducationReform> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (year != null) {
            queryWrapper.eq("year", year); // 按页面选中年份筛选
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
        }
        queryWrapper.orderByAsc("create_time"); // 按创建时间排序
        return educationReformMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<EducationReform> getEducationReformPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            educationReformMapper.chageUserName(userId, userName);
        });

        IPage<EducationReform> page = new Page<>(pageNum, pageSize);
        QueryWrapper<EducationReform> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return educationReformMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditEducationReform(Long id, String status, String remark) {
        EducationReform paper = this.getById(id);
        if (paper == null) {
            return false;
        }

        // 更新审核状态和备注
        paper.setStatus(status);
        paper.setRemark(remark);
        paper.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(paper)){
//            // 更新总工作量
//            return true;
//        }
        return this.updateById(paper);
    }

    // 新增项目（直接保存前端工作量，从endDate提取年份）
    @Override
    public boolean addEducationReform(EducationReform reform) {
        // 核心：从endDate（LocalDate）提取年份设置到year字段（与科技创新endYear逻辑对齐）
        reform.setYear(reform.getEndDate().getYear());
        return educationReformMapper.insert(reform) > 0;
    }

    // 批量删除（复用科技创新逻辑）
    @Override
    public boolean removeEducationReformByIds(List<Long> ids) {
        List<EducationReform> reforms = educationReformMapper.selectBatchIds(ids);
        for (EducationReform reform : reforms) {
            if (StringUtils.isNotEmpty(reform.getPdfUrl())) {
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(reform.getPdfUrl());
                FileUtils.deleteFile(filePath);
            }
        }
        return educationReformMapper.deleteBatchIds(ids) > 0;
    }

    // 计算总工作量（核心调整：直接累加前端输入的workload，无需调用单个计算方法）




}
