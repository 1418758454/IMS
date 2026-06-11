package com.ruoyi.manage.mapper.teaching;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.manage.domain.teaching.Competition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 学科竞赛Mapper接口
 * 继承MyBatis-Plus BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface CompetitionMapper extends BaseMapper<Competition> {

    // 查询数据库中所有id为userId的记录，将其用户名改为userName
    @Update("update teaching_competition set user_name = #{userName} where user_id = #{userId}")
    void chageUserName(@Param("userId") String userId, @Param("userName") String userName);
}