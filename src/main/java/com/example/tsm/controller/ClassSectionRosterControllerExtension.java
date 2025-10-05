package com.example.tsm.controller;

import com.example.tsm.common.api.ApiResponse;
import com.example.tsm.common.api.PageResult;
import com.example.tsm.dto.enrollment.EnrollmentResp;
import com.example.tsm.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sections/{sectionId}/enrollments")
@Tag(name = "ClassSectionRoster", description = "教学班花名册")
public class ClassSectionRosterControllerExtension {

    private final EnrollmentService enrollmentService;

    public ClassSectionRosterControllerExtension(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "教学班花名册（分页）")
    @GetMapping
    public ApiResponse<PageResult<EnrollmentResp>> roster(@PathVariable Long sectionId,
                                                          @ParameterObject Pageable pageable) {
        return ApiResponse.ok(
                enrollmentService.page(null, sectionId, null, null, null, null, pageable)
        );
    }
}