package com.ruoyi.manage.service.impl.research;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.research.ResearchMonograph;
import com.ruoyi.manage.domain.research.ResearchTotalWorkload;
import com.ruoyi.manage.mapper.research.MonographMapper;
import com.ruoyi.manage.mapper.research.ResearchTotalWorkloadMapper;
import com.ruoyi.manage.service.BasicInformationService;
import com.ruoyi.manage.service.research.MonographService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonographServiceImpl extends ServiceImpl<MonographMapper, ResearchMonograph> implements MonographService {

    @Autowired
    private BasicInformationService basicInformationService;
    @Autowired
    private ResearchTotalWorkloadMapper totalWorkloadMapper;
    @Autowired
    private MonographMapper monographMapper;

    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Override
    public IPage<ResearchMonograph> getMonographPage(Integer pageNum, Integer pageSize, String userId, String year, Boolean isAudit) {
        IPage<ResearchMonograph> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ResearchMonograph> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 按用户ID筛选
        // 新增条件：按年份筛选（若year不为空）
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.apply("YEAR(publish_time) = {0}", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }

        return monographMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean addMonograph(ResearchMonograph monograph) {
        monograph.setYear(monograph.getPublishTime().toString().substring(0,4));
        if(monographMapper.insert(monograph) > 0){
            // 计算模块总工作量
            countTotalConfirmedWorkload(monograph.getUserId(), monograph.getYear());
            countTotalEstimatedWorkload(monograph.getUserId(), monograph.getYear());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeMonographByIds(List<Long> ids) {

        // 1. 查询论著列表
        List<ResearchMonograph> monographs = monographMapper.selectBatchIds(ids);
        if (monographs.isEmpty()) {
            return false;
        }
        long userId = monographs.get(0).getUserId();
        String year = monographs.get(0).getYear();

        // 2. 删除PDF文件
        for (ResearchMonograph monograph : monographs) {
            String pdfUrl = monograph.getPdfUrl();
            if (StringUtils.isNotEmpty(pdfUrl)) {
                String filePath = ruoYiConfig.getProfile() + "/profile" + FileUtils.stripPrefix(pdfUrl);
                // 删除服务器文件（若依工具类，自动处理路径和异常）
                System.out.println("删除文件路径：" + filePath);
                FileUtils.deleteFile(filePath);
            }
        }

        // 3. 删除数据库记录
        if(monographMapper.deleteBatchIds(ids) > 0){
            // 计算模块总工作量
            countTotalConfirmedWorkload(userId, year);
            countTotalEstimatedWorkload(userId, year);
            return true;
        }
        return false;
    }

    @Override
    public IPage<ResearchMonograph> getMonographPageByUserIds(Integer pageNum, Integer pageSize, List<String> userIds, String year, Boolean isAudit) {
        // 查询数据库中所有id为userId的记录，将其用户名改为userName
        userIds.forEach(userId -> {
            String userName = basicInformationService.getById(userId).getName();
            monographMapper.chageUserName(userId, userName);
        });

        IPage<ResearchMonograph> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ResearchMonograph> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.apply("YEAR(publish_time) = {0}", year);
        }
        if(isAudit){ //审核页面无需显示已通过的课题
            queryWrapper.ne("status", "已通过");
            System.out.println("审核页面无需显示已通过的课题");
        }
        // 4. 执行分页查询
        monographMapper.selectPage(page, queryWrapper);

        // 5. 返回分页结果（包含当前页数据 records 和总条数 total）
        return page;
    }

    @Override
    public boolean auditMonograph(Long id, String status, String remark) {
        ResearchMonograph monograph = this.getById(id);
        if (monograph == null) {
            return false;
        }

        // 更新审核状态和备注
        monograph.setStatus(status);
        monograph.setRemark(remark);
        monograph.setUpdateTime(LocalDateTime.now());

        if(this.updateById(monograph)){
            // 计算模块总工作量
            countTotalConfirmedWorkload(monograph.getUserId(), monograph.getYear());
            countTotalEstimatedWorkload(monograph.getUserId(), monograph.getYear());
            return true;
        }
        return false;
    }

    @Override
    public void countTotalConfirmedWorkload(Long userId, String year) {
        QueryWrapper<ResearchMonograph> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        // 按年份筛选
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.eq("year", year);
        }

        // 只计算状态为"已通过"的记录
        queryWrapper.eq("status", "已通过");

        // 使用 MyBatis-Plus 的聚合查询计算工作量总和
        List<ResearchMonograph> confirmedMonographs = monographMapper.selectList(queryWrapper);

        // 计算总工作量
        BigDecimal total = BigDecimal.ZERO;
        for (ResearchMonograph monograph : confirmedMonographs) {
            if (monograph.getWorkload() != null) {
                total = total.add(monograph.getWorkload());
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
            totalWorkload.setMonographConfirmedWorkload(total);
            totalWorkloadMapper.insert(totalWorkload);
        } else {
            totalWorkload.setMonographConfirmedWorkload(total);
            totalWorkloadMapper.update(totalWorkload, new QueryWrapper<ResearchTotalWorkload>()
                    .eq("user_id", userId).eq("year", year));
        }
    }

    @Override
    public void countTotalEstimatedWorkload(Long userId, String year) {
        QueryWrapper<ResearchMonograph> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        // 按年份筛选
        if (StringUtils.isNotBlank(year)) {
            queryWrapper.eq("year", year);
        }

        // 使用 MyBatis-Plus 的聚合查询计算工作量总和
        List<ResearchMonograph> allMonographs = monographMapper.selectList(queryWrapper);

        // 计算总工作量
        BigDecimal total = BigDecimal.ZERO;
        for (ResearchMonograph monograph : allMonographs) {
            if (monograph.getWorkload() != null) {
                total = total.add(monograph.getWorkload());
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
            totalWorkload.setMonographEstimatedWorkload(total);
            totalWorkloadMapper.insert(totalWorkload);
        } else {
            totalWorkload.setMonographEstimatedWorkload(total);
            totalWorkloadMapper.update(totalWorkload, new QueryWrapper<ResearchTotalWorkload>()
                    .eq("user_id", userId).eq("year", year));
        }
    }
}