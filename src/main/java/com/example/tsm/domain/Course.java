package com.example.tsm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "course", uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_code", columnNames = {"code"})
})
@Getter
@Setter
public class Course extends BaseEntity {

    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "credits")
    private BigDecimal credits;

    @Column(name = "department", length = 64)
    private String department;

    @Column(name = "description", length = 1024)
    private String description;
}