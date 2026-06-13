package com.ruoyi.manage.service.statistics;

import com.ruoyi.manage.domain.statistics.StatisticsExportRequest;

import java.io.IOException;
import java.io.OutputStream;

public interface StatisticsExportService {
    void exportResearch(StatisticsExportRequest request, OutputStream outputStream) throws IOException;

    void exportTeaching(StatisticsExportRequest request, OutputStream outputStream) throws IOException;
}
