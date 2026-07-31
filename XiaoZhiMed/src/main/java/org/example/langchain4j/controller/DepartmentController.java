package org.example.langchain4j.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.langchain4j.common.Result;
import org.example.langchain4j.entity.Department;
import org.example.langchain4j.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "科室管理")
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Operation(summary = "分页查询科室")
    @GetMapping("/page")
    public Result<Page<Department>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Department::getName, keyword)
                    .or()
                    .like(Department::getDescription, keyword);
        }
        wrapper.orderByDesc(Department::getId);
        return Result.success(departmentService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @Operation(summary = "查询全部科室")
    @GetMapping("/list")
    public Result<List<Department>> list() {
        return Result.success(departmentService.list());
    }

    @Operation(summary = "根据ID查询科室")
    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        return Result.success(departmentService.getById(id));
    }

    @Operation(summary = "新增科室")
    @PostMapping
    public Result<Boolean> add(@RequestBody Department department) {
        return Result.success(departmentService.save(department));
    }

    @Operation(summary = "修改科室")
    @PutMapping
    public Result<Boolean> update(@RequestBody Department department) {
        return Result.success(departmentService.updateById(department));
    }

    @Operation(summary = "删除科室")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(departmentService.removeById(id));
    }
}
