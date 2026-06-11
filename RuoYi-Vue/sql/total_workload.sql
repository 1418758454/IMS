
/**
  * 创建科研总工作量统计表
 */
 CREATE TABLE research_total_workload (
     -- 0. 自增主键
                                id INT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    -- 0.1. 用户ID
                                user_id DECIMAL(20, 0) NOT NULL COMMENT '用户ID（数值型主键）',
    -- 0.2. 用户名（字符串类型，非数值字段需特殊处理，此处保留 VARCHAR 以符合实际业务）
                                user_name VARCHAR(50) NOT NULL COMMENT '用户名',
    -- 0.3. 年份
                                `year` VARCHAR(50) NOT NULL COMMENT '年份（如：2023）',
    -- 1. 课题工作量
--                                 subject_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '课题工作量',
                                subject_estimated_workload DECIMAL(11, 3) DEFAULT 0.0000 COMMENT '课题预计工作量',
                                subject_confirmed_workload DECIMAL(11, 3) DEFAULT 0.0000 COMMENT '课题已确认工作量',
    -- 2. 论文工作量
--                                 paper_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '论文工作量',
                                paper_estimated_workload DECIMAL(11, 3) DEFAULT 0.0000 COMMENT '论文预计工作量',
                                paper_confirmed_workload DECIMAL(11, 3) DEFAULT 0.0000 COMMENT '论文已确认工作量',

     -- 3. 论著工作量
--                                 monograph_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '论著工作量',
                                monograph_estimated_workload DECIMAL(11, 3) DEFAULT 0.0000 COMMENT '论著预计工作量',
                                monograph_confirmed_workload DECIMAL(11, 3) DEFAULT 0.0000 COMMENT '论著已确认工作量',

     -- 4. 获奖工作量
--                                 award_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '获奖工作量',
                                award_estimated_workload DECIMAL(11, 3) DEFAULT 0.0000 COMMENT '获奖预计工作量',
                                award_confirmed_workload DECIMAL(11, 3) DEFAULT 0.0000 COMMENT '获奖已确认工作量',

     -- 5. 专利工作量
--                                 patent_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '专利工作量',
                                patent_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '专利预计工作量',
                                patent_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '专利已确认工作量',

     -- 6. 软著工作量
--                                 software_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '软著工作量',
                                software_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '软著预计工作量',
                                software_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '软著已确认工作量',

     -- 7. 科研总工作量
                                total_workload DECIMAL(12, 3) DEFAULT 0.000 COMMENT '科研总工作量',
    -- 索引：支持按用户名查询
                                KEY idx_user_name (user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研总工作量统计表';

/**
  * 创建教学总工作量统计表
 */
CREATE TABLE teaching_total_workload (
    -- 0. 自增主键
                                id INT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    -- 0.1. 用户ID
                                user_id DECIMAL(20, 0) NOT NULL COMMENT '用户ID（数值型主键）',
    -- 0.2. 用户名（字符串类型，非数值字段需特殊处理，此处保留 VARCHAR 以符合实际业务）
                                user_name VARCHAR(50) NOT NULL COMMENT '用户名',
    -- 0.3. 授课年份
                                `year` INT NOT NULL COMMENT '授课年份（如：2023）',
    -- 1. 理论课工作量
--                                 theory_course_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '理论课工作量',
                                theory_course_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '理论课预计工作量',
                                theory_course_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '理论课已确认工作量',
    -- 2. 实验课工作量
                                experiment_course_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '实验课预计工作量',
                                experiment_course_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '实验课已确认工作量',
    -- 3. 实践教学工作量
                                practical_teaching_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '实践教学预计工作量',
                                practical_teaching_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '实践教学已确认工作量',
    -- 4. 毕业论文工作量
                                thesis_course_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '毕业论文预计工作量',
                                thesis_course_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '毕业论文已确认工作量',
    -- 5. 科技创新工作量
                                science_innovation_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '科技创新预计工作量',
                                science_innovation_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '科技创新已确认工作量',
    -- 6. 学科竞赛工作量
                                competition_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '学科竞赛预计工作量',
                                competition_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '学科竞赛已确认工作量',
    -- 7. 出版教材工作量
                                textbook_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '出版教材预计工作量',
                                textbook_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '出版教材已确认工作量',
    -- 8. 教改项目工作量
                                education_reform_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '教改项目预计工作量',
                                education_reform_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '教改项目已确认工作量',
    -- 9. 教改论文工作量
                                education_reform_paper_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '教改论文预计工作量',
                                education_reform_paper_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '教改论文已确认工作量',
    -- 10. 监考工作量
                                proctor_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '监考预计工作量',
                                proctor_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '监考已确认工作量',
    -- 11. 研究生理论课工作量
                                graduate_theory_course_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '研究生理论课预计工作量',
                                graduate_theory_course_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '研究生理论课已确认工作量',
    -- 12. 研究生指导学生工作量
                                graduate_guide_student_estimated_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '研究生指导学生预计工作量',
                                graduate_guide_student_confirmed_workload DECIMAL(11, 3) DEFAULT 0.000 COMMENT '研究生指导学生已确认工作量',
    -- 13. 教学总工作量
                                total_workload DECIMAL(12, 3) DEFAULT 0.000 COMMENT '教学总工作量',
    -- 索引：支持按用户名查询
                                KEY idx_user_name (user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学总工作量统计表（含理论课、实践课等多维度工作量）';


/*
 *修改教学业绩工作量字段sql语句
 */
-- 修改教学总工作量表结构
ALTER TABLE `teaching_total_workload`
-- 删除原有的单一工作量字段
DROP COLUMN `theory_course_workload`,
DROP COLUMN `practical_course_workload`,
DROP COLUMN `practical_teaching_workload`,
DROP COLUMN `thesis_course_workload`,
DROP COLUMN `science_innovation_workload`,
DROP COLUMN `competition_workload`,
DROP COLUMN `textbook_workload`,
DROP COLUMN `education_reform_workload`,
DROP COLUMN `education_reform_paper_workload`,
DROP COLUMN `proctor_workload`,
DROP COLUMN `graduate_theory_course_workload`,
DROP COLUMN `graduate_guide_student_workload`,

-- 添加理论课预计和已确认工作量字段
ADD COLUMN `theory_course_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '理论课预计工作量',
ADD COLUMN `theory_course_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '理论课已确认工作量',

-- 添加实验课预计和已确认工作量字段
ADD COLUMN `experiment_course_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '实验课预计工作量',
ADD COLUMN `experiment_course_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '实验课已确认工作量',

-- 添加实践教学预计和已确认工作量字段
ADD COLUMN `practical_teaching_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '实践教学预计工作量',
ADD COLUMN `practical_teaching_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '实践教学已确认工作量',

-- 添加毕业论文预计和已确认工作量字段
ADD COLUMN `thesis_course_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '毕业论文预计工作量',
ADD COLUMN `thesis_course_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '毕业论文已确认工作量',

-- 添加科技创新预计和已确认工作量字段
ADD COLUMN `science_innovation_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '科技创新预计工作量',
ADD COLUMN `science_innovation_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '科技创新已确认工作量',

-- 添加学科竞赛预计和已确认工作量字段
ADD COLUMN `competition_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '学科竞赛预计工作量',
ADD COLUMN `competition_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '学科竞赛已确认工作量',

-- 添加出版教材预计和已确认工作量字段
ADD COLUMN `textbook_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '出版教材预计工作量',
ADD COLUMN `textbook_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '出版教材已确认工作量',

-- 添加教改项目预计和已确认工作量字段
ADD COLUMN `education_reform_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '教改项目预计工作量',
ADD COLUMN `education_reform_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '教改项目已确认工作量',

-- 添加教改论文预计和已确认工作量字段
ADD COLUMN `education_reform_paper_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '教改论文预计工作量',
ADD COLUMN `education_reform_paper_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '教改论文已确认工作量',

-- 添加监考预计和已确认工作量字段
ADD COLUMN `proctor_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '监考预计工作量',
ADD COLUMN `proctor_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '监考已确认工作量',

-- 添加研究生理论课预计和已确认工作量字段
ADD COLUMN `graduate_theory_course_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '研究生理论课预计工作量',
ADD COLUMN `graduate_theory_course_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '研究生理论课已确认工作量',

-- 添加研究生指导学生预计和已确认工作量字段
ADD COLUMN `graduate_guide_student_estimated_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '研究生指导学生预计工作量',
ADD COLUMN `graduate_guide_student_confirmed_workload` DECIMAL(11, 3) DEFAULT 0.000 COMMENT '研究生指导学生已确认工作量';

alter table
ADD COLUMN `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
ADD COLUMN `remark` TEXT COMMENT '审核意见（可选）'