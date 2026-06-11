package com.ruoyi.manage.domain.teaching;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 本科理论课总体工作量实体类
 * 对应表：teaching_total_workload
 */
@Data
@TableName("teaching_total_workload") // 数据库表名
public class TeachingTotalWorkload implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户ID（主键）
    private BigDecimal userId;

    // 用户名
    private String userName;

    // 授课年份
    private Integer year;

    // 1. 理论课工作量
    // 预计工作量
    private BigDecimal theoryCourseEstimatedWorkload;
    // 实际工作量
    private BigDecimal theoryCourseConfirmedWorkload;

    // 2. 实验课工作量
    // 预计工作量
    private BigDecimal experimentCourseEstimatedWorkload;
    // 实际工作量
    private BigDecimal experimentCourseConfirmedWorkload;

    // 3. 实践教学工作量
    // 预计工作量
    private BigDecimal practicalTeachingEstimatedWorkload;
    // 实际工作量
    private BigDecimal practicalTeachingConfirmedWorkload;

    // 4. 毕业论文工作量
    // 预计工作量
    private BigDecimal thesisCourseEstimatedWorkload;
    // 实际工作量
    private BigDecimal thesisCourseConfirmedWorkload;

    // 5. 科技创新工作量
    // 预计工作量
    private BigDecimal scienceInnovationEstimatedWorkload;
    // 实际工作量
    private BigDecimal scienceInnovationConfirmedWorkload;

    // 6. 学科竞赛工作量
    // 预计工作量
    private BigDecimal competitionEstimatedWorkload;
    // 实际工作量
    private BigDecimal competitionConfirmedWorkload;

    // 7. 出版教材工作量
    // 预计工作量
    private BigDecimal textbookEstimatedWorkload;
    // 实际工作量
    private BigDecimal textbookConfirmedWorkload;

    // 8. 教改项目工作量
    // 预计工作量
    private BigDecimal educationReformEstimatedWorkload;
    // 实际工作量
    private BigDecimal educationReformConfirmedWorkload;

    // 9. 教改论文工作量
    // 预计工作量
    private BigDecimal educationReformPaperEstimatedWorkload;
    // 实际工作量
    private BigDecimal educationReformPaperConfirmedWorkload;

    // 10. 监考模块工作量
    // 预计工作量
    private BigDecimal proctorEstimatedWorkload;
    // 实际工作量
    private BigDecimal proctorConfirmedWorkload;

    // 11. 研究生理论课工作量
    // 预计工作量
    private BigDecimal graduateTheoryCourseEstimatedWorkload;
    // 实际工作量
    private BigDecimal graduateTheoryCourseConfirmedWorkload;

    // 12. 研究生指导学生工作量
    // 预计工作量
    private BigDecimal graduateGuideStudentEstimatedWorkload;
    // 实际工作量
    private BigDecimal graduateGuideStudentConfirmedWorkload;

    // 13. 教学总工作量
    private BigDecimal totalWorkload;
}
