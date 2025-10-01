package com.example.tsm.repository;

import com.example.tsm.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndSectionId(Long studentId, Long sectionId);
    long countBySectionId(Long sectionId);
}