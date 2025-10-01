package com.example.tsm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "class_section", uniqueConstraints = {
        @UniqueConstraint(name = "uk_section_course_term_code", columnNames = {"course_id", "term", "section_code"})
})
@Getter
@Setter
public class ClassSection extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_course"))
    private Course course;

    @Column(name = "section_code", nullable = false, length = 16)
    private String sectionCode; // 教学班号（如 01）

    @Column(name = "term", nullable = false, length = 32)
    private String term; // 学期（如 2024-2025-1）

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_teacher"))
    private Teacher teacher;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "schedule", length = 128)
    private String schedule;

    @Column(name = "classroom", length = 64)
    private String classroom;

    @Column(name = "remark", length = 255)
    private String remark;
}