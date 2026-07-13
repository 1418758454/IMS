-- Add dynamic education background fields.
-- Run once on an existing database before deploying the backend changes.

ALTER TABLE `user_registration`
  ADD COLUMN `master_school` varchar(100) DEFAULT NULL COMMENT '硕士毕业学校' AFTER `undergrad_time`,
  ADD COLUMN `master_time` date DEFAULT NULL COMMENT '硕士毕业时间' AFTER `master_school`,
  ADD COLUMN `doctor_school` varchar(100) DEFAULT NULL COMMENT '博士毕业学校' AFTER `master_time`,
  ADD COLUMN `doctor_time` date DEFAULT NULL COMMENT '博士毕业时间' AFTER `doctor_school`,
  ADD COLUMN `postdoctoral_experience` varchar(500) DEFAULT NULL COMMENT '博士后经历' AFTER `doctor_time`;

ALTER TABLE `basic_information`
  ADD COLUMN `master_school` varchar(100) DEFAULT NULL COMMENT '硕士毕业学校' AFTER `undergrad_time`,
  ADD COLUMN `master_time` date DEFAULT NULL COMMENT '硕士毕业时间' AFTER `master_school`,
  ADD COLUMN `doctor_school` varchar(100) DEFAULT NULL COMMENT '博士毕业学校' AFTER `master_time`,
  ADD COLUMN `doctor_time` date DEFAULT NULL COMMENT '博士毕业时间' AFTER `doctor_school`,
  ADD COLUMN `postdoctoral_experience` varchar(500) DEFAULT NULL COMMENT '博士后经历' AFTER `doctor_time`;

UPDATE `user_registration`
SET `master_time` = `highest_education_time`
WHERE `master_time` IS NULL
  AND `highest_education_time` IS NOT NULL
  AND `highest_education` LIKE '%硕士%';

UPDATE `user_registration`
SET `doctor_time` = `highest_education_time`
WHERE `doctor_time` IS NULL
  AND `highest_education_time` IS NOT NULL
  AND `highest_education` LIKE '%博士%';

UPDATE `basic_information`
SET `master_time` = `highest_education_time`
WHERE `master_time` IS NULL
  AND `highest_education_time` IS NOT NULL
  AND `highest_education` LIKE '%硕士%';

UPDATE `basic_information`
SET `doctor_time` = `highest_education_time`
WHERE `doctor_time` IS NULL
  AND `highest_education_time` IS NOT NULL
  AND `highest_education` LIKE '%博士%';
