package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 本科毕业论文指导实体类（与实验课UndergraduateExperimentCourse.java格式完全一致）
 */
@Data
@TableName("undergraduate_thesis_course")
public class ThesisCourse {

    /**
     * 主键ID（与实验课模块一致：bigint自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（关联sys_user表，与实验课模块字段名一致）
     */
    @TableField("user_id")
    private Long userId;

    /** 用户名 */
    private String userName;

    /**
     * 指导完成毕业论文数（核心业务字段，对应前端thesisCount）
     */
    @TableField("thesis_count")
    private Integer thesisCount;

    /**
     * 同时指导人数（核心业务字段，对应前端studentCount）
     */
    @TableField("student_count")
    private Integer studentCount;

    /**
     * 是否通过校/院抽查（对应前端isPassInspection，值为pass/fail）
     */
    @TableField("is_pass_inspection")
    private String isPassInspection;

    /**
     * 工作量（前端计算后存储，与实验课workload字段类型一致）
     */
    @TableField("workload")
    private BigDecimal workload;

    /**
     * 授课年份（与实验课模块year字段一致）
     */
    @TableField("year")
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