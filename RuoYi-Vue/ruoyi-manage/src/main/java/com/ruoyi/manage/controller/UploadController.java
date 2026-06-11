package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.constant.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
//    private static final String UPLOAD_PATH = "C:/Users/jimmy/Desktop/pdfupload/test/";
//    private static final String PDF_URL_PREFIX = "http://localhost:8080/pdfupload/";

    @PostMapping("/upload")
    public AjaxResult uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空"); // 使用现有error方法
        }

        try {
            String originalName = file.getOriginalFilename();
            String suffix = originalName.substring(originalName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            File destFile = new File(UPLOAD_PATH + fileName);
            file.transferTo(destFile);

            String pdfUrl = PDF_URL_PREFIX + fileName;
            // 使用现有success方法：success(String msg, Object data)
            return AjaxResult.success("上传成功", new PdfUploadResult(pdfUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("文件上传失败：" + e.getMessage()); // 使用现有error方法
        }
    }

    static class PdfUploadResult {
        private String url;
        public PdfUploadResult(String url) { this.url = url; }
        public String getUrl() { return url; }
    }
}