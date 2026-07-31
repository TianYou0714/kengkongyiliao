package org.example.langchain4j.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.langchain4j.entity.Patient;
import org.example.langchain4j.mapper.PatientMapper;
import org.example.langchain4j.service.PatientService;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
}
