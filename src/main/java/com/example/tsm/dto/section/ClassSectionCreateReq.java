package com.example.tsm.dto.section;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClassSectionCreateReq {

    @NotNull
    private Long courseId;

    @NotNull
    private Long teacherId;

    @NotBlank
    @Size(max = 32)
    private String term;          // 学期，例如 2024-2025-1

    @NotBlank
    @Size(max = 16)
    private String sectionCode;   // 教学班号（如 01）

    @NotNull
    @Min(1)
    private Integer capacity;     // 容量

    @Size(max = 128)
    private String schedule;      // 上课时间描述

    @Size(max = 64)
    private String classroom;     // 教室

    @Size(max = 255)
    private String remark;
}