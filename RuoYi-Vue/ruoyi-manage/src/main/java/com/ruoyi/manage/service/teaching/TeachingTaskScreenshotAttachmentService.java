package com.ruoyi.manage.service.teaching;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.teaching.TeachingTaskScreenshotAttachment;

import java.util.List;

/**
 * 本科教学任务截图附件服务。
 */
public interface TeachingTaskScreenshotAttachmentService extends IService<TeachingTaskScreenshotAttachment> {

    List<TeachingTaskScreenshotAttachment> listByUserYearModule(Long userId, Integer year, String moduleType);

    boolean hasAttachment(Long userId, Integer year, String moduleType);

    boolean removeOwnedAttachment(Long id, Long userId);
}
