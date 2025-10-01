package com.example.tsm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "teacher", uniqueConstraints = {
        @UniqueConstraint(name = "uk_teacher_code", columnNames = {"code"})
})
@Getter
@Setter
public class Teacher extends BaseEntity {

    @Column(name = "code", nullable = false, length = 32)
    private String code; // 工号

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "department", length = 64)
    private String department;

    @Column(name = "title", length = 64)
    private String title;

    @Column(name = "remark", length = 255)
    private String remark;
}