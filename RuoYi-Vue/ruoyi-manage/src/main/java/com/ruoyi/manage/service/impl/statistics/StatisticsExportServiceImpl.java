package com.ruoyi.manage.service.impl.statistics;

import com.ruoyi.common.config.RuoYiConfig;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchAward;
import com.ruoyi.manage.domain.research.ResearchMonograph;
import com.ruoyi.manage.domain.research.ResearchPaper;
import com.ruoyi.manage.domain.research.ResearchPatent;
import com.ruoyi.manage.domain.research.ResearchSoftware;
import com.ruoyi.manage.domain.research.ResearchSubject;
import com.ruoyi.manage.domain.statistics.StatisticsExportRequest;
import com.ruoyi.manage.domain.teaching.Competition;
import com.ruoyi.manage.domain.teaching.EducationReform;
import com.ruoyi.manage.domain.teaching.EducationReformPaper;
import com.ruoyi.manage.domain.teaching.ExperimentCourse;
import com.ruoyi.manage.domain.teaching.GraduateGuideStudent;
import com.ruoyi.manage.domain.teaching.GraduateTheoryCourse;
import com.ruoyi.manage.domain.teaching.PracticeCourse;
import com.ruoyi.manage.domain.teaching.Proctor;
import com.ruoyi.manage.domain.teaching.ScienceInnovation;
import com.ruoyi.manage.domain.teaching.Textbook;
import com.ruoyi.manage.domain.teaching.ThesisCourse;
import com.ruoyi.manage.domain.teaching.UndergraduateTheoryCourse;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.AwardService;
import com.ruoyi.manage.service.research.MonographService;
import com.ruoyi.manage.service.research.PaperService;
import com.ruoyi.manage.service.research.PatentService;
import com.ruoyi.manage.service.research.SoftwareService;
import com.ruoyi.manage.service.research.SubjectService;
import com.ruoyi.manage.service.statistics.StatisticsExportService;
import com.ruoyi.manage.service.teaching.CompetitionService;
import com.ruoyi.manage.service.teaching.EducationReformPaperService;
import com.ruoyi.manage.service.teaching.EducationReformService;
import com.ruoyi.manage.service.teaching.ExperimentCourseService;
import com.ruoyi.manage.service.teaching.GraduateGuideStudentService;
import com.ruoyi.manage.service.teaching.GraduateTheoryCourseService;
import com.ruoyi.manage.service.teaching.PracticeCourseService;
import com.ruoyi.manage.service.teaching.ProctorService;
import com.ruoyi.manage.service.teaching.ScienceInnovationService;
import com.ruoyi.manage.service.teaching.TextbookService;
import com.ruoyi.manage.service.teaching.ThesisCourseService;
import com.ruoyi.manage.service.teaching.TheoryCourseService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class StatisticsExportServiceImpl implements StatisticsExportService {

    private static final String SCOPE_YEAR_ALL = "YEAR_ALL";
    private static final String PDF_URL_SOURCE_MARK = "/source/";
    private static final String PDF_UPLOAD_PATH = "/www/wwwroot/pdfupload/source";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private BasicInformationService basicInformationService;

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private PaperService paperService;
    @Autowired
    private MonographService monographService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private PatentService patentService;
    @Autowired
    private SoftwareService softwareService;

    @Autowired
    private TheoryCourseService theoryCourseService;
    @Autowired
    private ExperimentCourseService experimentCourseService;
    @Autowired
    private PracticeCourseService practiceCourseService;
    @Autowired
    private ThesisCourseService thesisCourseService;
    @Autowired
    private ScienceInnovationService scienceInnovationService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private TextbookService textbookService;
    @Autowired
    private EducationReformService educationReformService;
    @Autowired
    private EducationReformPaperService educationReformPaperService;
    @Autowired
    private ProctorService proctorService;
    @Autowired
    private GraduateTheoryCourseService graduateTheoryCourseService;
    @Autowired
    private GraduateGuideStudentService graduateGuideStudentService;

    @Override
    public void exportResearch(StatisticsExportRequest request, OutputStream outputStream) throws IOException {
        writeExport("科研统计", buildResearchModules(), request, outputStream);
    }

    @Override
    public void exportTeaching(StatisticsExportRequest request, OutputStream outputStream) throws IOException {
        writeExport("教学统计", buildTeachingModules(), request, outputStream);
    }

    private void writeExport(String title, List<ModuleDefinition<?>> modules, StatisticsExportRequest request, OutputStream outputStream) throws IOException {
        List<PdfAttachment> attachments = new ArrayList<>();
        byte[] workbookBytes = buildWorkbook(modules, request, attachments);
        StatisticsExportRequest exportRequest = request == null ? new StatisticsExportRequest() : request;
        if (Boolean.TRUE.equals(exportRequest.getIncludePdf())) {
            writeZip(title, modules, exportRequest, workbookBytes, attachments, outputStream);
        } else {
            outputStream.write(workbookBytes);
        }
    }

    private byte[] buildWorkbook(List<ModuleDefinition<?>> modules, StatisticsExportRequest request, List<PdfAttachment> attachments) throws IOException {
        StatisticsExportRequest exportRequest = request == null ? new StatisticsExportRequest() : request;
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CellStyle headerStyle = buildHeaderStyle(workbook);
            CellStyle textStyle = buildTextStyle(workbook);
            Map<String, BasicInformation> userMap = buildUserMap();
            List<String> userIds = resolveUserIds(exportRequest);

            for (ModuleDefinition<?> module : modules) {
                writeSheet(workbook, headerStyle, textStyle, module, exportRequest, userIds, userMap, attachments);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private <T> void writeSheet(Workbook workbook, CellStyle headerStyle, CellStyle textStyle,
                                ModuleDefinition<T> module, StatisticsExportRequest request,
                                List<String> userIds, Map<String, BasicInformation> userMap,
                                List<PdfAttachment> attachments) {
        Sheet sheet = workbook.createSheet(module.sheetName);
        Row header = sheet.createRow(0);
        int headerColumn = 0;
        createCell(header, headerColumn++, "部门", headerStyle);
        for (ColumnDefinition column : module.columns) {
            createCell(header, headerColumn++, column.header, headerStyle);
        }

        List<T> rows = queryRows(module.service, request, userIds);
        int rowIndex = 1;
        for (T data : rows) {
            Row row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            String deptName = getDeptName(data, userMap);
            createCell(row, columnIndex++, deptName, textStyle);
            for (ColumnDefinition column : module.columns) {
                createCell(row, columnIndex++, formatValue(readProperty(data, column.property)), textStyle);
            }
            collectPdfAttachment(module.sheetName, data, deptName, attachments);
        }

        int totalColumns = module.columns.size() + 1;
        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.min(Math.max(currentWidth + 1024, 3000), 12000));
        }
    }

    private void writeZip(String title, List<ModuleDefinition<?>> modules, StatisticsExportRequest request, byte[] workbookBytes,
                          List<PdfAttachment> attachments, OutputStream outputStream) throws IOException {
        Map<String, Integer> entryNames = new HashMap<>();
        List<String> reportLines = new ArrayList<>();
        int successCount = 0;
        int missingCount = 0;

        try (ZipOutputStream zip = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            putZipEntry(zip, buildXlsxName(title, request), workbookBytes);
            for (ModuleDefinition<?> module : modules) {
                if (module.hasPdfColumn()) {
                    zip.putNextEntry(new ZipEntry("PDF附件/" + safeFileName(module.sheetName) + "/"));
                    zip.closeEntry();
                }
            }

            for (PdfAttachment attachment : attachments) {
                File file = resolvePdfFile(attachment.pdfUrl);
                String entryName = uniqueEntryName(
                        "PDF附件/" + safeFileName(attachment.moduleName) + "/" +
                                safeFileName(attachment.deptName) + "-" +
                                safeFileName(attachment.userName) + "-" +
                                safeFileName(attachment.originalFileName),
                        entryNames);
                if (file != null && file.isFile()) {
                    zip.putNextEntry(new ZipEntry(entryName));
                    Files.copy(file.toPath(), zip);
                    zip.closeEntry();
                    successCount++;
                    reportLines.add("成功: " + entryName);
                } else {
                    missingCount++;
                    reportLines.add("缺失: " + attachment.moduleName + " / " + attachment.deptName + " / " +
                            attachment.userName + " / " + attachment.pdfUrl);
                }
            }

            String report = "PDF下载结果\n成功: " + successCount + "\n缺失: " + missingCount + "\n\n" +
                    String.join("\n", reportLines);
            putZipEntry(zip, "PDF下载结果.txt", report.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void putZipEntry(ZipOutputStream zip, String entryName, byte[] bytes) throws IOException {
        zip.putNextEntry(new ZipEntry(entryName));
        zip.write(bytes);
        zip.closeEntry();
    }

    private String buildXlsxName(String title, StatisticsExportRequest request) {
        String year = request != null && StringUtils.isNotBlank(request.getYear()) ? request.getYear().trim() : "全部年份";
        return title + "_" + year + ".xlsx";
    }

    private void collectPdfAttachment(String moduleName, Object data, String deptName, List<PdfAttachment> attachments) {
        String pdfUrl = formatValue(readProperty(data, "pdfUrl"));
        if (StringUtils.isBlank(pdfUrl)) {
            return;
        }
        String userName = formatValue(readProperty(data, "userName"));
        String originalFileName = extractFileName(pdfUrl);
        attachments.add(new PdfAttachment(moduleName, StringUtils.isBlank(deptName) ? "未知部门" : deptName,
                StringUtils.isBlank(userName) ? "未知姓名" : userName, originalFileName, pdfUrl));
    }

    private File resolvePdfFile(String pdfUrl) {
        if (StringUtils.isBlank(pdfUrl)) {
            return null;
        }
        String decodedUrl = decode(pdfUrl);
        int sourceIndex = decodedUrl.indexOf(PDF_URL_SOURCE_MARK);
        List<File> candidates = new ArrayList<>();
        if (sourceIndex >= 0) {
            String fileName = decodedUrl.substring(sourceIndex + PDF_URL_SOURCE_MARK.length());
            candidates.add(new File(PDF_UPLOAD_PATH, fileName));
            candidates.add(new File(PDF_UPLOAD_PATH + fileName));
            candidates.add(new File("C:/Users/jimmy/Desktop/pdfupload/test", fileName));
            if (RuoYiConfig.getProfile() != null) {
                candidates.add(new File(RuoYiConfig.getProfile(), "source/" + fileName));
                candidates.add(new File(RuoYiConfig.getProfile(), "pdfupload/test/" + fileName));
                candidates.add(new File(RuoYiConfig.getProfile() + "/profile/source/" + fileName));
            }
            return firstExistingFile(candidates);
        }
        if (decodedUrl.contains("/profile/") && RuoYiConfig.getProfile() != null) {
            String relative = decodedUrl.substring(decodedUrl.indexOf("/profile/") + "/profile".length());
            candidates.add(new File(RuoYiConfig.getProfile() + "/profile" + relative));
            candidates.add(new File(RuoYiConfig.getProfile() + relative));
            return firstExistingFile(candidates);
        }
        File direct = new File(decodedUrl);
        if (direct.exists()) {
            return direct;
        }
        String fileName = extractFileName(decodedUrl);
        candidates.add(new File(PDF_UPLOAD_PATH, fileName));
        candidates.add(new File(PDF_UPLOAD_PATH + fileName));
        candidates.add(new File("C:/Users/jimmy/Desktop/pdfupload/test", fileName));
        if (RuoYiConfig.getProfile() != null) {
            candidates.add(new File(RuoYiConfig.getProfile(), fileName));
            candidates.add(new File(RuoYiConfig.getProfile(), "source/" + fileName));
            candidates.add(new File(RuoYiConfig.getProfile(), "profile/" + fileName));
        }
        return firstExistingFile(candidates);
    }

    private File firstExistingFile(List<File> candidates) {
        for (File candidate : candidates) {
            if (candidate != null && candidate.isFile()) {
                return candidate;
            }
        }
        return candidates.isEmpty() ? null : candidates.get(0);
    }

    private String extractFileName(String pdfUrl) {
        String decodedUrl = decode(pdfUrl);
        int queryIndex = decodedUrl.indexOf('?');
        if (queryIndex >= 0) {
            decodedUrl = decodedUrl.substring(0, queryIndex);
        }
        int slashIndex = Math.max(decodedUrl.lastIndexOf('/'), decodedUrl.lastIndexOf('\\'));
        String name = slashIndex >= 0 ? decodedUrl.substring(slashIndex + 1) : decodedUrl;
        return StringUtils.isBlank(name) ? "证明材料.pdf" : name;
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (Exception ignored) {
            return value;
        }
    }

    private String safeFileName(String value) {
        String safe = StringUtils.isBlank(value) ? "未命名" : value.trim();
        safe = safe.replaceAll("[\\\\/:*?\"<>|]", "_");
        return safe.length() > 120 ? safe.substring(0, 120) : safe;
    }

    private String uniqueEntryName(String entryName, Map<String, Integer> entryNames) {
        Integer count = entryNames.get(entryName);
        if (count == null) {
            entryNames.put(entryName, 1);
            return entryName;
        }
        entryNames.put(entryName, count + 1);
        int dotIndex = entryName.lastIndexOf('.');
        if (dotIndex > entryName.lastIndexOf('/')) {
            return entryName.substring(0, dotIndex) + "(" + (count + 1) + ")" + entryName.substring(dotIndex);
        }
        return entryName + "(" + (count + 1) + ")";
    }

    private <T> List<T> queryRows(IService<T> service, StatisticsExportRequest request, List<String> userIds) {
        if (userIds != null && userIds.isEmpty()) {
            return Collections.emptyList();
        }

        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(request.getYear())) {
            wrapper.eq("year", request.getYear().trim());
        }
        if (userIds != null) {
            wrapper.in("user_id", userIds);
        }
        wrapper.eq("status", "已通过");
        wrapper.orderByAsc("user_id").orderByAsc("create_time");
        return service.list(wrapper);
    }

    private List<String> resolveUserIds(StatisticsExportRequest request) {
        if (SCOPE_YEAR_ALL.equalsIgnoreCase(request.getScope())) {
            return null;
        }

        String userId = request.getUserId();
        if ("all".equals(userId)) {
            List<BasicInformation> users = request.getDeptId() == null
                    ? basicInformationService.list()
                    : basicInformationService.listByDept(request.getDeptId());
            if (users == null) {
                return Collections.emptyList();
            }
            return users.stream().map(BasicInformation::getUserId).collect(Collectors.toList());
        }

        if (StringUtils.isBlank(userId)) {
            userId = SecurityUtils.getUsername();
        }
        return Arrays.asList(userId);
    }

    private Map<String, BasicInformation> buildUserMap() {
        Map<String, BasicInformation> userMap = new HashMap<>();
        List<BasicInformation> users = basicInformationService.list();
        if (users != null) {
            for (BasicInformation user : users) {
                userMap.put(user.getUserId(), user);
            }
        }
        return userMap;
    }

    private String getDeptName(Object data, Map<String, BasicInformation> userMap) {
        Object userId = readProperty(data, "userId");
        if (userId == null) {
            return "";
        }
        BasicInformation user = userMap.get(String.valueOf(userId));
        return user == null || user.getDepartment() == null ? "" : user.getDepartment();
    }

    private Object readProperty(Object data, String property) {
        if (data == null || StringUtils.isBlank(property)) {
            return null;
        }
        try {
            Method method = data.getClass().getMethod("get" + Character.toUpperCase(property.charAt(0)) + property.substring(1));
            return method.invoke(data);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DATE_FORMATTER);
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DATE_TIME_FORMATTER);
        }
        return String.valueOf(value);
    }

    private void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private CellStyle buildHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle buildTextStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private List<ModuleDefinition<?>> buildResearchModules() {
        List<ModuleDefinition<?>> modules = new ArrayList<>();
        modules.add(new ModuleDefinition<ResearchSubject>("课题", subjectService, columns(
                column("姓名", "userName"), column("项目名称", "projectName"), column("课题类型", "subjectType"),
                column("当年到位经费", "money"), column("执行开始时间", "executeTimeStart"), column("执行结束时间", "executeTimeEnd"),
                column("年份", "year"), column("分配比例", "rank"), column("系数", "coefficient"),
                column("PDF链接", "pdfUrl"), column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<ResearchPaper>("论文", paperService, columns(
                column("姓名", "userName"), column("论文名称", "title"), column("出版刊物", "journal"),
                column("出版时间", "publishTime"), column("论文级别", "level"), column("年份", "year"),
                column("分配比例", "rank"), column("系数", "coefficient"), column("PDF链接", "pdfUrl"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<ResearchMonograph>("论著", monographService, columns(
                column("姓名", "userName"), column("论著名称", "title"), column("出版社", "publisher"),
                column("出版时间", "publishTime"), column("论著类型", "monographType"), column("年份", "year"),
                column("分配比例", "rank"), column("系数", "coefficient"), column("PDF链接", "pdfUrl"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<ResearchAward>("获奖", awardService, columns(
                column("姓名", "userName"), column("获奖名称", "name"), column("颁奖单位", "organizer"),
                column("获奖时间", "awardTime"), column("奖励级别", "level"), column("年份", "year"),
                column("分配比例", "rank"), column("系数", "coefficient"), column("PDF链接", "pdfUrl"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<ResearchPatent>("专利", patentService, columns(
                column("姓名", "userName"), column("专利名称", "name"), column("专利类型", "type"),
                column("申请时间", "applyTime"), column("授权时间", "authorizeTime"), column("年份", "year"),
                column("分配比例", "rank"), column("系数", "coefficient"), column("PDF链接", "pdfUrl"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<ResearchSoftware>("软著", softwareService, columns(
                column("姓名", "userName"), column("软著名称", "name"), column("申请时间", "applyTime"),
                column("授权时间", "authorizeTime"), column("年份", "year"), column("分配比例", "rank"),
                column("PDF链接", "pdfUrl"), column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        return modules;
    }

    private List<ModuleDefinition<?>> buildTeachingModules() {
        List<ModuleDefinition<?>> modules = new ArrayList<>();
        modules.add(new ModuleDefinition<UndergraduateTheoryCourse>("本科理论课", theoryCourseService, courseColumns()));
        modules.add(new ModuleDefinition<ExperimentCourse>("本科实验课", experimentCourseService, courseColumns()));
        modules.add(new ModuleDefinition<PracticeCourse>("实践教学", practiceCourseService, columns(
                column("姓名", "userName"), column("课程名称", "courseName"), column("课程类型", "courseType"),
                column("计划天数", "planDays"), column("班级数", "classCount"), column("年份", "year"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<ThesisCourse>("毕业论文", thesisCourseService, columns(
                column("姓名", "userName"), column("毕业论文数量", "thesisCount"), column("指导人数", "studentCount"),
                column("是否通过抽检", "isPassInspection"), column("年份", "year"), column("工作量", "workload"),
                column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<ScienceInnovation>("科技创新", scienceInnovationService, columns(
                column("姓名", "userName"), column("项目名称", "projectName"), column("结题年份", "endYear"),
                column("项目级别", "level"), column("年份", "year"), column("PDF链接", "pdfUrl"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<Competition>("学科竞赛", competitionService, columns(
                column("姓名", "userName"), column("获奖项目名称", "awardProjectName"), column("赛事等级", "competitionLevel"),
                column("获奖级别", "awardLevel"), column("年份", "year"), column("PDF链接", "pdfUrl"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<Textbook>("出版教材", textbookService, columns(
                column("姓名", "userName"), column("教材名称", "textbookName"), column("出版社名称", "pressName"),
                column("出版时间", "publishDate"), column("教材级别", "level"), column("编写身份", "compilerIdentity"),
                column("年份", "year"), column("PDF链接", "pdfUrl"), column("工作量", "workload"),
                column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<EducationReform>("教改项目", educationReformService, columns(
                column("姓名", "userName"), column("项目名称", "projectName"), column("结题时间", "endDate"),
                column("项目级别", "level"), column("年份", "year"), column("PDF链接", "pdfUrl"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<EducationReformPaper>("教改论文", educationReformPaperService, columns(
                column("姓名", "userName"), column("论文名称", "paperName"), column("发表日期", "publishDate"),
                column("论文级别", "level"), column("年份", "year"), column("PDF链接", "pdfUrl"),
                column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<Proctor>("监考", proctorService, columns(
                column("姓名", "userName"), column("监考类型", "examType"), column("监考次数", "examCount"),
                column("年份", "year"), column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<GraduateTheoryCourse>("研究生理论课", graduateTheoryCourseService, columns(
                column("姓名", "userName"), column("课程名称", "courseName"), column("授课学生", "studentClass"),
                column("课程类型", "courseType"), column("是否实验课", "isExperimentalCourse"), column("理论课时", "theoryHours"),
                column("学生人数", "studentCount"), column("年份", "year"), column("工作量", "workload"),
                column("审核状态", "status"), column("审核意见", "remark"))));
        modules.add(new ModuleDefinition<GraduateGuideStudent>("研究生指导学生", graduateGuideStudentService, columns(
                column("姓名", "userName"), column("学生数", "studentCount"), column("学生类型", "studentType"),
                column("系数", "coefficient"), column("年份", "year"), column("工作量", "workload"),
                column("审核状态", "status"), column("审核意见", "remark"))));
        return modules;
    }

    private List<ColumnDefinition> courseColumns() {
        return columns(
                column("姓名", "userName"), column("课程名称", "courseName"), column("授课学生", "studentClass"),
                column("课程类型", "courseType"), column("理论课时", "theoryHours"), column("学生人数", "studentCount"),
                column("年份", "year"), column("工作量", "workload"), column("审核状态", "status"), column("审核意见", "remark"));
    }

    private List<ColumnDefinition> columns(ColumnDefinition... columns) {
        return Arrays.asList(columns);
    }

    private ColumnDefinition column(String header, String property) {
        return new ColumnDefinition(header, property);
    }

    private static class ModuleDefinition<T> {
        private final String sheetName;
        private final IService<T> service;
        private final List<ColumnDefinition> columns;

        private ModuleDefinition(String sheetName, IService<T> service, List<ColumnDefinition> columns) {
            this.sheetName = sheetName;
            this.service = service;
            this.columns = columns;
        }

        private boolean hasPdfColumn() {
            return columns != null && columns.stream().anyMatch(column -> "pdfUrl".equals(column.property));
        }
    }

    private static class ColumnDefinition {
        private final String header;
        private final String property;

        private ColumnDefinition(String header, String property) {
            this.header = header;
            this.property = property;
        }
    }

    private static class PdfAttachment {
        private final String moduleName;
        private final String deptName;
        private final String userName;
        private final String originalFileName;
        private final String pdfUrl;

        private PdfAttachment(String moduleName, String deptName, String userName, String originalFileName, String pdfUrl) {
            this.moduleName = moduleName;
            this.deptName = deptName;
            this.userName = userName;
            this.originalFileName = originalFileName;
            this.pdfUrl = pdfUrl;
        }
    }
}
