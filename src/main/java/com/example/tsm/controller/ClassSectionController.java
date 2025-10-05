package com.example.tsm.controller;

import com.example.tsm.common.api.ApiResponse;
import com.example.tsm.common.api.PageResult;
import com.example.tsm.dto.section.ClassSectionCreateReq;
import com.example.tsm.dto.section.ClassSectionResp;
import com.example.tsm.dto.section.ClassSectionUpdateReq;
import com.example.tsm.service.ClassSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sections")
@Tag(name = "ClassSection", description = "教学班管理")
public class ClassSectionController {

    private final ClassSectionService classSectionService;

    public ClassSectionController(ClassSectionService classSectionService) {
        this.classSectionService = classSectionService;
    }

    @Operation(summary = "分页查询教学班")
    @GetMapping
    public ApiResponse<PageResult<ClassSectionResp>> page(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) String keyword,
            @ParameterObject Pageable pageable
    ) {
        return ApiResponse.ok(classSectionService.page(courseId, teacherId, term, keyword, pageable));
    }

    @Operation(summary = "教学班详情")
    @GetMapping("/{id}")
    public ApiResponse<ClassSectionResp> get(@PathVariable Long id) {
        return ApiResponse.ok(classSectionService.get(id));
    }

    @Operation(summary = "新增教学班")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ClassSectionResp> create(@Valid @RequestBody ClassSectionCreateReq req) throws DuplicateKeyException {
        return ApiResponse.ok(classSectionService.create(req));
    }

    @Operation(summary = "编辑教学班")
    @PutMapping("/{id}")
    public ApiResponse<ClassSectionResp> update(@PathVariable Long id, @Valid @RequestBody ClassSectionUpdateReq req) {
        return ApiResponse.ok(classSectionService.update(id, req));
    }

    @Operation(summary = "删除教学班")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        classSectionService.delete(id);
        return ApiResponse.okMsg("deleted");
    }
}