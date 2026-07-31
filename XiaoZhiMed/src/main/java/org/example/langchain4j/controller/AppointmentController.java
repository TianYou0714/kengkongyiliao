package org.example.langchain4j.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.langchain4j.common.Result;
import org.example.langchain4j.entity.Appointment;
import org.example.langchain4j.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预约挂号管理")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Operation(summary = "分页查询预约")
    @GetMapping("/page")
    public Result<Page<Appointment>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Appointment::getUsername, keyword)
                    .or()
                    .like(Appointment::getDepartment, keyword)
                    .or()
                    .like(Appointment::getDoctorName, keyword);
        }
        wrapper.orderByDesc(Appointment::getId);
        return Result.success(appointmentService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @Operation(summary = "根据ID查询预约")
    @GetMapping("/{id}")
    public Result<Appointment> getById(@PathVariable Long id) {
        return Result.success(appointmentService.getById(id));
    }

    @Operation(summary = "新增预约")
    @PostMapping
    public Result<Boolean> add(@RequestBody Appointment appointment) {
        return Result.success(appointmentService.save(appointment));
    }

    @Operation(summary = "修改预约")
    @PutMapping
    public Result<Boolean> update(@RequestBody Appointment appointment) {
        return Result.success(appointmentService.updateById(appointment));
    }

    @Operation(summary = "取消预约")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(appointmentService.removeById(id));
    }
}
