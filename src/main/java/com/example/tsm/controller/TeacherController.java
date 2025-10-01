package com.example.tsm.controller;

import com.example.tsm.common.api.ApiResponse;
import com.example.tsm.common.api.PageResult;
import com.example.tsm.dto.teacher.TeacherCreateReq;
import com.example.tsm.dto.teacher.TeacherResp;
import com.example.tsm.dto.teacher.TeacherUpdateReq;
import com.example.tsm.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers")
@Tag(name = "Teacher", description = "教师管理")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @Operation(summary = "分页查询教师")
    @GetMapping
    public ApiResponse<PageResult<TeacherResp>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @ParameterObject Pageable pageable
    ) {
        return ApiResponse.ok(teacherService.page(keyword, department, pageable));
    }

    @Operation(summary = "教师详情")
    @GetMapping("/{id}")
    public ApiResponse<TeacherResp> get(@PathVariable Long id) {
        return ApiResponse.ok(teacherService.get(id));
    }

    @Operation(summary = "新增教师")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TeacherResp> create(@Valid @RequestBody TeacherCreateReq req) throws DuplicateKeyException {
        return ApiResponse.ok(teacherService.create(req));
    }

    @Operation(summary = "编辑教师")
    @PutMapping("/{id}")
    public ApiResponse<TeacherResp> update(@PathVariable Long id, @Valid @RequestBody TeacherUpdateReq req) {
        return ApiResponse.ok(teacherService.update(id, req));
    }

    @Operation(summary = "删除教师")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        teacherService.delete(id);
    }
}