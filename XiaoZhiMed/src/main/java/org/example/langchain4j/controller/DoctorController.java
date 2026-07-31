package org.example.langchain4j.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.langchain4j.common.Result;
import org.example.langchain4j.entity.Doctor;
import org.example.langchain4j.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "医生管理")
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Operation(summary = "分页查询医生")
    @GetMapping("/page")
    public Result<Page<Doctor>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Long departmentId) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Doctor::getName, keyword)
                    .or()
                    .like(Doctor::getSpecialty, keyword);
        }
        if (departmentId != null) {
            wrapper.eq(Doctor::getDepartmentId, departmentId);
        }
        wrapper.orderByDesc(Doctor::getId);
        return Result.success(doctorService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @Operation(summary = "查询全部医生")
    @GetMapping("/list")
    public Result<List<Doctor>> list() {
        return Result.success(doctorService.list());
    }

    @Operation(summary = "根据ID查询医生")
    @GetMapping("/{id}")
    public Result<Doctor> getById(@PathVariable Long id) {
        return Result.success(doctorService.getById(id));
    }

    @Operation(summary = "新增医生")
    @PostMapping
    public Result<Boolean> add(@RequestBody Doctor doctor) {
        return Result.success(doctorService.save(doctor));
    }

    @Operation(summary = "修改医生")
    @PutMapping
    public Result<Boolean> update(@RequestBody Doctor doctor) {
        return Result.success(doctorService.updateById(doctor));
    }

    @Operation(summary = "删除医生")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(doctorService.removeById(id));
    }
}
