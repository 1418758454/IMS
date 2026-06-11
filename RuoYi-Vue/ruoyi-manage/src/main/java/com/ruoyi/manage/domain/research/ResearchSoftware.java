package com.ruoyi.manage.domain.research;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 科研软著实体类
 * 对应表：research_software
 */
@Data
@TableName("research_software") // 数据库表名
public class ResearchSoftware {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（外键） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 软著名称（前端：name） */
    private String name;

    /** 申请时间（前端：applyTime） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyTime;

    /** 授权时间（前端：authorizeTime） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate authorizeTime;

    /** 页面选中年份（从前端选择的年份，如2025） */
    private String year;

    /** 排名（MySQL保留关键字，需转义） */
    @TableField("`rank`")
    private BigDecimal rank;

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