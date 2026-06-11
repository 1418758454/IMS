package com.ruoyi.manage.domain.research;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 科研获奖实体类
 * 对应表：research_award
 */
@Data
@TableName("research_award") // 数据库表名
public class ResearchAward {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（外键） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 获奖名称（前端：name） */
    private String name;

    /** 颁奖单位（前端：organizer） */
    private String organizer;

    /** 获奖时间（前端：awardTime） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate awardTime;

    /** 奖励级别（前端：level，如“国家级”） */
    private String level;

    /** 页面选中年份（从前端选择的年份，如2025） */
    private String year;

    /** 排名（MySQL保留关键字，需转义） */
    @TableField("`rank`")
    private BigDecimal rank;

    /** 系数（前端：coefficient，如1.0/0.8） */
    private BigDecimal coefficient;

    /** PDF文件URL（前端：pdfUrl） */
    private String pdfUrl;

    /** 工作量（前端：workload） */
    private BigDecimal workload;

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