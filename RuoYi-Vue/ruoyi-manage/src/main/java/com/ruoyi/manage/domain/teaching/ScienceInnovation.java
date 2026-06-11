package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 科技创新实体类（对应数据库表：teaching_science_innovation）
 * @TableName teaching_science_innovation
 */
@TableName(value = "teaching_science_innovation")
@Data
public class ScienceInnovation implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联用户ID（外键，关联用户表） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 项目名称 */
    private String projectName;

    /** 结题年份（如：2024） */
    private Integer endYear;

    /** 项目级别（国家级/省级） */
    private String level;

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
