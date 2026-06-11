package com.ruoyi.manage.domain.research;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 科研课题实体类
 * 对应表：research_subject
 */
@Data
@TableName("research_subject") // 数据库表名
public class ResearchSubject{

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO) // 自增主键
    private Long id;

    /** 用户ID（外键，关联用户表） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 项目名称（前端：projectName） */
    private String projectName;

    /** 课题类型（前端：subjectType，如“国家自然基金”） */
    private String subjectType;

    /** 当年到位金额 */
    private BigDecimal money;

    /** 执行时间-开始*/
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("execute_time_start")
    private LocalDate executeTimeStart;

    /** 执行时间-结束 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("execute_time_end")
    private LocalDate executeTimeEnd;

    /** 页面选中年份（从前端选择的年份，如2025） */
    private String year;

    /** 排名（前端：rank，MySQL保留关键字，需转义） */
    @TableField("`rank`") // 反引号转义保留关键字
    private BigDecimal rank;

    /** 系数（前端：coefficient，如1.0/0.8） */
    private BigDecimal coefficient;

    /** PDF文件URL（前端：pdfUrl） */
    private String pdfUrl;

    /** 工作量（前端：workload） */
    private BigDecimal workload;

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