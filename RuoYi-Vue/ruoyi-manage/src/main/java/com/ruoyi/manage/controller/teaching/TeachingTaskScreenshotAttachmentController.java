package com.ruoyi.manage.controller.teaching;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.teaching.TeachingTaskScreenshotAttachment;
import com.ruoyi.manage.service.teaching.TeachingTaskScreenshotAttachmentService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 本科教学任务截图附件控制器。
 */
@RestController
@RequestMapping("/manage/teaching/task-screenshot")
public class TeachingTaskScreenshotAttachmentController {

    private static final Set<String> VALID_MODULE_TYPES = new HashSet<>(
            Arrays.asList("theory", "experiment", "practice", "graduate_theory"));

    @Autowired
    private TeachingTaskScreenshotAttachmentService attachmentService;

    @Autowired
    private ISysUserService userService;

    @GetMapping("/list")
    public AjaxResult list(@RequestParam Integer year,
                           @RequestParam String moduleType,
                           @RequestParam(required = false) Long userId) {
        String validationMessage = validateYearAndModule(year, moduleType);
        if (validationMessage != null) {
            return AjaxResult.error(validationMessage);
        }

        Long targetUserId = userId == null ? Long.valueOf(SecurityUtils.getUsername()) : userId;
        return AjaxResult.success(attachmentService.listByUserYearModule(targetUserId, year, moduleType));
    }

    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file,
                             @RequestParam Integer year,
                             @RequestParam String moduleType) {
        String validationMessage = validateYearAndModule(year, moduleType);
        if (validationMessage != null) {
            return AjaxResult.error(validationMessage);
        }

        try {
            Long userId = Long.valueOf(SecurityUtils.getUsername());
            String pdfUrl = userService.uploadPdf(file);

            TeachingTaskScreenshotAttachment attachment = new TeachingTaskScreenshotAttachment();
            attachment.setUserId(userId);
            attachment.setYear(year);
            attachment.setModuleType(moduleType);
            attachment.setFileUrl(pdfUrl);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setCreateTime(LocalDateTime.now());
            attachment.setUpdateTime(LocalDateTime.now());
            attachmentService.save(attachment);
            return AjaxResult.success("上传成功", attachment);
        } catch (Exception e) {
            return AjaxResult.error("文件上传失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        Long userId = Long.valueOf(SecurityUtils.getUsername());
        if (!attachmentService.removeOwnedAttachment(id, userId)) {
            return AjaxResult.error("只能删除本人上传的任务截图");
        }
        return AjaxResult.success("删除成功");
    }

    private String validateYearAndModule(Integer year, String moduleType) {
        if (year == null || year <= 0) {
            return "请选择有效年度";
        }
        if (!VALID_MODULE_TYPES.contains(moduleType)) {
            return "仅支持理论课、实验课或实践教学的任务截图";
        }
        return null;
    }
}
