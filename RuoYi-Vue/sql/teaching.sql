/**
 * 1. 本科教学（理论课）表
 */
CREATE TABLE `teaching_undergraduate_theory_course` (
                                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                               `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                               `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                               `course_name` VARCHAR(255) NOT NULL COMMENT '课程名称',
                                               `student_class` VARCHAR(255) NOT NULL COMMENT '授课学生（如：2023级数学1班）',
                                               `course_type` VARCHAR(50) NOT NULL COMMENT '课程类型（必修课/选修课/通识课等）',
                                               `theory_hours` INT NOT NULL COMMENT '理论课时',
                                               `student_count` INT NOT NULL COMMENT '学生人数',
                                               `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',
                                               `year` INT NOT NULL COMMENT '授课年份（如：2023）',
                                               `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                               `remark` TEXT COMMENT '审核意见（可选）',
                                               PRIMARY KEY (`id`),
                                               KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下课程）',
                                               KEY `idx_year` (`year`) COMMENT '年份索引（按年度筛选课程）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（理论课）表';

/**
 * 2. 本科教学（实验课）表
 */
CREATE TABLE `teaching_undergraduate_experiment_course` (
                                                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                            `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                                            `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                                            `course_name` VARCHAR(255) NOT NULL COMMENT '课程名称',
                                                            `student_class` VARCHAR(255) NOT NULL COMMENT '授课学生（如：2023级计算机1班）',
                                                            `course_type` VARCHAR(50) NOT NULL COMMENT '课程类型（必修课/选修课/通识课等）',
                                                            `theory_hours` INT NOT NULL COMMENT '实验课时',  -- 字段名不变，注释调整为“实验课时”
                                                            `student_count` INT NOT NULL COMMENT '学生人数',
                                                            `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',
                                                            `year` INT NOT NULL COMMENT '授课年份（如：2023）',
                                                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                            `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                                            `remark` TEXT COMMENT '审核意见（可选）',
                                                            PRIMARY KEY (`id`),
                                                            KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下实验课程）',  -- 索引注释适配实验课场景
                                                            KEY `idx_year` (`year`) COMMENT '年份索引（按年度筛选实验课程）'  -- 索引注释适配实验课场景
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（实验课）表';

/**
 * 3. 本科教学（实践教学）表
 */
CREATE TABLE `teaching_undergraduate_practice_course` (
                                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                          `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                                          `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                                          `course_name` VARCHAR(255) NOT NULL COMMENT '课程名称',
                                                          `course_type` VARCHAR(50) NOT NULL COMMENT '课程类型（实习/实训/课程设计）',
                                                          `plan_days` INT NOT NULL COMMENT '计划天数',  -- 对应前端“计划天数”列（替换实验课的theory_hours）
                                                          `class_count` INT NOT NULL COMMENT '班级数',  -- 对应前端“班级数”列（替换实验课的student_count）
                                                          `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',  -- 与实验课保持一致
                                                          `year` INT NOT NULL COMMENT '授课年份（如：2023）',
                                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                          `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                                          `remark` TEXT COMMENT '审核意见（可选）',
                                                          PRIMARY KEY (`id`),
                                                          KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下实践课程）',  -- 与实验课索引逻辑一致
                                                          KEY `idx_year` (`year`) COMMENT '年份索引（按年度筛选实践课程）'  -- 与实验课索引逻辑一致
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（实践教学）表';

/**
 * 本科教学任务截图附件表
 * 仅供理论课、实验课、实践教学三个模块按教师和年度共用。
 */
CREATE TABLE `teaching_task_screenshot_attachment` (
                                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                          `user_id` BIGINT NOT NULL COMMENT '教师用户ID',
                                                          `year` INT NOT NULL COMMENT '年度',
                                                          `module_type` VARCHAR(20) NOT NULL COMMENT '模块类型：theory、experiment、practice',
                                                          `file_url` VARCHAR(500) NOT NULL COMMENT 'PDF访问地址',
                                                          `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
                                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                          PRIMARY KEY (`id`),
                                                          KEY `idx_user_year_module` (`user_id`, `year`, `module_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学任务截图附件表';

/**
 * 4. 本科毕业论文课程表
 */
CREATE TABLE `undergraduate_thesis_course` (
                                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                               `user_id` bigint(20) NOT NULL COMMENT '用户ID（关联sys_user表）',
                                               `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                               `thesis_count` int(11) NOT NULL DEFAULT 1 COMMENT '指导完成毕业论文数量',
                                               `student_count` int(11) NOT NULL DEFAULT 1 COMMENT '同时指导人数',
                                               `is_pass_inspection` varchar(20) NOT NULL COMMENT '是否通过校/院抽查（pass-通过，fail-未通过）',
                                               `workload` decimal(11,3) NOT NULL DEFAULT 0.000 COMMENT '工作量（前端计算后存储）',
                                               `year` int(11) NOT NULL COMMENT '授课年份（如2025）',
                                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                               `remark` TEXT COMMENT '审核意见（可选）',
                                               PRIMARY KEY (`id`),
                                               KEY `idx_user_year` (`user_id`, `year`) COMMENT '用户+年份联合索引（查询效率优化）'
#                                                CONSTRAINT `fk_thesis_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE COMMENT '关联用户表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科毕业论文指导记录（与实验课模块结构对齐）';

/**
 * 5.本科教学（科技创新）表
 */
CREATE TABLE `teaching_science_innovation` (
                                               `pdf_url` VARCHAR(500) COMMENT 'PDF证明材料URL',
                                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                               `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                               `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                               `project_name` VARCHAR(255) NOT NULL COMMENT '项目名称',
                                               `end_year` INT NOT NULL COMMENT '结题年份（如：2024）',
                                               `level` VARCHAR(50) NOT NULL COMMENT '项目级别（国家级/省级）',
                                               `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',
                                               `year` int(11) NOT NULL COMMENT '页面选中年份（如2025）',
                                               `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                               `remark` TEXT COMMENT '审核意见（可选）',
                                               PRIMARY KEY (`id`),
                                               KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下科技创新项目）',
                                               KEY `idx_end_year` (`year`) COMMENT '结题年份索引（按年度筛选项目）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（科技创新）表';

/**
 * 6. 本科教学（学科竞赛）表
 */
CREATE TABLE `teaching_competition` (
                                        `pdf_url` VARCHAR(500) COMMENT 'PDF证明材料URL',
                                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID（对应序号列）',
                                        `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                        `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                        `award_project_name` VARCHAR(255) NOT NULL COMMENT '获奖项目名称',
                                        `competition_level` VARCHAR(50) NOT NULL COMMENT '赛事等级（国家级/省部级/校级）',
                                        `award_level` VARCHAR(50) NOT NULL COMMENT '获奖级别（特等奖/一等奖/二等奖/三等奖）',
                                        `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',
                                        `year` INT NOT NULL COMMENT '页面选中年份（如2025）',
                                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                        `remark` TEXT COMMENT '审核意见（可选）',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下学科竞赛项目）',
                                        KEY `idx_year` (`year`) COMMENT '选中年份索引（按年度筛选项目）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（学科竞赛）表';

/**
 * 7. 本科教学（出版教材）表
 */
CREATE TABLE `teaching_textbook` (
                                     `pdf_url` VARCHAR(500) COMMENT 'PDF证明材料URL',
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                     `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                     `textbook_name` VARCHAR(255) NOT NULL COMMENT '教材名称',
                                     `press_name` VARCHAR(255) NOT NULL COMMENT '出版社名称',
                                     `publish_date` DATE NOT NULL COMMENT '出版时间（如：2024-06-15）',
                                     `level` VARCHAR(50) NOT NULL COMMENT '教材级别（国家级/省部级/校级）',
                                     `compiler_identity` VARCHAR(50) NOT NULL COMMENT '教材编写身份（主编/副主编/参编/编委）',
                                     `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',
                                     `year` INT NOT NULL COMMENT '页面选中年份（如2025）',
                                     `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                     `remark` TEXT COMMENT '审核意见（可选）',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下出版教材）',
                                     KEY `idx_year` (`year`) COMMENT '选中年份索引（按年度筛选教材）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（出版教材）表';

/**
 * 8.本科教学（教改项目）表
 * 说明：与科技创新表结构一致，仅调整"结题时间"字段类型及表名/注释
 */
CREATE TABLE `teaching_education_reform` (
                                             `pdf_url` VARCHAR(500) COMMENT 'PDF证明材料URL',
                                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                             `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                             `project_name` VARCHAR(255) NOT NULL COMMENT '项目名称',
                                             `end_date` DATE NOT NULL COMMENT '结题时间（如：2024-12-31）',  -- 核心调整：由年份整数改为日期类型
                                             `level` VARCHAR(50) NOT NULL COMMENT '项目级别（国家级/省部级/校级）',
                                             `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量（手动输入，保留三位小数）',  -- 手动输入标识
                                             `year` INT NOT NULL COMMENT '页面选中年份（从结题时间提取，如2025）',  -- 与科技创新表逻辑一致
                                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                             `remark` TEXT COMMENT '审核意见（可选）',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下教改项目）',
                                             KEY `idx_year` (`year`) COMMENT '页面选中年份索引（按年度筛选项目）'  -- 索引字段与科技创新表一致
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（教改项目）表';

/**
 * 9.本科教学（教改论文）表
 * 说明：与科技创新表结构一致，仅调整"结题时间"字段类型及表名/注释
 */
CREATE TABLE `teaching_education_reform_paper` (
                                                   `pdf_url` VARCHAR(500) COMMENT 'PDF证明材料URL',
                                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                   `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                                   `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                                   `paper_name` VARCHAR(255) NOT NULL COMMENT '论文名称',
                                                   `publish_date` DATE NOT NULL COMMENT '发表日期（如：2024-12-31）',
                                                   `level` VARCHAR(50) NOT NULL COMMENT '论文级别（国家级/省部级/校级）',
                                                   `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',
                                                   `year` INT NOT NULL COMMENT '页面选中年份（从发表日期提取，如2025）',
                                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                   `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                   `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                                   `remark` TEXT COMMENT '审核意见（可选）',
                                                   PRIMARY KEY (`id`),
                                                   KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下教改论文）',
                                                   KEY `idx_publish_year` (`year`) COMMENT '发表年份索引（按年度筛选论文）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（教改论文）表';

/**
 * 10.本科教学（监考）表
 */
CREATE TABLE `teaching_proctor` (
                                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID（序号）',
                                    `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                    `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                    `exam_type` VARCHAR(50) NOT NULL COMMENT '监考类型（期末考试/补考/四六级考试/研究生考试）',
                                    `exam_count` INT NOT NULL COMMENT '本年度监考次数',
                                    `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',
                                    `year` INT NOT NULL COMMENT '页面选中年份（如2025）',
                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                    `remark` TEXT COMMENT '审核意见（可选）',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下监考记录）',
                                    KEY `idx_proctor_year` (`year`) COMMENT '监考年份索引（按年度筛选监考记录）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本科教学（监考）表';

/**
 * 11.研究生教学（理论课）表
 */
CREATE TABLE `teaching_graduate_theory_course` (
                                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                   `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                                   `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                                   `course_name` VARCHAR(255) NOT NULL COMMENT '课程名称',
                                                   `student_class` VARCHAR(255) NOT NULL COMMENT '授课学生（如：2023级计算机硕士1班）',
                                                   `course_type` VARCHAR(50) NOT NULL COMMENT '课程类型（必修课/选修课/通识课等）',
                                                   `is_experimental_course` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为实验课（0：否，1：是）',
                                                   `theory_hours` INT NOT NULL COMMENT '理论课时',
                                                   `student_count` INT NOT NULL COMMENT '学生人数',
                                                   `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量',
                                                   `year` INT NOT NULL COMMENT '授课年份（如：2023）',
                                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                   `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                   `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                                   `remark` TEXT COMMENT '审核意见（可选）',
                                                   PRIMARY KEY (`id`),
                                                   KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下课程）',
                                                   KEY `idx_year` (`year`) COMMENT '年份索引（按年度筛选课程）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='研究生教学（理论课）表';

/**
 * 12.研究生教学（指导学生）表
 */
CREATE TABLE `teaching_graduate_guide_student` (
                                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                   `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                                   `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                                   `student_count` INT NOT NULL COMMENT '学生数（如：3）',
                                                   `student_type` VARCHAR(50) NOT NULL COMMENT '学生类型（下拉框值：如一年级学硕/二年级专硕/博士研究生）',
                                                   `coefficient` INT NOT NULL COMMENT '系数（与学生类型绑定：如一年级学硕系数30）',
                                                   `workload` DECIMAL(11, 3) NOT NULL COMMENT '工作量（手动输入，保留三位小数）',
                                                   `year` INT NOT NULL COMMENT '页面选中年份（从前端选择的年份，如2025）',
                                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                   `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                   `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                                   `remark` TEXT COMMENT '审核意见（可选）',
                                                   PRIMARY KEY (`id`),
                                                   KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引（查询教师名下指导学生记录）',
                                                   KEY `idx_year` (`year`) COMMENT '页面选中年份索引（按年度筛选指导记录）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='研究生教学（指导学生）表';
