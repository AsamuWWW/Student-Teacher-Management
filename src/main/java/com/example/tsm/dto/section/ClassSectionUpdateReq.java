package com.example.tsm.dto.section;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClassSectionUpdateReq {

    @NotNull
    private Long courseId;

    @NotNull
    private Long teacherId;

    @NotBlank
    @Size(max = 32)
    private String term;

    @NotBlank
    @Size(max = 16)
    private String sectionCode;

    @NotNull
    @Min(1)
    private Integer capacity;

    @Size(max = 128)
    private String schedule;

    @Size(max = 64)
    private String classroom;

    @Size(max = 255)
    private String remark;
}