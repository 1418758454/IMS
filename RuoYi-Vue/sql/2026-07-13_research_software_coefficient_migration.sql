-- 为软著记录保存实际采用的系数，当前新记录默认使用 2.00。
-- 本脚本兼容 coefficient 字段已存在和不存在两种数据库状态。

SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'research_software'
      AND COLUMN_NAME = 'coefficient'
);

SET @add_column_sql = IF(
    @column_exists = 0,
    'ALTER TABLE `research_software` ADD COLUMN `coefficient` DECIMAL(5,2) NOT NULL DEFAULT 2.00 COMMENT ''系数（当前默认2.0）'' AFTER `rank`',
    'SELECT 1'
);

PREPARE add_column_stmt FROM @add_column_sql;
EXECUTE add_column_stmt;
DEALLOCATE PREPARE add_column_stmt;

UPDATE `research_software`
SET `coefficient` = 2.00
WHERE `coefficient` IS NULL;

ALTER TABLE `research_software`
    MODIFY COLUMN `coefficient` DECIMAL(5,2) NOT NULL DEFAULT 2.00 COMMENT '系数（当前默认2.0）';
