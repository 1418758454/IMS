package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 研究生指导学生实体类（对应数据库表：teaching_graduate_guide_student）
 * @TableName teaching_graduate_guide_student
 */
@TableName(value = "teaching_graduate_guide_student")
@Data
public class GraduateGuideStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String userName;

    /** 关联用户ID（外键，关联用户表） */
    private Long userId;

    /** 学生数（如：3） */
    private Integer studentCount;

    /** 学生类型（下拉框值：如一年级学硕/二年级专硕/博士研究生） */
    private String studentType;

    /** 系数（与学生类型绑定：如一年级学硕系数30） */
    private Integer coefficient;

    /** 工作量（手动输入，保留三位小数） */
    private BigDecimal workload;

    /** 页面选中年份（从前端选择的年份，如2025） */
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