package com.example.tsm.dto.enrollment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EnrollmentResp {
    private Long id;
    private Long studentId;
    private String studentCode;
    private String studentName;

    private Long classSectionId;
    private String sectionCode;
    private String term;

    private Long courseId;
    private String courseCode;
    private String courseName;

    private Long teacherId;
    private String teacherCode;
    private String teacherName;

    private LocalDateTime enrolledAt;
    private BigDecimal grade;
}