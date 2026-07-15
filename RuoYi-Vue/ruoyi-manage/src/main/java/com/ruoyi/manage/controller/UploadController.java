package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制类
 * 与个人资料上传接口共用证明材料保存逻辑，支持 PDF 和图片。
 */
@RestController
@RequestMapping("/api/system")
public class UploadController {

    @Autowired
    private ISysUserService userService;

    @PostMapping("/upload")
    public AjaxResult uploadEvidence(@RequestParam("file") MultipartFile file) {
        try {
            String url = userService.uploadPdf(file);
            return AjaxResult.success("上传成功", new EvidenceUploadResult(url, file.getOriginalFilename()));
        } catch (Exception e) {
            return AjaxResult.error("文件上传失败：" + e.getMessage());
        }
    }

    static class EvidenceUploadResult {
        private final String url;
        private final String originalFilename;

        EvidenceUploadResult(String url, String originalFilename) {
            this.url = url;
            this.originalFilename = originalFilename;
        }

        public String getUrl() {
            return url;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }
    }
}
