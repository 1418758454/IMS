package com.ruoyi.manage.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.RegisterInformation;
import com.ruoyi.manage.mapper.BasicInformationMapper;
import com.ruoyi.manage.service.BasicInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BasicInformationServiceImpl extends ServiceImpl<BasicInformationMapper, BasicInformation> implements BasicInformationService {

    @Autowired
    private BasicInformationMapper basicInformationMapper;


    @Override
    public Integer checkLoginId(String userId) {
        if(baseMapper.selectCount(new QueryWrapper<BasicInformation>().eq("user_id", userId).eq("status", 1)) > 0){
            // 如果信息表中该工号状态为1，则返回0，表示该工号已注册
            return 0;
        }else if (basicInformationMapper.checkStatus(userId) > 0) {
            // 如果注册表中已经有该工号，则返回-1，表示该工号正在审核中
            return -1;
        }else{
            return 1;
        }
    }
    // 注册审核
//    @Override
//    public Integer checkLoginId(String userId) {
//        // 审核工号
//        if (baseMapper.selectCount(new QueryWrapper<BasicInformation>().eq("user_id", userId)) > 0) {
//            if (baseMapper.selectCount(new QueryWrapper<BasicInformation>().eq("user_id", userId).eq("status", 1)) > 0) {
//                return -1;  //返回已被注册
//            } else {
//                return 1;  // 返回审核通过
//            }
//        }else {
//            return 0;  //返回注册表中没有该工号
//        }
//    }


    // 保存注册信息到注册表
    @Override
    public void saveRegisterInfo(RegisterInformation registerInfo) {
        // 新增注册信息
        basicInformationMapper.insertRegisterinfo(registerInfo);
    }

    // 修改个人基本信息
    @Override
    public void updateBasicInfo(BasicInformation basicInformation, String username) {
        baseMapper.update(basicInformation, new QueryWrapper<BasicInformation>().eq("user_id", username));
    }

    // 根据部门id查询部门下所有员工
    @Override
    public List<BasicInformation> listByDept(Long deptId) {
        // 根据部门id查出所有满足条件的员工
        // 如果id为1，则查”数学信息“，如果为2，则查”计算机信息“
        if (deptId == 0) {
            // 当id为0时，没有限制条件，返回所有员工
            System.out.println("无部门限制条件");
            return baseMapper.selectList(new QueryWrapper<BasicInformation>());
        }else if (deptId == 1) {
            return baseMapper.selectList(new QueryWrapper<BasicInformation>().eq("department", "应用数学"));
        }else if (deptId == 2) {
            return baseMapper.selectList(new QueryWrapper<BasicInformation>().eq("department", "信息与计算科学"));
        }else if (deptId == 3) {
            return baseMapper.selectList(new QueryWrapper<BasicInformation>().eq("department", "统计"));
        }else if (deptId == 4) {
            return baseMapper.selectList(new QueryWrapper<BasicInformation>().eq("department", "大学教学部"));
        }else{
            return null;
        }
    }


    // 将注册信息写入信息表
    @Override
    public void passRegisterInfo(BasicInformation basicInformation, String username, String password) {

        // 注册对应工号的基本信息
        baseMapper.update(basicInformation, new QueryWrapper<BasicInformation>().eq("user_id", basicInformation.getUserId()));
        basicInformationMapper.insertUser(username, password);
    }



    // 根据工号查询个人基本信息
    @Override
    public BasicInformation getByloginId(String id) {
        return baseMapper.selectOne(new QueryWrapper<BasicInformation>().eq("user_id", id));
    }

}
