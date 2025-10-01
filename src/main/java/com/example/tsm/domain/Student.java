package com.example.tsm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student", uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_code", columnNames = {"code"})
})
@Getter
@Setter
public class Student extends BaseEntity {

    @Column(name = "code", nullable = false, length = 32)
    private String code; // 学号

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "grade", length = 16)
    private String grade;

    @Column(name = "major", length = 64)
    private String major;

    @Column(name = "remark", length = 255)
    private String remark;
}