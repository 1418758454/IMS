package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.file.EvidenceFilePathUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.teaching.TeachingTaskScreenshotAttachment;
import com.ruoyi.manage.mapper.teaching.TeachingTaskScreenshotAttachmentMapper;
import com.ruoyi.manage.service.teaching.TeachingTaskScreenshotAttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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
    @Transactional(rollbackFor = Exception.class)
    public boolean removeOwnedAttachment(Long id, Long userId) {
        TeachingTaskScreenshotAttachment attachment = getOne(new LambdaQueryWrapper<TeachingTaskScreenshotAttachment>()
                .eq(TeachingTaskScreenshotAttachment::getId, id)
                .eq(TeachingTaskScreenshotAttachment::getUserId, userId));
        if (attachment == null) {
            return false;
        }

        File storageFile = EvidenceFilePathUtils.resolveStorageFile(
                RuoYiConfig.getProfile(), attachment.getFileUrl());
        if (storageFile == null) {
            throw new ServiceException("附件文件路径无效，无法删除");
        }
        if (!removeById(attachment.getId())) {
            return false;
        }
        if (storageFile.exists()
                && (!storageFile.isFile() || !FileUtils.deleteFile(storageFile.getAbsolutePath()))) {
            throw new ServiceException("附件记录已找到，但服务器文件删除失败，请联系管理员");
        }
        return true;
    }
}
