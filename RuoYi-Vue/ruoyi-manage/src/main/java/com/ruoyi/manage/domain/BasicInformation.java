package com.ruoyi.manage.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员基本信息实体类（若依框架规范版）
 * 表名：basic_information（与数据库表名一致）
 */
@Data
@TableName("basic_information")  // MyBatis-Plus 表名映射（必须）
public class BasicInformation {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID（若依框架默认使用自增策略）
     * 注：BaseEntity 已包含 id 字段，无需重复定义，直接继承即可
     * 若需修改主键策略，可在此处重写 @TableId 注解（如：@TableId(type = IdType.ASSIGN_UUID)）
     */

    /**
     * 工号（唯一字段）
     */
    @TableId(type = IdType.INPUT)
    @TableField("user_Id")
    private String userId;

    /**
     * 姓名（非空字段）
     */
    @NotBlank(message = "姓名不能为空")  // 字符串非空校验（推荐）
    @TableField("name")
    private String name;

    /**
     * 性别（男/女/未知）
     * 若依框架建议：使用字典管理（如 "0"=男, "1"=女, "2"=未知），需配合 @Dict 注解（需引入若依字典依赖）
     */
    @TableField("gender")
    private String gender;

    /**
     * 出生年月（格式：yyyy-MM-dd）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")  // 前端JSON序列化格式（若依前端默认支持）
    @TableField("birth_date")  // 数据库字段名映射（下划线转驼峰）
    private LocalDate birthDate;


    /**
     * 籍贯（如：北京/上海）
     */
    @TableField("native_place")
    private String nativePlace;

    /**
     * 政治面貌（如：中共党员/群众）
     */
    @TableField("political_status")
    private String politicalStatus;

    /**
     * 职称
     */
    @TableField("department")
    private String department;

    /**
     * 部门
     */
    @TableField("position")
    private String position;

    /**
     * 职务
     */
    @TableField("title")
    private String title;

    /**
     * 现有岗位
     */
    @TableField("current_position")
    private String currentPosition;

    /**
     * 本科毕业学校
     */
    @TableField("undergrad_school")
    private String undergradSchool;

    /**
     * 本科毕业时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("undergrad_time")
    private LocalDate undergradTime;

    /**
     * 硕士毕业学校
     */
    @TableField("master_school")
    private String masterSchool;

    /**
     * 硕士毕业时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "master_time", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate masterTime;

    /**
     * 博士毕业学校
     */
    @TableField("doctor_school")
    private String doctorSchool;

    /**
     * 博士毕业时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "doctor_time", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate doctorTime;

    /**
     * 博士后经历
     */
    @Length(max = 500, message = "博士后经历不能超过500字符")
    @TableField("postdoctoral_experience")
    private String postdoctoralExperience;

    /**
     * 最高学历
     */
    @TableField("highest_education")
    private String highestEducation;

    /**
     * 最高学历获得时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("highest_education_time")
    private LocalDate highestEducationTime;

    /**
     * 出国访学经历
     */
    @Length(max = 500, message = "出国访学经历不能超过500字符")
    @TableField("overseas_experience")
    private String overseasExperience;

    /**
     * 主要研究方向
     */
    @Length(max = 500, message = "研究方向不能超过500字符")
    @TableField("research_direction")
    private String researchDirection;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")  // 邮箱格式校验
    @TableField("email")
    private String email;

    /**
     * 微信
     */
    @TableField("wechat")
    private String wechat;

    /**
     * QQ
     */
    @TableField("qq")
    private String qq;

    /**
     * 个人网站
     */
    @TableField("personal_website")
    private String personalWebsite;

    /**
     * 状态（1=正常，2=禁用，3=待审核）
     */
    @TableField("status")
    private Integer status;

    /**
     * 用户密码
     */
//    @TableField("password")
    @TableField(updateStrategy = FieldStrategy.NEVER,select = false)  // 密码字段不更新到基本信息表
    private String password;


}
