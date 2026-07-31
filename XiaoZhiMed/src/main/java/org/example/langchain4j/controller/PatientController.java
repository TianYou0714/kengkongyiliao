package org.example.langchain4j.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.langchain4j.common.Result;
import org.example.langchain4j.entity.Patient;
import org.example.langchain4j.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "患者管理")
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Operation(summary = "分页查询患者")
    @GetMapping("/page")
    public Result<Page<Patient>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Patient::getName, keyword)
                    .or()
                    .like(Patient::getPhone, keyword)
                    .or()
                    .like(Patient::getIdCard, keyword);
        }
        wrapper.orderByDesc(Patient::getId);
        return Result.success(patientService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @Operation(summary = "根据ID查询患者")
    @GetMapping("/{id}")
    public Result<Patient> getById(@PathVariable Long id) {
        return Result.success(patientService.getById(id));
    }

    @Operation(summary = "新增患者")
    @PostMapping
    public Result<Boolean> add(@RequestBody Patient patient) {
        return Result.success(patientService.save(patient));
    }

    @Operation(summary = "修改患者")
    @PutMapping
    public Result<Boolean> update(@RequestBody Patient patient) {
        return Result.success(patientService.updateById(patient));
    }

    @Operation(summary = "删除患者")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(patientService.removeById(id));
    }
}
