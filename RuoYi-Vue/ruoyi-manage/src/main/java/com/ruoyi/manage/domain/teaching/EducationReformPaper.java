package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教改论文实体类（对应数据库表：teaching_education_reform_paper）
 * @TableName teaching_education_reform_paper
 */
@TableName(value = "teaching_education_reform_paper")
@Data
public class EducationReformPaper implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联用户ID（外键，关联用户表） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 论文名称 */
    private String paperName;

    /** 发表日期（如：2024-12-31） */
    private LocalDate publishDate;

    /** 论文级别（国家级/省部级/校级） */
    private String level;

    /** 工作量 */
    private BigDecimal workload;

    /** PDF file URL */
    private String pdfUrl;

    /** 页面选中年份（从发表日期提取，如2025） */
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
