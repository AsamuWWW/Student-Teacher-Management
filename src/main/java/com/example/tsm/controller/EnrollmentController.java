package com.example.tsm.controller;

import com.example.tsm.common.api.ApiResponse;
import com.example.tsm.common.api.PageResult;
import com.example.tsm.dto.enrollment.EnrollmentCreateReq;
import com.example.tsm.dto.enrollment.EnrollmentGradeUpdateReq;
import com.example.tsm.dto.enrollment.EnrollmentResp;
import com.example.tsm.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(name = "Enrollment", description = "选课管理")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "分页查询选课记录（综合过滤）")
    @GetMapping
    public ApiResponse<PageResult<EnrollmentResp>> page(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long classSectionId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String keyword,
            @ParameterObject Pageable pageable
    ) {
        return ApiResponse.ok(enrollmentService.page(studentId, classSectionId, courseId, teacherId, term, keyword, pageable));
    }

    @Operation(summary = "选课（学生加入教学班）")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EnrollmentResp> enroll(@Valid @RequestBody EnrollmentCreateReq req) throws DuplicateKeyException {
        return ApiResponse.ok(enrollmentService.enroll(req));
    }

    @Operation(summary = "选课详情")
    @GetMapping("/{id}")
    public ApiResponse<EnrollmentResp> get(@PathVariable Long id) {
        return ApiResponse.ok(enrollmentService.get(id));
    }

    @Operation(summary = "退选")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> drop(@PathVariable Long id) {
        enrollmentService.drop(id);
        return ApiResponse.okMsg("deleted");
    }

    @Operation(summary = "更新成绩")
    @PutMapping("/{id}/grade")
    public ApiResponse<EnrollmentResp> updateGrade(@PathVariable Long id,
                                                   @Valid @RequestBody EnrollmentGradeUpdateReq req) {
        return ApiResponse.ok(enrollmentService.updateGrade(id, req));
    }
}