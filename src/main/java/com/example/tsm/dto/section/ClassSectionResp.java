package com.example.tsm.dto.section;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassSectionResp {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long teacherId;
    private String teacherCode;
    private String teacherName;
    private String term;
    private String sectionCode;
    private Integer capacity;
    private String schedule;
    private String classroom;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}