package com.example.tsm.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseCreateReq {

    @NotBlank
    @Size(max = 32)
    private String code;          // 课程编码（唯一）

    @NotBlank
    @Size(max = 128)
    private String name;          // 课程名称

    @Size(max = 64)
    private String department;    // 开课院系

    @Size(max = 1024)
    private String description;   // 描述

    // 可选：学分（简单用字符串或数字，这里用字符串再后续扩展为 BigDecimal DTO->Entity 转换）
    @Size(max = 16)
    private String credits;
}