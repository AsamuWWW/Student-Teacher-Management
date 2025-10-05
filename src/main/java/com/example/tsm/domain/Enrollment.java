package com.example.tsm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "enrollment",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_enrollment_student_section",
                   columnNames = {"student_id", "class_section_id"})
       },
       indexes = {
           @Index(name = "idx_enr_section", columnList = "class_section_id"),
           @Index(name = "idx_enr_student", columnList = "student_id")
       })
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 学生
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_enr_student"))
    private Student student;

    // 教学班
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_section_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_enr_section"))
    private ClassSection classSection;

    @CreationTimestamp
    private LocalDateTime enrolledAt;

    // 成绩（可选）
    @Column(precision = 5, scale = 2)
    private BigDecimal grade;
}