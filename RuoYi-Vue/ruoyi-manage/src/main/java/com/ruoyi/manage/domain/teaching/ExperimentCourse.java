package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * @TableName teaching_undergraduate_experiment_course
 */
@TableName(value ="teaching_undergraduate_experiment_course")
@Data
public class ExperimentCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 用户名 */
    private String userName;

    private String courseName;

    private String studentClass;

    private String courseType;

    private Integer theoryHours;

    private Integer studentCount;

    private BigDecimal workload;

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