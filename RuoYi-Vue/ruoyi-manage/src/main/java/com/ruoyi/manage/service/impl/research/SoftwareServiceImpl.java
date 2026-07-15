package com.ruoyi.manage.service.impl.research;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchSoftware;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.mapper.research.ResearchTotalWorkloadMapper;
import com.ruoyi.manage.mapper.research.SoftwareMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.SoftwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SoftwareServiceImpl extends ServiceImpl<SoftwareMapper, ResearchSoftware> implements SoftwareService {

    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private ResearchTotalWorkloadMapper totalWorkloadMapper;
    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public IPage<ResearchSoftware> getSoftwarePage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit) {
        IPage<ResearchSoftware> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ResearchSoftware> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 按用户ID筛选
        // 新增条件：按年份筛选（若year不为空）
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.apply("YEAR(authorize_time) = {0}", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }

        return softwareMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean addSoftware(ResearchSoftware software) {
        software.setYear(software.getAuthorizeTime().toString().substring(0,4));
        if(softwareMapper.insert(software) > 0){
            // 计算模块总工作量
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSoftwareByIds(List<Long> ids) {
        // 1. 查询软著列表
        List<ResearchSoftware> softwares = softwareMapper.selectBatchIds(ids);
        if (softwares.isEmpty()) {
            return false;
        }

        long userId = softwares.get(0).getUserId();
        String year = softwares.get(0).getYear();

        // 2. 删除PDF文件
        for (ResearchSoftware software : softwares) {
            String pdfUrl = software.getPdfUrl();
            if (StringUtils.isNotEmpty(pdfUrl)) {
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(pdfUrl);
                // 删除服务器文件（若依工具类，自动处理路径和异常）
                System.out.println("删除文件路径：" + filePath);
                FileUtils.deleteFile(filePath);
            }
        }

        // 3. 删除数据库记录
        if(softwareMapper.deleteBatchIds(ids) > 0){
            // 计算模块总工作量
            return true;
        }
        return false;
    }

    @Override
    public IPage<ResearchSoftware> getSoftwarePageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            softwareMapper.chageUserName(userId, userName);
        });

        IPage<ResearchSoftware> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ResearchSoftware> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.apply("YEAR(authorize_time) = {0}", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }
        // 4. 执行分页查询
        softwareMapper.selectPage(page, queryWrapper);

        // 5. 返回分页结果（包含当前页数据 records 和总条数 total）
        return page;
    }

    @Override
    public boolean auditSoftware(Long id, String status, String remark) {
        ResearchSoftware software = this.getById(id);
        if (software == null) {
            return false;
        }

        // 更新审核状态和备注
        software.setStatus(status);
        software.setRemark(remark);
        software.setUpdateTime(LocalDateTime.now());

        if(this.updateById(software)){
            // 计算模块总工作量
            return true;
        }
        return false;
    }




}