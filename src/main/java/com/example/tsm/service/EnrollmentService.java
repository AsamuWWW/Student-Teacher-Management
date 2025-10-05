package com.example.tsm.service;

import com.example.tsm.common.api.PageResult;
import com.example.tsm.common.exception.NotFoundException;
import com.example.tsm.domain.ClassSection;
import com.example.tsm.domain.Course;
import com.example.tsm.domain.Enrollment;
import com.example.tsm.domain.Student;
import com.example.tsm.dto.enrollment.EnrollmentCreateReq;
import com.example.tsm.dto.enrollment.EnrollmentGradeUpdateReq;
import com.example.tsm.dto.enrollment.EnrollmentResp;
import com.example.tsm.repository.ClassSectionRepository;
import com.example.tsm.repository.CourseRepository;
import com.example.tsm.repository.EnrollmentRepository;
import com.example.tsm.repository.StudentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ClassSectionRepository classSectionRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             ClassSectionRepository classSectionRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.classSectionRepository = classSectionRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public EnrollmentResp enroll(EnrollmentCreateReq req) {
        // 学生存在？
        Student stu = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new NotFoundException("学生不存在"));
        // 悲观锁加载教学班
        ClassSection section = classSectionRepository.lockById(req.getClassSectionId());
        if (section == null) {
            throw new NotFoundException("教学班不存在");
        }

        // 重复选课？
        if (enrollmentRepository.existsByStudentIdAndClassSectionId(stu.getId(), section.getId())) {
            throw new DuplicateKeyException("已选该教学班，不能重复选课");
        }

        // 容量检查
        long current = enrollmentRepository.countByClassSectionId(section.getId());
        if (current >= section.getCapacity()) {
            throw new DuplicateKeyException("教学班容量已满");
        }

        Enrollment e = new Enrollment();
        e.setStudent(stu);
        e.setClassSection(section);
        Enrollment saved = enrollmentRepository.save(e);
        return toResp(saved);
    }

    @Transactional
    public void drop(Long id) {
        Enrollment e = enrollmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("选课记录不存在"));
        enrollmentRepository.delete(e);
    }

    @Transactional
    public EnrollmentResp updateGrade(Long id, EnrollmentGradeUpdateReq req) {
        Enrollment e = enrollmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("选课记录不存在"));
        if (req.getGrade() == null || req.getGrade().isBlank()) {
            e.setGrade(null);
        } else {
            try {
                e.setGrade(new BigDecimal(req.getGrade()));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("成绩格式不正确");
            }
        }
        return toResp(enrollmentRepository.save(e));
    }

    @Transactional(readOnly = true)
    public EnrollmentResp get(Long id) {
        Enrollment e = enrollmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("选课记录不存在"));
        return toResp(e);
    }

    @Transactional(readOnly = true)
    public PageResult<EnrollmentResp> page(Long studentId,
                                           Long classSectionId,
                                           Long courseId,
                                           Long teacherId,
                                           String term,
                                           String keyword,
                                           Pageable pageable) {
        Specification<Enrollment> spec = (root, query, cb) -> {
            var preds = new ArrayList<Predicate>();

            if (studentId != null) {
                preds.add(cb.equal(root.get("student").get("id"), studentId));
            }
            if (classSectionId != null) {
                preds.add(cb.equal(root.get("classSection").get("id"), classSectionId));
            }
            if (courseId != null) {
                preds.add(cb.equal(root.get("classSection").get("course").get("id"), courseId));
            }
            if (teacherId != null) {
                preds.add(cb.equal(root.get("classSection").get("teacher").get("id"), teacherId));
            }
            if (term != null && !term.isBlank()) {
                preds.add(cb.equal(root.get("classSection").get("term"), term));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                preds.add(cb.or(
                        cb.like(cb.lower(root.get("classSection").get("sectionCode")), like),
                        cb.like(cb.lower(root.get("classSection").get("course").get("code")), like),
                        cb.like(cb.lower(root.get("classSection").get("course").get("name")), like),
                        cb.like(cb.lower(root.get("student").get("code")), like),
                        cb.like(cb.lower(root.get("student").get("name")), like)
                ));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };

        Page<Enrollment> page = enrollmentRepository.findAll(spec, pageable);
        return PageResult.from(page.map(this::toResp));
    }

    private EnrollmentResp toResp(Enrollment e) {
        EnrollmentResp r = new EnrollmentResp();
        r.setId(e.getId());
        r.setStudentId(e.getStudent().getId());
        r.setStudentCode(e.getStudent().getCode());
        r.setStudentName(e.getStudent().getName());

        ClassSection cs = e.getClassSection();
        r.setClassSectionId(cs.getId());
        r.setSectionCode(cs.getSectionCode());
        r.setTerm(cs.getTerm());

        Course course = cs.getCourse();
        r.setCourseId(course.getId());
        r.setCourseCode(course.getCode());
        r.setCourseName(course.getName());

        var teacher = cs.getTeacher();
        r.setTeacherId(teacher.getId());
        r.setTeacherCode(teacher.getCode());
        r.setTeacherName(teacher.getName());

        r.setEnrolledAt(e.getEnrolledAt());
        r.setGrade(e.getGrade());
        return r;
    }
}