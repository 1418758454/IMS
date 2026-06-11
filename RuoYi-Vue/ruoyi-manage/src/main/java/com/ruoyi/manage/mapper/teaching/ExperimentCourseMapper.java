package com.ruoyi.manage.mapper.teaching;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.manage.domain.teaching.ExperimentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 本科实验课Mapper接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface ExperimentCourseMapper extends BaseMapper<ExperimentCourse> {

    // 查询数据库中所有id为userId的记录，将其用户名改为userName
    @Update("update teaching_undergraduate_experiment_course set user_name = #{userName} where user_id = #{userId}")
    void chageUserName(@Param("userId") String userId, @Param("userName") String userName);
}