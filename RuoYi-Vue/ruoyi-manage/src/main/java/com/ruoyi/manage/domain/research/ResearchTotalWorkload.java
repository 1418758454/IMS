package com.ruoyi.manage.domain.research;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 科研总体工作量实体类
 * 对应表：research_total_workload
 */
@Data
@TableName("research_total_workload") // 数据库表名
public class ResearchTotalWorkload implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户ID（主键）
    private BigDecimal userId;

    // 用户名
    private String userName;

    // 授课年份
    private String year;

    // 1. 课题工作量
//    private BigDecimal subjectWorkload;
    // 预计工作量
    private BigDecimal subjectEstimatedWorkload;
    // 实际工作量
    private BigDecimal subjectConfirmedWorkload;

    // 2. 论文工作量
//    private BigDecimal paperWorkload;
    // 预计工作量
    private BigDecimal paperEstimatedWorkload;
    // 实际工作量
    private BigDecimal paperConfirmedWorkload;

    // 3. 专著工作量
//    private BigDecimal monographWorkload;
    // 预计工作量
    private BigDecimal monographEstimatedWorkload;
    // 实际工作量
    private BigDecimal monographConfirmedWorkload;

    // 4. 获奖工作量
//    private BigDecimal awardWorkload;
    // 预计工作量
    private BigDecimal awardEstimatedWorkload;
    // 实际工作量
    private BigDecimal awardConfirmedWorkload;

    // 5.  专利工作量
//    private BigDecimal patentWorkload;
    // 预计工作量
    private BigDecimal patentEstimatedWorkload;
    // 实际工作量
    private BigDecimal patentConfirmedWorkload;

    // 6. 软著工作量
//    private BigDecimal softwareWorkload;
    // 预计工作量
    private BigDecimal softwareEstimatedWorkload;
    // 实际工作量
    private BigDecimal softwareConfirmedWorkload;

    // 7. 科研总工作量
    private BigDecimal totalWorkload;
}
