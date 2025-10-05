package com.example.tsm.repository;

import com.example.tsm.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
    boolean existsByStudentIdAndClassSectionId(Long studentId, Long classSectionId);
    long countByClassSectionId(Long classSectionId);
}