package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 本科实践教学实体类
 * 对应表：teaching_undergraduate_practice_course
 */
@Data
@TableName("teaching_undergraduate_practice_course") // 数据库表名（与SQL一致）
public class PracticeCourse {

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

    /** 课程类型（实习/实训/课程设计） */
    @TableField("course_type")
    private String courseType;

    /** 计划天数（对应实验课表的“实验学时”） */
    @TableField("plan_days")
    private Integer planDays;

    /** 班级数（对应实验课表的“学生人数”） */
    @TableField("class_count")
    private Integer classCount;

    /** 工作量 */
    private BigDecimal workload;

    /** 授课年份 */
    private Integer year;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 审核状态 */
    private String status;

    /** 审核意见 */
    private String remark;

}