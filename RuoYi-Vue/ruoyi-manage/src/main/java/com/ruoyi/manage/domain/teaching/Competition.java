package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学科竞赛实体类（对应数据库表：teaching_competition）
 * @TableName teaching_competition
 */
@TableName(value = "teaching_competition")
@Data
public class Competition implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（对应序号列） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联用户ID（外键，关联用户表） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 获奖项目名称 */
    private String awardProjectName;

    /** 赛事等级（国家级/省部级/校级） */
    private String competitionLevel;

    /** 获奖级别（特等奖/一等奖/二等奖/三等奖） */
    private String awardLevel;

    /** 工作量 */
    private BigDecimal workload;

    /** PDF file URL */
    private String pdfUrl;

    /** 页面选中年份（如2025） */
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
