package org.example.langchain4j.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.langchain4j.entity.Doctor;
import org.example.langchain4j.mapper.DoctorMapper;
import org.example.langchain4j.service.DoctorService;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {
}
