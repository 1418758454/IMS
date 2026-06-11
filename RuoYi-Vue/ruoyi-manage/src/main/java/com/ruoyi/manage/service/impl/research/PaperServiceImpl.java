package com.ruoyi.manage.service.impl.research;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchPaper;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.mapper.research.PaperMapper;
import com.ruoyi.manage.mapper.research.ResearchTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, ResearchPaper> implements PaperService {

    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private ResearchTotalWorkloadMapper totalWorkloadMapper;
    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public IPage<ResearchPaper> getPaperPage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit) {
        IPage<ResearchPaper> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ResearchPaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 按用户ID筛选
        // 新增条件：按年份筛选（若year不为空）
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.apply("YEAR(publish_time) = {0}", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }

        return paperMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean addPaper(ResearchPaper paper) {
        paper.setYear(paper.getPublishTime().toString().substring(0,4));
        if(paperMapper.insert(paper) > 0){
            // 计算模块总工作量
            countTotalConfirmedWorkload(paper.getUserId(), paper.getYear());
            countTotalEstimatedWorkload(paper.getUserId(), paper.getYear());
            return true;
        }
        return false;
    }

    @Override
    public boolean removePaperByIds(List<Long> ids) {

        // 1. 查询论文列表，获取PDF文件路径
        List<ResearchPaper> papers = paperMapper.selectBatchIds(ids);
        if (papers.isEmpty()) {
            return false;
        }

        long userId = papers.get(0).getUserId();
        String year = papers.get(0).getYear();

        // 2. 遍历删除服务器PDF文件
        for (ResearchPaper paper : papers) {
            String pdfUrl = paper.getPdfUrl();
            if (StringUtils.isNotEmpty(pdfUrl)) {
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(pdfUrl);
                // 删除服务器文件（若依工具类，自动处理路径和异常）
                System.out.println("删除文件路径：" + filePath);
                FileUtils.deleteFile(filePath); // 若依工具类删除文件
            }
        }

        // 3. 删除数据库记录
        if(paperMapper.deleteBatchIds(ids) > 0){
            // 计算模块总工作量
            countTotalConfirmedWorkload(userId, year);
            countTotalEstimatedWorkload(userId, year);
            return true;
        }
        return false;
    }

    @Override
    public IPage<ResearchPaper> getPaperPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            paperMapper.chageUserName(userId, userName);
        });

        IPage<ResearchPaper> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ResearchPaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.apply("YEAR(publish_time) = {0}", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }
        // 4. 执行分页查询
        paperMapper.selectPage(page, queryWrapper);

        // 5. 返回分页结果（包含当前页数据 records 和总条数 total）
        return page;
    }

    @Override
    public boolean auditPaper(Long id, String status, String remark) {
        ResearchPaper paper = this.getById(id);
        if (paper == null) {
            return false;
        }

        // 更新审核状态和备注
        paper.setStatus(status);
        paper.setRemark(remark);
        paper.setUpdateTime(LocalDateTime.now());

        if(this.updateById(paper)){
            // 计算模块总工作量
            countTotalConfirmedWorkload(paper.getUserId(), paper.getYear());
            countTotalEstimatedWorkload(paper.getUserId(), paper.getYear());
            return true;
        }
        return false;
    }

    @Override
    public void countTotalConfirmedWorkload(Long userId, String year) {
        QueryWrapper<ResearchPaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        // 按年份筛选
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.eq("year", year);
        }

        // 只计算状态为"已通过"的记录
        queryWrapper.eq("status", "已通过");

        // 使用 MyBatis-Plus 的聚合查询计算工作量总和
        List<ResearchPaper> confirmedPapers = paperMapper.selectList(queryWrapper);

        // 计算总工作量
        BigDecimal total = BigDecimal.ZERO;
        for (ResearchPaper paper : confirmedPapers) {
            if (paper.getWorkload() != null) {
                total = total.add(paper.getWorkload());
            }
        }

        // 更新总工作量表
        BasicInformation user = basicInformationService.getById(userId);
        ResearchTotalWorkload totalWorkload = totalWorkloadMapper.selectOne(
                new QueryWrapper<ResearchTotalWorkload>().eq("user_id", userId).eq("year", year)
        );

        if (totalWorkload == null) { //查看总表是否已有记录，没有就创建，有则更新
            totalWorkload = new ResearchTotalWorkload();
            totalWorkload.setUserId(BigDecimal.valueOf(userId));
            totalWorkload.setUserName(user.getName());
            totalWorkload.setYear(year);
            totalWorkload.setPaperConfirmedWorkload(total);
            totalWorkloadMapper.insert(totalWorkload);
        } else {
            totalWorkload.setPaperConfirmedWorkload(total);
            totalWorkloadMapper.update(totalWorkload, new QueryWrapper<ResearchTotalWorkload>()
                    .eq("user_id", userId).eq("year", year));
        }
    }

    @Override
    public void countTotalEstimatedWorkload(Long userId, String year) {
        QueryWrapper<ResearchPaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        // 按年份筛选
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.eq("year", year);
        }

        // 使用 MyBatis-Plus 的聚合查询计算工作量总和
        List<ResearchPaper> allPapers = paperMapper.selectList(queryWrapper);

        // 计算总工作量
        BigDecimal total = BigDecimal.ZERO;
        for (ResearchPaper paper : allPapers) {
            if (paper.getWorkload() != null) {
                total = total.add(paper.getWorkload());
            }
        }

        // 更新总工作量表
        BasicInformation user = basicInformationService.getById(userId);
        ResearchTotalWorkload totalWorkload = totalWorkloadMapper.selectOne(
                new QueryWrapper<ResearchTotalWorkload>().eq("user_id", userId).eq("year", year)
        );

        if (totalWorkload == null) { //查看总表是否已有记录，没有就创建，有则更新
            totalWorkload = new ResearchTotalWorkload();
            totalWorkload.setUserId(BigDecimal.valueOf(userId));
            totalWorkload.setUserName(user.getName());
            totalWorkload.setYear(year);
            totalWorkload.setPaperEstimatedWorkload(total);
            totalWorkloadMapper.insert(totalWorkload);
        } else {
            totalWorkload.setPaperEstimatedWorkload(total);
            totalWorkloadMapper.update(totalWorkload, new QueryWrapper<ResearchTotalWorkload>()
                    .eq("user_id", userId).eq("year", year));
        }
    }
}