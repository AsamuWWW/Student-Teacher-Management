package com.example.tsm.dto.course;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CourseResp {
    private Long id;
    private String code;
    private String name;
    private String department;
    private String description;
    private BigDecimal credits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}