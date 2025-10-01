package com.example.tsm.controller;

import com.example.tsm.common.api.ApiResponse;
import com.example.tsm.common.api.PageResult;
import com.example.tsm.dto.student.StudentCreateReq;
import com.example.tsm.dto.student.StudentResp;
import com.example.tsm.dto.student.StudentUpdateReq;
import com.example.tsm.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Student", description = "学生管理")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "分页查询学生")
    @GetMapping
    public ApiResponse<PageResult<StudentResp>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String major,
            @ParameterObject Pageable pageable
    ) {
        return ApiResponse.ok(studentService.page(keyword, grade, major, pageable));
    }

    @Operation(summary = "学生详情")
    @GetMapping("/{id}")
    public ApiResponse<StudentResp> get(@PathVariable Long id) {
        return ApiResponse.ok(studentService.get(id));
    }

    @Operation(summary = "新增学生")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StudentResp> create(@Valid @RequestBody StudentCreateReq req) {
        return ApiResponse.ok(studentService.create(req));
    }

    @Operation(summary = "编辑学生")
    @PutMapping("/{id}")
    public ApiResponse<StudentResp> update(@PathVariable Long id, @Valid @RequestBody StudentUpdateReq req) {
        return ApiResponse.ok(studentService.update(id, req));
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}