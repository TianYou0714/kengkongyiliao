package org.example.langchain4j.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.langchain4j.entity.Department;
import org.example.langchain4j.mapper.DepartmentMapper;
import org.example.langchain4j.service.DepartmentService;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
}
