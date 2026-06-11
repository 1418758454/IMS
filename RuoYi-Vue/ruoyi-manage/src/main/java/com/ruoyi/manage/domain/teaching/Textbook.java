package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出版教材实体类（对应数据库表：teaching_textbook）
 * @TableName teaching_textbook
 */
@TableName(value = "teaching_textbook")
@Data
public class Textbook implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联用户ID（外键，关联用户表） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 教材名称 */
    private String textbookName;

    /** 出版社名称 */
    private String pressName;

    /** 出版时间（如：2024-06-15） */
    private LocalDate publishDate;

    /** 教材级别（国家级/省部级/校级） */
    private String level;

    /** 教材编写身份（主编/副主编/参编/编委） */
    private String compilerIdentity;

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
