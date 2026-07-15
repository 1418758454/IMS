package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.teaching.EducationReformPaper;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.teaching.EducationReformPaperMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.EducationReformPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教改论文Service实现类（重点复用科技创新的年份处理和总工作量计算逻辑）
 */
@Service
public class EducationReformPaperServiceImpl extends ServiceImpl<EducationReformPaperMapper, EducationReformPaper>
        implements EducationReformPaperService {

    @Autowired
    private EducationReformPaperMapper educationReformPaperMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper; // 总工作量表Mapper（与科技创新共用）
    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public IPage<EducationReformPaper> getEducationReformPaperPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<EducationReformPaper> page = new Page<>(pageNum, pageSize);
        QueryWrapper<EducationReformPaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 按当前用户筛选

        // 按提取的年份筛选（与科技创新的year字段筛选逻辑完全一致）
        if (year != null) {
            queryWrapper.eq("year", year);
        }

        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
        }

        queryWrapper.orderByAsc("create_time"); // 按创建时间排序（复用科技创新排序逻辑）
        return educationReformPaperMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<EducationReformPaper> getEducationReformPaperPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            educationReformPaperMapper.chageUserName(userId, userName);
        });

        IPage<EducationReformPaper> page = new Page<>(pageNum, pageSize);
        QueryWrapper<EducationReformPaper> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return educationReformPaperMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditEducationReformPaper(Long id, String status, String remark) {
        EducationReformPaper paper = this.getById(id);
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


    @Override
    public boolean addEducationReformPaper(EducationReformPaper paper) {
        int publishYear = paper.getPublishDate().getYear();
        paper.setYear(publishYear);
        return educationReformPaperMapper.insert(paper) > 0;
    }

    @Override
    public boolean removeEducationReformPaperByIds(List<Long> ids) {
        List<EducationReformPaper> papers = educationReformPaperMapper.selectBatchIds(ids);
        for (EducationReformPaper paper : papers) {
            if (StringUtils.isNotEmpty(paper.getPdfUrl())) {
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(paper.getPdfUrl());
                FileUtils.deleteFile(filePath);
            }
        }
        return educationReformPaperMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 核心：教改论文工作量计算
     * 规则：按项目级别设置固定工作量，保留三位小数（可根据实际需求调整系数）
     */
    @Override
    public double countWorkload(Long userId, EducationReformPaper paper) {
        // 项目级别系数：国家级5.0，省部级3.0，校级1.0（可配置化，此处硬编码仅为示例）
        double levelCoefficient;
        switch (paper.getLevel()) {
            case "一级期刊":
                levelCoefficient = 40.0;
                break;
            case "核心期刊":
                levelCoefficient = 20.0;
                break;
            case "其他期刊":
                levelCoefficient = 3.0;
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
     * 计算总工作量（直接累加workload字段，无需重新计算，与科技创新逻辑差异点）
     */



}
