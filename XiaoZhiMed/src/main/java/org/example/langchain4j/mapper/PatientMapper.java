package org.example.langchain4j.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.langchain4j.entity.Patient;

/**
 * 患者信息表 Mapper 接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

}
