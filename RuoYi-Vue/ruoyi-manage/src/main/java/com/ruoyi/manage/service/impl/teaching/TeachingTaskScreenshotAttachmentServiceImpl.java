package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.teaching.TeachingTaskScreenshotAttachment;
import com.ruoyi.manage.mapper.teaching.TeachingTaskScreenshotAttachmentMapper;
import com.ruoyi.manage.service.teaching.TeachingTaskScreenshotAttachmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachingTaskScreenshotAttachmentServiceImpl
        extends ServiceImpl<TeachingTaskScreenshotAttachmentMapper, TeachingTaskScreenshotAttachment>
        implements TeachingTaskScreenshotAttachmentService {

    @Override
    public List<TeachingTaskScreenshotAttachment> listByUserYearModule(Long userId, Integer year, String moduleType) {
        return list(new LambdaQueryWrapper<TeachingTaskScreenshotAttachment>()
                .eq(TeachingTaskScreenshotAttachment::getUserId, userId)
                .eq(TeachingTaskScreenshotAttachment::getYear, year)
                .eq(TeachingTaskScreenshotAttachment::getModuleType, moduleType)
                .orderByDesc(TeachingTaskScreenshotAttachment::getCreateTime)
                .orderByDesc(TeachingTaskScreenshotAttachment::getId));
    }

    @Override
    public boolean hasAttachment(Long userId, Integer year, String moduleType) {
        return count(new LambdaQueryWrapper<TeachingTaskScreenshotAttachment>()
                .eq(TeachingTaskScreenshotAttachment::getUserId, userId)
                .eq(TeachingTaskScreenshotAttachment::getYear, year)
                .eq(TeachingTaskScreenshotAttachment::getModuleType, moduleType)) > 0;
    }

    @Override
    public boolean removeOwnedAttachment(Long id, Long userId) {
        TeachingTaskScreenshotAttachment attachment = getOne(new LambdaQueryWrapper<TeachingTaskScreenshotAttachment>()
                .eq(TeachingTaskScreenshotAttachment::getId, id)
                .eq(TeachingTaskScreenshotAttachment::getUserId, userId));
        return attachment != null && removeById(attachment.getId());
    }
}
