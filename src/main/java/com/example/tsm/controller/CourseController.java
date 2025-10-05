package com.example.tsm.controller;

import com.example.tsm.common.api.ApiResponse;
import com.example.tsm.common.api.PageResult;
import com.example.tsm.dto.course.CourseCreateReq;
import com.example.tsm.dto.course.CourseResp;
import com.example.tsm.dto.course.CourseUpdateReq;
import com.example.tsm.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course", description = "课程管理")
public class CourseController {

    private final CourseService courseService;
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "分页查询课程")
    @GetMapping
    public ApiResponse<PageResult<CourseResp>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @ParameterObject Pageable pageable
    ) {
        return ApiResponse.ok(courseService.page(keyword, department, pageable));
    }

    @Operation(summary = "课程详情")
    @GetMapping("/{id}")
    public ApiResponse<CourseResp> get(@PathVariable Long id) {
        return ApiResponse.ok(courseService.get(id));
    }

    @Operation(summary = "新增课程")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CourseResp> create(@Valid @RequestBody CourseCreateReq req) throws DuplicateKeyException {
        return ApiResponse.ok(courseService.create(req));
    }

    @Operation(summary = "编辑课程")
    @PutMapping("/{id}")
    public ApiResponse<CourseResp> update(@PathVariable Long id, @Valid @RequestBody CourseUpdateReq req) {
        return ApiResponse.ok(courseService.update(id, req));
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ApiResponse.okMsg("deleted");
    }
}