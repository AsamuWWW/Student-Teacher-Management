package com.example.tsm.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseUpdateReq {

    @NotBlank
    @Size(max = 32)
    private String code;

    @NotBlank
    @Size(max = 128)
    private String name;

    @Size(max = 64)
    private String department;

    @Size(max = 1024)
    private String description;

    @Size(max = 16)
    private String credits;
}