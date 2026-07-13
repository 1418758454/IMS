-- 本科教学任务截图附件迁移脚本
-- 本表独立于既有课程表，不需要回填或修改历史课程数据。

CREATE TABLE IF NOT EXISTS `teaching_task_screenshot_attachment` (
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
