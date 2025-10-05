package com.example.tsm.dto.enrollment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentCreateReq {

    @NotNull
    private Long studentId;

    @NotNull
    private Long classSectionId;
}