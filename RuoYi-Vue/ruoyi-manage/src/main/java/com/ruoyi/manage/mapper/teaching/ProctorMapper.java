package com.ruoyi.manage.mapper.teaching;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.manage.domain.teaching.Proctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 监考模块Mapper接口
 */
@Mapper
public interface ProctorMapper extends BaseMapper<Proctor> {

    // 查询数据库中所有id为userId的记录，将其用户名改为userName
    @Update("update teaching_proctor set user_name = #{userName} where user_id = #{userId}")
    void chageUserName(@Param("userId") String userId, @Param("userName") String userName);
}