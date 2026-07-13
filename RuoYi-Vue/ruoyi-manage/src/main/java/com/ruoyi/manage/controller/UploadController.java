package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

/**
 * 文件上传控制类
 * 具体上传逻辑查看ruoyi-system\src\main\java\com\ruoyi\system\service\impl\SysUserServiceImpl.java
 */
@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "http://43.139.98.113:81")
public class UploadController {
    private static final String UPLOAD_PATH = "/www/wwwroot/pdfupload/source";
    private static final String PDF_URL_PREFIX = "http://43.139.98.113:81/source/";

    @PostMapping("/upload")
    public AjaxResult uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            StoredPdf storedPdf = storePdf(file);
            return AjaxResult.success("上传成功", new PdfUploadResult(storedPdf.getUrl()));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("文件上传失败：" + e.getMessage());
        }
    }

    public static StoredPdf storePdf(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            throw new IllegalArgumentException("仅支持PDF格式文件");
        }
        if (file.getSize() > 100L * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过100MB");
        }

        File uploadDirectory = new File(UPLOAD_PATH);
        if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
            throw new IOException("无法创建上传目录");
        }

        String fileName = UUID.randomUUID().toString() + ".pdf";
        file.transferTo(new File(uploadDirectory, fileName));
        return new StoredPdf(PDF_URL_PREFIX + fileName, originalName);
    }

    public static class StoredPdf {
        private final String url;
        private final String originalName;

        public StoredPdf(String url, String originalName) {
            this.url = url;
            this.originalName = originalName;
        }

        public String getUrl() {
            return url;
        }

        public String getOriginalName() {
            return originalName;
        }
    }

    static class PdfUploadResult {
        private String url;
        public PdfUploadResult(String url) { this.url = url; }
        public String getUrl() { return url; }
    }
}
