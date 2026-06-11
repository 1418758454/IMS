package com.ruoyi.manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.RegisterInformation;
import com.ruoyi.manage.service.BasicInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 人员信息 Controller
 * 继承 BaseController，复用分页、响应格式化等工具方法
 */
@RestController
@RequestMapping("/manage/information")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST}) // 允许跨域的源和方法
public class BasicInformationController extends BaseController {

    @Autowired
    private BasicInformationService basicInformationService;

    // 审核注册信息
    // 合法注册，暂存到注册表待审核
    @PostMapping(value = "/register/save")
    @Anonymous
    public AjaxResult saveRegister(@RequestBody RegisterInformation registerInfo) {
        System.out.println("注册个人基本信息"+registerInfo);

        registerInfo.setStatus(0);
//        basicInformation.setStatus(1);
        // 检查该工号是否已经提交注册信息，避免重复提交
        Integer isSave = basicInformationService.checkLoginId(registerInfo.getUserId());

        if (isSave == 1) {// 保存注册信息到注册表
            String password = SecurityUtils.encryptPassword(registerInfo.getPassword());
            registerInfo.setPassword(password);
            basicInformationService.saveRegisterInfo(registerInfo);
            return AjaxResult.success();
        }else if(isSave == -1){
            return AjaxResult.error("提交失败，该工号注册信息正在审核中");
        }else {
            return AjaxResult.error("提交失败，该工号已注册");
        }
    }


    // 审核通过保存注册信息到个人信息表  todo 是否没有调用这个函数了
    @PostMapping(value = "/register/pass")
    @Anonymous
    public AjaxResult passRegister(@RequestBody BasicInformation basicInformation) {
        System.out.println("注册个人基本信息"+basicInformation);
        basicInformation.setStatus(1);
        // 检查登录账号是否在职工表中,若存在则检查是否已经注册 审核是否允许注册
        Integer isRegister = basicInformationService.checkLoginId(basicInformation.getUserId());
        if (isRegister == 1) {
            String username = basicInformation.getUserId();
            String password = SecurityUtils.encryptPassword(basicInformation.getPassword());

            // 审核通过，注册人员信息
            basicInformationService.passRegisterInfo(basicInformation, username, password);
            return AjaxResult.success();
        }else if(isRegister == 0){
            return AjaxResult.error("未审核通过，工号不存在");
        }else {
            return AjaxResult.error("未审核通过，工号已注册");
        }

    }



    // 根据登录用户名查询人员基本信息
    @GetMapping(value = "/{id}")
    public AjaxResult getBaseInfo(@PathVariable("id") String id) {
        // 根据id查询人员基本信息
        BasicInformation basicInformation = basicInformationService.getByloginId(id);
        return AjaxResult.success(basicInformation);
    }
}
