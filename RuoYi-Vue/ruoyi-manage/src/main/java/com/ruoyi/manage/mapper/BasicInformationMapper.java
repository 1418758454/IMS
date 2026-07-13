package com.ruoyi.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.manage.domain.BasicInformation;
import com.ruoyi.manage.domain.RegisterInformation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BasicInformationMapper extends BaseMapper<BasicInformation> {
    @Select("insert into sys_user (user_name, password) values (#{userId}, #{password})")
    void insertUser(@Param("userId") String userId, @Param("password") String password);

    @Insert("insert into user_registration (user_Id, name, gender, birth_date, native_place, political_status,title, current_position, undergrad_school, undergrad_time, master_school, master_time, doctor_school, doctor_time, postdoctoral_experience, highest_education, highest_education_time, overseas_experience, research_direction, phone, email, wechat, qq, personal_website, password, status, department, position) values (#{userId}, #{name}, #{gender}, #{birthDate}, #{nativePlace}, #{politicalStatus}, #{title}, #{currentPosition}, #{undergradSchool}, #{undergradTime}, #{masterSchool}, #{masterTime}, #{doctorSchool}, #{doctorTime}, #{postdoctoralExperience}, #{highestEducation}, #{highestEducationTime}, #{overseasExperience}, #{researchDirection}, #{phone}, #{email}, #{wechat}, #{qq}, #{personalWebsite}, #{password}, #{status}, #{department}, #{position})")
    void insertRegisterinfo(RegisterInformation registerInfo);

    //查看是否已经有对应工号
    @Select("select count(*) from user_registration where user_id = #{userId}")
    int checkStatus(String userId);
}
