-- 注册表测试数据 1：待审核状态（status=0，完整字段示例）
INSERT INTO user_registration (
    user_id, name, gender, birth_date, native_place, political_status,
    title, current_position, undergrad_school, undergrad_time, highest_education,
    highest_education_time, overseas_experience, research_direction, phone, email,
    wechat, qq, personal_website, status, password, create_time
) VALUES (
             '202433173', '张三', '男', '1990-01-15', '山东济南', '中共党员',
             '副教授', '计算机系教师', '山东大学', '2012-06-30', '硕士',
             '2015-03-20', '2018.09-2019.09 美国斯坦福大学访学', '人工智能', '13800138001', 'zhangsan@example.com',
             'zhangsan_wechat', '123456', 'https://zhangsan.example.com', 0, 'Password123!', '2025-09-01 09:30:00'
         );

USE 'ry-vue';
-- 科研表测试数据
INSERT INTO research_subject (
    user_id, project_name, subject_type, execute_time_start, execute_time_end,
    `rank`, coefficient, pdf_url, workload
) VALUES (
             20243170112,  -- user_id：假设关联用户ID=1
             '深度学习',  -- 项目名称
             '国家自然基金',  -- 课题类型
             '2023-01-01',  -- 执行开始时间
             '2025-12-31',  -- 执行结束时间
             1,  -- 排名
             1.0,  -- 系数
             'https://example.com/pdfs/subject1.pdf',  -- PDF文件URL（示例）
             100.00  -- 工作量
         );

INSERT INTO research_paper (
    user_id, title, journal, publish_time, level, `rank`, pdf_url, workload
) VALUES (
             1,  -- user_id：关联用户ID=1
             '深度学习在医学影像分析中的应用进展',  -- 论文名称
             '计算机学报',  -- 出版刊物
             '2024-03-15',  -- 出版时间
             'SCI一区',  -- 论文级别
             1,  -- 排名
             'https://example.com/pdfs/paper1.pdf',  -- PDF文件URL（示例）
             150.00  -- 工作量
         );

INSERT INTO research_monograph (
    user_id, title, publisher, publish_time, `rank`, pdf_url, workload
) VALUES (
             1,  -- user_id：关联用户ID=1
             '人工智能算法导论（第二版）',  -- 论著名称
             '高等教育出版社',  -- 出版社
             '2024-05-20',  -- 出版时间
             1,  -- 排名
             'https://example.com/pdfs/monograph1.pdf',  -- PDF文件URL（示例）
             80.00  -- 工作量
         );

INSERT INTO research_award (
    user_id, name, organizer, award_time, level, `rank`, pdf_url, workload
) VALUES (
             1,  -- user_id：关联用户ID=1
             '2024年度国家级科技进步一等奖',  -- 获奖名称
             '中华人民共和国科学技术部',  -- 颁奖单位
             '2024-01-10',  -- 获奖时间
             '国家级',  -- 奖励级别
             1,  -- 排名
             'https://example.com/pdfs/award1.pdf',  -- PDF文件URL（示例）
             200.00  -- 工作量
         );

INSERT INTO research_patent (
    user_id, name, type, apply_time, authorize_time, `rank`, pdf_url, workload
) VALUES (
             1,  -- user_id：关联用户ID=1
             '一种基于区块链的分布式数据存储方法',  -- 专利名称
             '发明专利',  -- 专利类型
             '2023-05-20',  -- 申请时间
             '2024-02-15',  -- 授权时间
             1,  -- 排名
             'https://example.com/pdfs/patent1.pdf',  -- PDF文件URL（示例）
             60.00  -- 工作量
         );

INSERT INTO research_software (
    user_id, name, apply_time, authorize_time, `rank`, pdf_url, workload
) VALUES (
             1,  -- user_id：关联用户ID=1
             '科研项目管理系统V1.0',  -- 软著名称
             '2024-03-10',  -- 申请时间
             '2024-07-05',  -- 授权时间
             1,  -- 排名
             'https://example.com/pdfs/software1.pdf',  -- PDF文件URL（示例）
             30.00  -- 工作量
         );

USE 'ry-vue';
INSERT INTO teaching_undergraduate_theory_course (
    course_name,        -- 课程名称
    student_class,      -- 授课学生（班级）
    course_type,        -- 课程类型（compulsory/elective/general/professional）
    theory_hours,       -- 理论学时
    student_count,      -- 学生人数
    workload,           -- 工作量（小数，保留2位）
    year,               -- 授课年份
    user_id             -- 关联用户ID（测试用例用户ID=1）
) VALUES (
             '高等数学',         -- 课程名称
             '2023级计算机科学与技术1班',  -- 授课学生
             'compulsory',       -- 课程类型：必修课
             64,                 -- 理论学时
             50,                 -- 学生人数
             32.00,              -- 工作量（64学时×50人×系数，此处简化为32.00）
             2024,               -- 授课年份
             20243170112                   -- 用户ID（测试账号）
         );