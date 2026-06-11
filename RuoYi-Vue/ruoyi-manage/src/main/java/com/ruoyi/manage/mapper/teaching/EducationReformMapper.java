package com.ruoyi.manage.mapper.teaching;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.manage.domain.teaching.EducationReform;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 教改项目Mapper接口（与科技创新Mapper结构一致）
 */
@Mapper
public interface EducationReformMapper extends BaseMapper<EducationReform> {

    // 查询数据库中所有id为userId的记录，将其用户名改为userName
    @Update("update teaching_education_reform set user_name = #{userName} where user_id = #{userId}")
    void chageUserName(@Param("userId") String userId, @Param("userName") String userName);
}