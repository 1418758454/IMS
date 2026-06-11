package com.ruoyi.manage.mapper.teaching;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.manage.domain.teaching.TeachingTotalWorkload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;


/**
 * 教学总工作量Mapper接口
 */
@Mapper
public interface TeachingTotalWorkloadMapper extends BaseMapper<TeachingTotalWorkload> {

    // 插入id和用户名到教学总工作量表
    @Update("INSERT INTO teaching_total_workload(user_id, user_name) VALUES (#{userId}, #{name})")
    void addTeachingWorkload(@Param("userId") String userId, @Param("name") String name);

    // 根据用户id和授课年份更新理论课总工作量
    @Update("UPDATE teaching_total_workload SET theory_course_workload = #{totalWorkload} WHERE user_id = #{userId} AND year = #{year}")
    void updateTheoryById(@Param("userId") Long userId, @Param("totalWorkload") BigDecimal totalWorkload, @Param("year") Integer year);


}
