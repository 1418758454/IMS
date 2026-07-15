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
import com.ruoyi.manage.domain.teaching.Textbook;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.mapper.teaching.TextbookMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.teaching.TextbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出版教材服务实现类（核心：出版时间年份提取逻辑与科技创新结题年份对齐）
 */
@Service
public class TextbookServiceImpl extends ServiceImpl<TextbookMapper, Textbook> implements TextbookService {

    @Autowired
    private TextbookMapper textbookMapper;
    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;
    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public IPage<Textbook> getTextbookPage(Integer pageNum, Integer pageSize, String userId, Integer year, Boolean isAudit) {
        IPage<Textbook> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Textbook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        // 按出版年份筛选（与科技创新year字段逻辑一致）
        if (year != null) {
            queryWrapper.eq("year", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
        }

        queryWrapper.orderByAsc("create_time");
        return textbookMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Textbook> getTextbookPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, Integer year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            textbookMapper.chageUserName(userId, userName);
        });

        IPage<Textbook> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Textbook> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", userIds)
                .eq(year != null, "year", year)
                .ne(isAudit == true, "status", "已通过")
                .orderByAsc("create_time");
        return textbookMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean auditTextbook(Long id, String status, String remark) {
        Textbook textbook = this.getById(id);
        if (textbook == null) {
            return false;
        }

        // 更新审核状态和备注
        textbook.setStatus(status);
        textbook.setRemark(remark);
        textbook.setUpdateTime(LocalDateTime.now());

//        if(this.updateById(textbook)){
//            // 更新总工作量
//            return true;
//        }
        return this.updateById(textbook);
    }


    @Override
    public boolean addTextbook(Textbook textbook) {
        // 提取出版时间的年份（核心：与科技创新endYear处理逻辑一致）
        Integer publishYear = textbook.getPublishDate().getYear();
        textbook.setYear(publishYear);
        return textbookMapper.insert(textbook) > 0;
    }

    @Override
    public boolean removeTextbookByIds(List<Long> ids) {
        List<Textbook> textbooks = textbookMapper.selectBatchIds(ids);
        for (Textbook textbook : textbooks) {
            if (StringUtils.isNotEmpty(textbook.getPdfUrl())) {
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(textbook.getPdfUrl());
                FileUtils.deleteFile(filePath);
            }
        }
        return textbookMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 工作量计算规则（示例：按级别+编写身份加权计算）
     * 可根据实际需求调整系数，此处仅为示例
     */
    @Override
    public double countWorkload(Long userId, Textbook textbook) {
        double baseCoeff = 0.0;
        if ("国家级规划教材".equals(textbook.getLevel())) {
            if ("主编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 120.0;
            } else if ("副主编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 60.0;
            } else if ("参编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 30.0;
            }
//            else {
//                baseCoeff = 60.0;
//            }
        } else if ("省部级规划教材".equals(textbook.getLevel())) {
            if ("主编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 60.0;
            } else if ("副主编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 30.0;
            } else if ("参编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 0.0;
            }
        } else if ("一般教材".equals(textbook.getLevel())) {
            if ("主编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 30.0;
            } else if ("副主编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 0.0;
            } else if ("参编".equals(textbook.getCompilerIdentity())) {
                baseCoeff = 0.0;
            }
        }

        // 总工作量 = 级别系数 × 身份系数（保留三位小数）
        return Math.round(baseCoeff * 1000.0) / 1000.0;
    }

    /**
     * 更新总工作量表（与科技创新逻辑完全一致，仅修改字段名）
     */

}
