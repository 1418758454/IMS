package com.ruoyi.manage.controller.statistics;

import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.statistics.StatisticsExportRequest;
import com.ruoyi.manage.service.statistics.StatisticsExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/manage/statistics/export")
public class StatisticsExportController {

    @Autowired
    private StatisticsExportService statisticsExportService;

    @PostMapping("/research")
    public void exportResearch(@RequestBody StatisticsExportRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(contentType(request));
        FileUtils.setAttachmentResponseHeader(response, buildFileName("科研统计", request));
        statisticsExportService.exportResearch(request, response.getOutputStream());
    }

    @PostMapping("/teaching")
    public void exportTeaching(@RequestBody StatisticsExportRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(contentType(request));
        FileUtils.setAttachmentResponseHeader(response, buildFileName("教学统计", request));
        statisticsExportService.exportTeaching(request, response.getOutputStream());
    }

    private String buildFileName(String prefix, StatisticsExportRequest request) {
        String year = request != null && request.getYear() != null && request.getYear().trim().length() > 0
                ? request.getYear().trim()
                : "全部年份";
        return prefix + "_" + year + (includePdf(request) ? ".zip" : ".xlsx");
    }

    private String contentType(StatisticsExportRequest request) {
        return includePdf(request)
                ? "application/zip"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    private boolean includePdf(StatisticsExportRequest request) {
        return request != null && Boolean.TRUE.equals(request.getIncludePdf());
    }
}
