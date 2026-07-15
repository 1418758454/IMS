-- 科研、教学工作量汇总重建迁移脚本。
-- 执行前请确认已备份数据库。本脚本只清空并重建两张汇总表，不修改任何业务明细表。
-- 预计工作量统计全部状态；确认工作量仅统计“已通过”；教学学科竞赛按年度分别封顶 200。

START TRANSACTION;

DELETE FROM research_total_workload;
DELETE FROM teaching_total_workload;

INSERT INTO research_total_workload (
    user_id,
    user_name,
    `year`,
    subject_estimated_workload,
    subject_confirmed_workload,
    paper_estimated_workload,
    paper_confirmed_workload,
    monograph_estimated_workload,
    monograph_confirmed_workload,
    award_estimated_workload,
    award_confirmed_workload,
    patent_estimated_workload,
    patent_confirmed_workload,
    software_estimated_workload,
    software_confirmed_workload,
    total_workload
)
SELECT
    detail.user_id,
    COALESCE(MAX(detail.user_name), ''),
    LEFT(TRIM(CAST(detail.`year` AS CHAR)), 4),
    COALESCE(SUM(CASE WHEN detail.module_name = 'subject' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'subject' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'paper' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'paper' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'monograph' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'monograph' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'award' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'award' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'patent' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'patent' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'software' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'software' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.status = '已通过' THEN detail.workload ELSE 0 END), 0)
FROM (
    SELECT user_id, user_name, `year`, COALESCE(workload, 0) AS workload, status, 'subject' AS module_name
    FROM research_subject
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'paper'
    FROM research_paper
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'monograph'
    FROM research_monograph
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'award'
    FROM research_award
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'patent'
    FROM research_patent
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'software'
    FROM research_software
) detail
WHERE detail.user_id IS NOT NULL
  AND detail.`year` IS NOT NULL
  AND TRIM(detail.`year`) <> ''
  AND TRIM(CAST(detail.`year` AS CHAR)) REGEXP '^[0-9]{4}'
GROUP BY detail.user_id, LEFT(TRIM(CAST(detail.`year` AS CHAR)), 4);

INSERT INTO teaching_total_workload (
    user_id,
    user_name,
    `year`,
    theory_course_estimated_workload,
    theory_course_confirmed_workload,
    experiment_course_estimated_workload,
    experiment_course_confirmed_workload,
    practical_teaching_estimated_workload,
    practical_teaching_confirmed_workload,
    thesis_course_estimated_workload,
    thesis_course_confirmed_workload,
    science_innovation_estimated_workload,
    science_innovation_confirmed_workload,
    competition_estimated_workload,
    competition_confirmed_workload,
    textbook_estimated_workload,
    textbook_confirmed_workload,
    education_reform_estimated_workload,
    education_reform_confirmed_workload,
    education_reform_paper_estimated_workload,
    education_reform_paper_confirmed_workload,
    proctor_estimated_workload,
    proctor_confirmed_workload,
    graduate_theory_course_estimated_workload,
    graduate_theory_course_confirmed_workload,
    graduate_guide_student_estimated_workload,
    graduate_guide_student_confirmed_workload,
    total_workload
)
SELECT
    detail.user_id,
    COALESCE(MAX(detail.user_name), ''),
    detail.`year`,
    COALESCE(SUM(CASE WHEN detail.module_name = 'theory_course' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'theory_course' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'experiment_course' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'experiment_course' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'practical_teaching' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'practical_teaching' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'thesis_course' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'thesis_course' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'science_innovation' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'science_innovation' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    LEAST(200, COALESCE(SUM(CASE WHEN detail.module_name = 'competition' THEN detail.workload ELSE 0 END), 0)),
    LEAST(200, COALESCE(SUM(CASE WHEN detail.module_name = 'competition' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0)),
    COALESCE(SUM(CASE WHEN detail.module_name = 'textbook' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'textbook' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'education_reform' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'education_reform' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'education_reform_paper' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'education_reform_paper' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'proctor' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'proctor' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'graduate_theory_course' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'graduate_theory_course' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'graduate_guide_student' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE WHEN detail.module_name = 'graduate_guide_student' AND detail.status = '已通过' THEN detail.workload ELSE 0 END), 0),
    COALESCE(SUM(CASE
        WHEN detail.status = '已通过' AND detail.module_name <> 'competition' THEN detail.workload
        ELSE 0
    END), 0)
    + LEAST(200, COALESCE(SUM(CASE
        WHEN detail.status = '已通过' AND detail.module_name = 'competition' THEN detail.workload
        ELSE 0
    END), 0))
FROM (
    SELECT user_id, user_name, `year`, COALESCE(workload, 0) AS workload, status, 'theory_course' AS module_name
    FROM teaching_undergraduate_theory_course
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'experiment_course'
    FROM teaching_undergraduate_experiment_course
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'practical_teaching'
    FROM teaching_undergraduate_practice_course
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'thesis_course'
    FROM undergraduate_thesis_course
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'science_innovation'
    FROM teaching_science_innovation
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'competition'
    FROM teaching_competition
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'textbook'
    FROM teaching_textbook
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'education_reform'
    FROM teaching_education_reform
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'education_reform_paper'
    FROM teaching_education_reform_paper
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'proctor'
    FROM teaching_proctor
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'graduate_theory_course'
    FROM teaching_graduate_theory_course
    UNION ALL
    SELECT user_id, user_name, `year`, COALESCE(workload, 0), status, 'graduate_guide_student'
    FROM teaching_graduate_guide_student
) detail
WHERE detail.user_id IS NOT NULL
  AND detail.`year` IS NOT NULL
GROUP BY detail.user_id, detail.`year`;

COMMIT;

-- 为统一刷新使用的 ON DUPLICATE KEY UPDATE 增加唯一键；重复执行时自动跳过已存在索引。
SET @research_index_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'research_total_workload'
      AND index_name = 'uk_research_total_user_year'
);
SET @research_index_sql = IF(
    @research_index_exists = 0,
    'ALTER TABLE research_total_workload ADD UNIQUE KEY uk_research_total_user_year (user_id, `year`)',
    'SELECT 1'
);
PREPARE research_index_statement FROM @research_index_sql;
EXECUTE research_index_statement;
DEALLOCATE PREPARE research_index_statement;

SET @teaching_index_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'teaching_total_workload'
      AND index_name = 'uk_teaching_total_user_year'
);
SET @teaching_index_sql = IF(
    @teaching_index_exists = 0,
    'ALTER TABLE teaching_total_workload ADD UNIQUE KEY uk_teaching_total_user_year (user_id, `year`)',
    'SELECT 1'
);
PREPARE teaching_index_statement FROM @teaching_index_sql;
EXECUTE teaching_index_statement;
DEALLOCATE PREPARE teaching_index_statement;
