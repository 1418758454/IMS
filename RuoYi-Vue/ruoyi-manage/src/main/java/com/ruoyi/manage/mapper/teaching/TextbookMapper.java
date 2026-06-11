package com.ruoyi.manage.mapper.teaching;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.manage.domain.teaching.Textbook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 出版教材Mapper接口（与科技创新Mapper结构完全一致）
 */
@Mapper
public interface TextbookMapper extends BaseMapper<Textbook> {

    // 查询数据库中所有id为userId的记录，将其用户名改为userName
    @Update("update teaching_textbook set user_name = #{userName} where user_id = #{userId}")
    void chageUserName(@Param("userId") String userId, @Param("userName") String userName);
}