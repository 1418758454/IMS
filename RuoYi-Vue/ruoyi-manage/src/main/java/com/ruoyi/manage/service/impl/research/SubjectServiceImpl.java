package com.ruoyi.manage.service.impl.research;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchSubject;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.research.ResearchTotalWorkloadMapper;
import com.ruoyi.manage.mapper.research.SubjectMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, ResearchSubject> implements SubjectService{

    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private ResearchTotalWorkloadMapper totalWorkloadMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    // 注入若依配置类，用于获取文件存储根路径
    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public IPage<ResearchSubject> getSubjectPage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit) {
        // 1. 创建分页对象（MyBatis-Plus 分页插件自动处理分页逻辑）
        IPage<ResearchSubject> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件：根据 userId 精确匹配（数据库字段为 user_id）
        QueryWrapper<ResearchSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 核心条件：user_id = #{userId}
        // 3. 新增条件：按年份筛选（若year不为空）
        if (StringUtils.isNotBlank(year)) {
            // 对 `create_time`字段进行年份提取，在进行匹配，{0}为占位符
            // SQL片段：YEAR(create_time) = '2024'（动态拼接年份）
//            queryWrapper.apply("YEAR(execute_time_start) = {0}", year); //获取项目启动年份
            queryWrapper.eq("year", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }

        // 4. 执行分页查询（MyBatis-Plus 自动拼接 SQL：SELECT * FROM research_subject WHERE user_id = ? LIMIT ?, ?）
        subjectMapper.selectPage(page, queryWrapper);

        // 5. 返回分页结果（包含当前页数据 records 和总条数 total）
        return page;

    }

    @Override
    public IPage<ResearchSubject> getSubjectPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            subjectMapper.chageUserName(userId, userName);
        });

        IPage<ResearchSubject> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ResearchSubject> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.eq("year", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }
        // 4. 执行分页查询
        subjectMapper.selectPage(page, queryWrapper);

        // 5. 返回分页结果（包含当前页数据 records 和总条数 total）
        return page;
    }

    @Override
    public boolean auditSubject(Long id, String status, String remark) {
        ResearchSubject subject = this.getById(id);
        if (subject == null) {
            return false;
        }

        // 更新审核状态和备注
        subject.setStatus(status);
        subject.setRemark(remark);
//        subject.setAuditor(auditor);
//        subject.setAuditTime(LocalDateTime.now());
        subject.setUpdateTime(LocalDateTime.now());

        if(this.updateById(subject)){
            // 计算模块总工作量
            return true;
        }
        return false;
//        return this.updateById(subject);
    }

    @Override
    public boolean addSubject(ResearchSubject subject) {
        // 新增课题，新增成功则更新总工作量表
        if(subjectMapper.insert(subject) > 0){
            // 计算模块总工作量
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubjectByIds(List<Long> ids) {
        // 删除课题
        // 1. 根据ID列表查询所有课题信息（获取pdfUrl）
        List<ResearchSubject> subjects = subjectMapper.selectBatchIds(ids);
        if (subjects.isEmpty()) {
            return false; // 无课题数据，直接返回
        }

        long userId = subjects.get(0).getUserId();
        String year = subjects.get(0).getYear();

        // 2. 遍历课题，删除对应的PDF文件
        for (ResearchSubject subject : subjects) {
            String pdfUrl = subject.getPdfUrl();
            if (StringUtils.isNotEmpty(pdfUrl)) {
                // 拼接完整文件路径（参考若依框架删除头像逻辑）
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(pdfUrl);
                // 删除服务器文件（若依工具类，自动处理路径和异常）
                System.out.println("删除文件路径：" + filePath);
                FileUtils.deleteFile(filePath);
            }
        }

        // 3. 删除数据库中的课题记录
        if(subjectMapper.deleteBatchIds(ids) > 0){
            // 计算模块总工作量
            return true;
        }
        return false;
//        return subjectMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public boolean updateSubject(ResearchSubject subject) {
        if(subjectMapper.updateById(subject) > 0){
            // 计算模块总工作量
            return true;
        }
        return false;
//        return subjectMapper.updateById(subject) > 0;
    }





}
