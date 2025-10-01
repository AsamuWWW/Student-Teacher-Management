package com.example.tsm.repository;

import com.example.tsm.domain.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassSectionRepository extends JpaRepository<ClassSection, Long> {
    boolean existsByCourseIdAndTermAndSectionCode(Long courseId, String term, String sectionCode);
}