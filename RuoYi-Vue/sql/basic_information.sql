/**
 * 1. 注册信息表
 */
CREATE TABLE `user_registration` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                     `user_id` varchar(50) NOT NULL COMMENT '工号（必填）',
                                     `name` varchar(50) NOT NULL COMMENT '姓名（必填）',
                                     `gender` enum('男','女','其他') DEFAULT NULL COMMENT '性别（枚举限制输入值，避免乱码）',
                                     `birth_date` date DEFAULT NULL COMMENT '出生年月（日期类型，方便按年龄排序）',
                                     `native_place` varchar(100) DEFAULT NULL COMMENT '籍贯（如：湖北省武汉市）',
                                     `political_status` varchar(20) DEFAULT NULL COMMENT '政治面貌（如：中共党员、群众）',
                                     `title` varchar(50) DEFAULT NULL COMMENT '职称（如：工程师、教授）',
                                     `department` varchar(50) DEFAULT NULL COMMENT '部门（如：应用数学）',
                                     `position` varchar(50) DEFAULT NULL COMMENT '职务（如：系主任）',
                                     `current_position` varchar(50) DEFAULT NULL COMMENT '现有岗位（如：前端开发工程师）',
                                     `undergrad_school` varchar(100) DEFAULT NULL COMMENT '本科毕业学校',
                                     `undergrad_time` date DEFAULT NULL COMMENT '本科毕业时间',
                                     `master_school` varchar(100) DEFAULT NULL COMMENT '硕士毕业学校',
                                     `master_time` date DEFAULT NULL COMMENT '硕士毕业时间',
                                     `doctor_school` varchar(100) DEFAULT NULL COMMENT '博士毕业学校',
                                     `doctor_time` date DEFAULT NULL COMMENT '博士毕业时间',
                                     `postdoctoral_experience` varchar(500) DEFAULT NULL COMMENT '博士后经历',
                                     `highest_education` varchar(50) DEFAULT NULL COMMENT '最高学历（如：硕士研究生）',
                                     `highest_education_time` date DEFAULT NULL COMMENT '最高学历获得时间',
                                     `overseas_experience` varchar(500) DEFAULT NULL COMMENT '出国访学经历（限制500字符，支持简要描述）',  -- TEXT→VARCHAR(500)
                                     `research_direction` varchar(500) DEFAULT NULL COMMENT '主要研究方向（限制500字符，支持关键词+简要说明）',  -- TEXT→VARCHAR(500)
                                     `phone` varchar(20) DEFAULT NULL COMMENT '电话（支持国际号码，如+8613800138000）',
                                     `email` varchar(100) DEFAULT NULL COMMENT 'E-mail',
                                     `wechat` varchar(50) DEFAULT NULL COMMENT '微信',
                                     `qq` varchar(20) DEFAULT NULL COMMENT 'QQ号',
                                     `personal_website` varchar(255) DEFAULT NULL COMMENT '个人网站（URL地址）',
                                     `password` varchar(100) DEFAULT NULL COMMENT '加密密码',
                                     `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-待审核，1-通过，2-拒绝）',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
                                     `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
                                     `review_time` datetime DEFAULT NULL COMMENT '审核时间',
                                     `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '工号唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户注册信息表（待审核）';

/**
 * 2. 人员基本信息表
 */
CREATE TABLE `basic_information` (
    -- 1. 核心业务字段（优化长文本为VARCHAR）
#                                      `id` int NOT NULL AUTO_INCREMENT COMMENT '自增主键（唯一标识每条记录）',
                                     `user_id` varchar(50) NOT NULL COMMENT '工号（必填）',
                                     `name` varchar(50) NOT NULL COMMENT '姓名（必填）',
                                     `gender` enum('男','女','其他') DEFAULT NULL COMMENT '性别（枚举限制输入值，避免乱码）',
                                     `birth_date` date DEFAULT NULL COMMENT '出生年月（日期类型，方便按年龄排序）',
                                     `native_place` varchar(100) DEFAULT NULL COMMENT '籍贯（如：湖北省武汉市）',
                                     `political_status` varchar(20) DEFAULT NULL COMMENT '政治面貌（如：中共党员、群众）',
                                     `title` varchar(50) DEFAULT NULL COMMENT '职称（如：工程师、教授）',
                                     `department` varchar(50) DEFAULT NULL COMMENT '部门（如：应用数学）',
                                     `position` varchar(50) DEFAULT NULL COMMENT '职务（如：系主任）',
                                     `current_position` varchar(50) DEFAULT NULL COMMENT '现有岗位（如：前端开发工程师）',
                                     `undergrad_school` varchar(100) DEFAULT NULL COMMENT '本科毕业学校',
                                     `undergrad_time` date DEFAULT NULL COMMENT '本科毕业时间',
                                     `master_school` varchar(100) DEFAULT NULL COMMENT '硕士毕业学校',
                                     `master_time` date DEFAULT NULL COMMENT '硕士毕业时间',
                                     `doctor_school` varchar(100) DEFAULT NULL COMMENT '博士毕业学校',
                                     `doctor_time` date DEFAULT NULL COMMENT '博士毕业时间',
                                     `postdoctoral_experience` varchar(500) DEFAULT NULL COMMENT '博士后经历',
                                     `highest_education` varchar(50) DEFAULT NULL COMMENT '最高学历（如：硕士研究生）',
                                     `highest_education_time` date DEFAULT NULL COMMENT '最高学历获得时间',
                                     `overseas_experience` varchar(500) DEFAULT NULL COMMENT '出国访学经历（限制500字符，支持简要描述）',  -- TEXT→VARCHAR(500)
                                     `research_direction` varchar(500) DEFAULT NULL COMMENT '主要研究方向（限制500字符，支持关键词+简要说明）',  -- TEXT→VARCHAR(500)
                                     `phone` varchar(20) DEFAULT NULL COMMENT '电话（支持国际号码，如+8613800138000）',
                                     `email` varchar(100) DEFAULT NULL COMMENT 'E-mail',
                                     `wechat` varchar(50) DEFAULT NULL COMMENT '微信',
                                     `qq` varchar(20) DEFAULT NULL COMMENT 'QQ号',
                                     `personal_website` varchar(255) DEFAULT NULL COMMENT '个人网站（URL地址）',

    -- 2. 审计字段（跟踪数据创建/修改记录）
                                     `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间（自动填充当前时间）',
                                     `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间（自动更新）',
                                     `created_by` int DEFAULT NULL COMMENT '创建人ID（关联用户表的id）',
                                     `updated_by` int DEFAULT NULL COMMENT '最后更新人ID（关联用户表的id）',

    -- 3. 逻辑删除字段（避免物理删除数据）
                                     `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除（0=未删除，1=已删除）',

    -- 4. 状态控制字段（根据业务需求可选）
                                     `status` tinyint DEFAULT '1' COMMENT '状态（0=未注册，1=已注册）',

    -- 主键和索引
                                     PRIMARY KEY (`user_id`),
                                     KEY `idx_name` (`name`) COMMENT '姓名索引（加速按姓名查询）',
                                     KEY `idx_is_deleted` (`is_deleted`) COMMENT '删除标记索引（查询时过滤已删除数据）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员基本信息表';
