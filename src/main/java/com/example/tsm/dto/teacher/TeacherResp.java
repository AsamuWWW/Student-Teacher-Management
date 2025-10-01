package com.example.tsm.dto.teacher;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeacherResp {
    private Long id;
    private String code;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String title;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}