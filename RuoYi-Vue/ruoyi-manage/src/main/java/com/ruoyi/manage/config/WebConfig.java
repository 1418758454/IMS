//package com.ruoyi.manage.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // 配置静态资源映射：URL路径 -> 本地文件夹路径
//        registry.addResourceHandler("/pdfupload/**") // 前端访问的URL前缀
//                .addResourceLocations("file:C:/Users/jimmy/Desktop/pdfupload/"); // 本地文件夹绝对路径
//        // 注意：路径末尾必须加斜杠“/”，否则映射会出错
//    }
//}