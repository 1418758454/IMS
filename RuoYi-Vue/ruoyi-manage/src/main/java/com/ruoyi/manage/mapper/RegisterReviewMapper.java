package com.ruoyi.manage.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.RegisterInformation;
import org.apache.ibatis.annotations.*;

/**
 * 注册审核 Mapper 接口（仅处理审核相关数据库操作）
 */
@Mapper
public interface RegisterReviewMapper extends BaseMapper<RegisterInformation> { // 继承 BaseMapper

    /**
     * 1. 分页查询待审核列表（修复 SQL 实现）
     */
    @Select("SELECT * FROM user_registration ${ew.customSqlSegment}") // 动态条件
    IPage<RegisterInformation> selectPendingList(
            Page<RegisterInformation> page,
            @Param(Constants.WRAPPER) Wrapper<RegisterInformation> queryWrapper
    );


    /**
     * 2. 创建若依用户账号（动态状态参数），同时将用户角色设置为普通用户
     */
    // 创建若依用户
    @Insert("INSERT INTO sys_user (user_name, password, status) VALUES (#{userId}, #{password}, #{status})")
    void insertUser(
            @Param("userId") String userId,
            @Param("password") String password,
            @Param("status") Integer status // 动态传入状态
    );
    //获取若依用户ID
    @Select("SELECT user_id FROM sys_user WHERE user_name = #{userId}")
    Long findId(String userId);
    // 设置新用户的若依角色
    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{userId}, 2)")
    void insertUserRole(Long userId);


    /**
     * 3. 审核通过：插入正式表（basic_information）
     */
    @Insert("INSERT INTO basic_information (user_id, name, gender, birth_date, native_place, political_status, title, current_position, undergrad_school, undergrad_time, highest_education, highest_education_time, overseas_experience, research_direction, phone, email, wechat, qq, personal_website, status, department, position) VALUES (#{userId}, #{name}, #{gender}, #{birthDate}, #{nativePlace}, #{politicalStatus}, #{title}, #{currentPosition}, #{undergradSchool}, #{undergradTime}, #{highestEducation}, #{highestEducationTime}, #{overseasExperience}, #{researchDirection}, #{phone}, #{email}, #{wechat}, #{qq}, #{personalWebsite}, 1, #{department}, #{position})")
    void insertBasicInfo(BasicInformation basicInfo);

}