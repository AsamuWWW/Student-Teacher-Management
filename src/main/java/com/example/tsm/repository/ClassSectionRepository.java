package com.example.tsm.repository;

import com.example.tsm.domain.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

public interface ClassSectionRepository extends JpaRepository<ClassSection, Long>, JpaSpecificationExecutor<ClassSection> {

    boolean existsByCourseIdAndTermAndSectionCode(Long courseId, String term, String sectionCode);

    // 选课前加锁，防止并发超额
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select cs from ClassSection cs where cs.id = :id")
    ClassSection lockById(Long id);
}