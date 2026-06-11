package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 监考实体类（对应数据库表：teaching_proctor）
 */
@TableName("teaching_proctor")
@Data
public class Proctor implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（序号） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联用户ID（外键，关联用户表） */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 监考类型（期末考试/补考/四六级考试/研究生考试） */
    private String examType;

    /** 本年度监考次数 */
    private Integer examCount;

    /** 工作量 */
    private BigDecimal workload;

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