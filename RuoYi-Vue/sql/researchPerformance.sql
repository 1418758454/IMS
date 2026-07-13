/**
 * 1. 科研课题表
 */
CREATE TABLE `research_subject` (
                                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                    `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                    `project_name` VARCHAR(255) NOT NULL COMMENT '项目名称',
                                    `subject_type` VARCHAR(50) NOT NULL COMMENT '课题类型（国家自然基金/省部级项目等）',
                                    `money` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '当年到位经费(元)',
                                    `execute_time_start` DATE NOT NULL COMMENT '执行时间-开始',
                                    `execute_time_end` DATE NOT NULL COMMENT '执行时间-结束',
                                    `year` VARCHAR(50) NOT NULL COMMENT '年份（如：2023）',
                                    `rank` DECIMAL(5,2) NOT NULL COMMENT '排名',
                                    `coefficient` DECIMAL(5,2) NOT NULL COMMENT '系数（1.0/0.8/0.5等）',
                                    `pdf_url` VARCHAR(500) COMMENT 'PDF文件URL（可选）',
                                    `workload` DECIMAL(10,2) NOT NULL COMMENT '工作量',
                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                    `remark` TEXT COMMENT '审核意见（可选）',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研课题表';

/**
 * 2. 科研论文表
 */
CREATE TABLE `research_paper` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                  `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                  `title` VARCHAR(255) NOT NULL COMMENT '论文名称',
                                  `journal` VARCHAR(255) NOT NULL COMMENT '出版刊物',
                                  `publish_time` DATE NOT NULL COMMENT '出版时间',
                                  `level` VARCHAR(50) NOT NULL COMMENT '论文级别（SCI一区/核心期刊等）',
                                  `year` VARCHAR(50) NOT NULL COMMENT '年份（如：2023）',
                                  `rank` DECIMAL(5,2) NOT NULL COMMENT '排名',
                                  `coefficient` DECIMAL(5,2) NOT NULL COMMENT '系数（1.0/0.8/0.5等）',
                                  `pdf_url` VARCHAR(500) COMMENT 'PDF文件URL（可选）',
                                  `workload` DECIMAL(10,2) NOT NULL COMMENT '工作量',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                  `remark` TEXT COMMENT '审核意见（可选）',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研论文表';

/**
 * 3. 科研论著表
 */
CREATE TABLE `research_monograph` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                      `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                      `title` VARCHAR(255) NOT NULL COMMENT '论著名称',
                                      `publisher` VARCHAR(255) NOT NULL COMMENT '出版社',
                                      `publish_time` DATE NOT NULL COMMENT '出版时间',
                                      `monograph_type` VARCHAR(50) NOT NULL COMMENT '专著类型',
                                      `year` VARCHAR(50) NOT NULL COMMENT '年份（如：2023）',
                                      `rank` DECIMAL(5,2) NOT NULL COMMENT '排名',
                                      `coefficient` DECIMAL(5,2) NOT NULL COMMENT '系数（1.0/0.8/0.5等）',
                                      `pdf_url` VARCHAR(500) COMMENT 'PDF文件URL（可选）',
                                      `workload` DECIMAL(10,2) NOT NULL COMMENT '工作量',
                                      `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                      `remark` TEXT COMMENT '审核意见（可选）',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研论著表';

/**
* 4. 科研获奖表
*/
CREATE TABLE `research_award` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                  `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                  `name` VARCHAR(255) NOT NULL COMMENT '获奖名称',
                                  `organizer` VARCHAR(255) NOT NULL COMMENT '颁奖单位',
                                  `award_time` DATE NOT NULL COMMENT '获奖时间',
                                  `level` VARCHAR(50) NOT NULL COMMENT '奖励级别（国家级/省部级等）',
                                  `year` VARCHAR(50) NOT NULL COMMENT '年份（如：2023）',
                                  `rank` DECIMAL(5,2) NOT NULL COMMENT '排名',
                                  `coefficient` DECIMAL(6,2) NOT NULL COMMENT '系数（1.0/0.8/0.5等）',
                                  `pdf_url` VARCHAR(500) COMMENT 'PDF文件URL（可选）',
                                  `workload` DECIMAL(10,2) NOT NULL COMMENT '工作量',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                  `remark` TEXT COMMENT '审核意见（可选）',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研获奖表';

/**
 * 5. 科研专利表
 */
CREATE TABLE `research_patent` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                   `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                   `name` VARCHAR(255) NOT NULL COMMENT '专利名称',
                                   `type` VARCHAR(50) NOT NULL COMMENT '专利类型（发明专利/实用新型专利等）',
                                   `apply_time` DATE NOT NULL COMMENT '申请时间',
                                   `authorize_time` DATE COMMENT '授权时间（可选）',
                                   `year` VARCHAR(50) NOT NULL COMMENT '年份（如：2023）',
                                   `rank` DECIMAL(5,2) NOT NULL COMMENT '排名',
                                   `coefficient` DECIMAL(5,2) NOT NULL COMMENT '系数（1.0/0.8/0.5等）',
                                   `pdf_url` VARCHAR(500) COMMENT 'PDF文件URL（可选）',
                                   `workload` DECIMAL(10,2) NOT NULL COMMENT '工作量',
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                   `remark` TEXT COMMENT '审核意见（可选）',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研专利表';

/**
 * 6. 科研软件著作权表
 */
CREATE TABLE `research_software` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `user_id` BIGINT NOT NULL COMMENT '关联用户ID（外键，关联用户表）',
                                     `user_name` VARCHAR(255) NOT NULL COMMENT '用户名称',
                                     `name` VARCHAR(255) NOT NULL COMMENT '软著名称',
                                     `apply_time` DATE NOT NULL COMMENT '申请时间',
                                     `authorize_time` DATE COMMENT '授权时间（可选）',
                                     `year` VARCHAR(50) NOT NULL COMMENT '年份（如：2023）',
                                     `rank` DECIMAL(5,2) NOT NULL COMMENT '排名',
                                     `coefficient` DECIMAL(5,2) NOT NULL DEFAULT 2.00 COMMENT '系数（当前默认2.0）',
                                     `pdf_url` VARCHAR(500) COMMENT 'PDF文件URL（可选）',
                                     `workload` DECIMAL(10,2) NOT NULL COMMENT '工作量',
                                     `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态（待审核/审核通过/返回修改）',
                                     `remark` TEXT COMMENT '审核意见（可选）',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研软著表';
