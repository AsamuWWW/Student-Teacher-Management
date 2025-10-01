package com.example.tsm.dto.student;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentResp {
    private Long id;
    private String code;
    private String name;
    private String email;
    private String phone;
    private String grade;
    private String major;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}