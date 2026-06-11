package com.ruoyi.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.RegisterInformation;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import com.ruoyi.manage.mapper.RegisterReviewMapper;
import com.ruoyi.manage.mapper.teaching.TeachingTotalWorkloadMapper;
import com.ruoyi.manage.service.RegisterReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注册审核 Service 实现类（独立处理审核流程，不依赖 BasicInformationService）
 */
@Service
public class RegisterReviewServiceImpl implements RegisterReviewService {

    @Autowired
    private RegisterReviewMapper registerReviewMapper; // 独立的审核Mapper
    @Autowired
    private TeachingTotalWorkloadMapper teachingTotalWorkloadMapper;

    @Override
    public IPage<RegisterInformation> getPendingList(Page<RegisterInformation> page, String keyword) {
        QueryWrapper<RegisterInformation> queryWrapper = new QueryWrapper<>();

        // 1. 基础条件：仅查询待审核状态（status=0）
//        queryWrapper.eq("status", 0);


        // 2. 模糊查询条件：若 keyword 不为空，则按工号或姓名模糊匹配
        if (StringUtils.isNotBlank(keyword)) {
            // 拼接条件：user_id LIKE %keyword% OR name LIKE %keyword%
            queryWrapper.like("user_id", keyword) // 工号模糊匹配
                    .or() // 逻辑 OR
                    .like("name", keyword); // 姓名模糊匹配
        }

        // 3. 排序：按提交时间顺序
        queryWrapper.orderByAsc("create_time");

        // 4. 执行分页查询
        return registerReviewMapper.selectPage(page, queryWrapper);
    }

    /**
     * 审核注册信息（通过/拒绝）
     */
    @Override
    @Transactional // 事务保证：通过时迁移数据+删除注册记录，原子性执行
    public void review(Long id, Integer status) {
        // 1. 查询注册信息（校验存在性）
        System.out.println("id: " + id);
        RegisterInformation registerInfo = registerReviewMapper.selectById(id);
        if (registerInfo == null) {
            throw new RuntimeException("注册信息不存在");
        }

        // 2. 审核通过（status=1）：迁移数据到正式表+删除注册记录
        if (status == 1) {
            // 2.1 复制注册信息到正式表实体（BasicInformation）
            BasicInformation basicInfo = new BasicInformation();
            BeanUtils.copyProperties(registerInfo, basicInfo); // 复制 userId、name 等核心字段
            registerReviewMapper.insertBasicInfo(basicInfo); // 插入正式表（basic_information）

            // 2.2 创建用户账号（插入 sys_user 表）
            registerReviewMapper.insertUser(registerInfo.getUserId(), registerInfo.getPassword(),0);
            // 获取若依新建用户的自增id
            Long userId = registerReviewMapper.findId(registerInfo.getUserId());
            // 将新建用户的角色设置为普通角色
            registerReviewMapper.insertUserRole(userId);

            // 2.3 在教学工作量表中新增用户id和用户名
//            teachingTotalWorkloadMapper.addTeachingWorkload(basicInfo.getUserId(), basicInfo.getName());

            // 2.4 删除注册信息表中已通过记录
            registerReviewMapper.deleteById(id);
        }
        // 3. 审核拒绝
        else if (status == 2) {
            registerReviewMapper.deleteById(id); // 直接删除被拒绝的注册信息
        } else {
            throw new RuntimeException("无效审核状态（1=通过，2=拒绝）");
        }
    }

    /**
     * 审核注册信息（修改注册表信息）
     */
    @Override
    public void updataRegisterInfo(RegisterInformation registerInfo) {
        // 1. 获取注册信息（校验存在性）
        RegisterInformation oldRegisterInfo = registerReviewMapper.selectById(registerInfo.getId());
        if (oldRegisterInfo == null) {
            throw new RuntimeException("注册信息不存在");
        }

        // 2. 更新注册信息
        registerReviewMapper.updateById(registerInfo);
    }
}