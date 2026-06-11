package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 研究生理论课实体类
 * 对应表：teaching_graduate_theory_course
 */
@Data
@TableName("teaching_graduate_theory_course") // 数据库表名（研究生表）
public class GraduateTheoryCourse {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（外键，关联用户表） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 课程名称 */
    @TableField("course_name")
    private String courseName;

    /** 授课学生（班级） */
    @TableField("student_class")
    private String studentClass;

    /** 课程类型（必修课/选修课等） */
    @TableField("course_type")
    private String courseType;

    /** 实验课（是/否） */
    @TableField("is_experimental_course")
    private String isExperimentalCourse;

    /** 理论课时 */
    @TableField("theory_hours")
    private Integer theoryHours;

    /** 学生人数 */
    @TableField("student_count")
    private Integer studentCount;

    /** 工作量 */
    private BigDecimal workload;

    /** 授课年份 */
    private Integer year;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 审核状态 */
    private String status;

    /** 审核意见 */
    private String remark;

}