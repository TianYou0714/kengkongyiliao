package org.example.langchain4j.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.langchain4j.entity.Doctor;

/**
 * 医生信息表 Mapper 接口
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

}
