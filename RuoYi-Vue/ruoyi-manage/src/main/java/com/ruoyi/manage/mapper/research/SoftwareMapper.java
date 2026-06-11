package com.ruoyi.manage.mapper.research;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.manage.domain.research.ResearchSoftware;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SoftwareMapper extends BaseMapper<ResearchSoftware> {

    // 查询数据库中所有id为userId的记录，将其用户名改为userName
    @Update("update research_software set user_name = #{userName} where user_id = #{userId}")
    void chageUserName(@Param("userId") String userId, @Param("userName") String userName);
}
