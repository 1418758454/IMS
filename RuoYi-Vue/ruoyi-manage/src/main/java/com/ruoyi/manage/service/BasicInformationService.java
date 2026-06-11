package com.ruoyi.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.RegisterInformation;

import java.util.List;

public interface BasicInformationService extends IService<BasicInformation> {


    BasicInformation getByloginId(String id);

    Integer checkLoginId(String userId);

    void passRegisterInfo(BasicInformation basicInformation, String username, String password);

    void saveRegisterInfo(RegisterInformation registerInfo);

    void updateBasicInfo(BasicInformation basicInformation, String username);

    List<BasicInformation> listByDept(Long deptId);
}
