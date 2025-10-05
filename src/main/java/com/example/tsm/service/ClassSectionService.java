package com.example.tsm.service;

import com.example.tsm.common.api.PageResult;
import com.example.tsm.common.exception.NotFoundException;
import com.example.tsm.domain.ClassSection;
import com.example.tsm.domain.Course;
import com.example.tsm.domain.Teacher;
import com.example.tsm.dto.section.ClassSectionCreateReq;
import com.example.tsm.dto.section.ClassSectionResp;
import com.example.tsm.dto.section.ClassSectionUpdateReq;
import com.example.tsm.repository.ClassSectionRepository;
import com.example.tsm.repository.CourseRepository;
import com.example.tsm.repository.TeacherRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class ClassSectionService {

    private final ClassSectionRepository classSectionRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public ClassSectionService(ClassSectionRepository classSectionRepository,
                               CourseRepository courseRepository,
                               TeacherRepository teacherRepository) {
        this.classSectionRepository = classSectionRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public ClassSectionResp create(ClassSectionCreateReq req) {
        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new NotFoundException("课程不存在"));
        Teacher teacher = teacherRepository.findById(req.getTeacherId())
                .orElseThrow(() -> new NotFoundException("教师不存在"));

        if (classSectionRepository.existsByCourseIdAndTermAndSectionCode(
                req.getCourseId(), req.getTerm(), req.getSectionCode())) {
            throw new DuplicateKeyException("教学班已存在（课程/学期/班号重复）");
        }

        ClassSection cs = new ClassSection();
        copy(req, cs, course, teacher);
        ClassSection saved = classSectionRepository.save(cs);
        return toResp(saved);
    }

    @Transactional
    public ClassSectionResp update(Long id, ClassSectionUpdateReq req) {
        ClassSection cs = classSectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("教学班不存在"));

        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new NotFoundException("课程不存在"));
        Teacher teacher = teacherRepository.findById(req.getTeacherId())
                .orElseThrow(() -> new NotFoundException("教师不存在"));

        boolean keyChanged = !cs.getCourse().getId().equals(req.getCourseId())
                || !cs.getTerm().equals(req.getTerm())
                || !cs.getSectionCode().equals(req.getSectionCode());

        if (keyChanged && classSectionRepository.existsByCourseIdAndTermAndSectionCode(
                req.getCourseId(), req.getTerm(), req.getSectionCode())) {
            throw new DuplicateKeyException("教学班已存在（课程/学期/班号重复）");
        }

        copy(req, cs, course, teacher);
        ClassSection saved = classSectionRepository.save(cs);
        return toResp(saved);
    }

    @Transactional(readOnly = true)
    public ClassSectionResp get(Long id) {
        ClassSection cs = classSectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("教学班不存在"));
        return toResp(cs);
    }

    @Transactional
    public void delete(Long id) {
        ClassSection cs = classSectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("教学班不存在"));
        classSectionRepository.delete(cs);
    }

    @Transactional(readOnly = true)
    public PageResult<ClassSectionResp> page(Long courseId,
                                             Long teacherId,
                                             String term,
                                             String keyword,
                                             Pageable pageable) {
        Specification<ClassSection> spec = (root, query, cb) -> {
            var preds = new ArrayList<Predicate>();
            if (courseId != null) {
                preds.add(cb.equal(root.get("course").get("id"), courseId));
            }
            if (teacherId != null) {
                preds.add(cb.equal(root.get("teacher").get("id"), teacherId));
            }
            if (term != null && !term.isBlank()) {
                preds.add(cb.equal(root.get("term"), term));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                preds.add(cb.or(
                        cb.like(cb.lower(root.get("sectionCode")), like),
                        cb.like(cb.lower(root.get("course").get("name")), like),
                        cb.like(cb.lower(root.get("course").get("code")), like)
                ));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };
        Page<ClassSection> page = classSectionRepository.findAll(spec, pageable);
        return PageResult.from(page.map(this::toResp));
    }

    /* ============ 两个重载 copy，无强制类型转换 ============ */
    private void copy(ClassSectionCreateReq req, ClassSection cs, Course course, Teacher teacher) {
        cs.setCourse(course);
        cs.setTeacher(teacher);
        cs.setTerm(req.getTerm());
        cs.setSectionCode(req.getSectionCode());
        cs.setCapacity(req.getCapacity());
        cs.setSchedule(req.getSchedule());
        cs.setClassroom(req.getClassroom());
        cs.setRemark(req.getRemark());
    }

    private void copy(ClassSectionUpdateReq req, ClassSection cs, Course course, Teacher teacher) {
        cs.setCourse(course);
        cs.setTeacher(teacher);
        cs.setTerm(req.getTerm());
        cs.setSectionCode(req.getSectionCode());
        cs.setCapacity(req.getCapacity());
        cs.setSchedule(req.getSchedule());
        cs.setClassroom(req.getClassroom());
        cs.setRemark(req.getRemark());
    }

    private ClassSectionResp toResp(ClassSection cs) {
        ClassSectionResp r = new ClassSectionResp();
        r.setId(cs.getId());
        r.setCourseId(cs.getCourse().getId());
        r.setCourseCode(cs.getCourse().getCode());
        r.setCourseName(cs.getCourse().getName());
        r.setTeacherId(cs.getTeacher().getId());
        r.setTeacherCode(cs.getTeacher().getCode());
        r.setTeacherName(cs.getTeacher().getName());
        r.setTerm(cs.getTerm());
        r.setSectionCode(cs.getSectionCode());
        r.setCapacity(cs.getCapacity());
        r.setSchedule(cs.getSchedule());
        r.setClassroom(cs.getClassroom());
        r.setRemark(cs.getRemark());
        r.setCreatedAt(cs.getCreatedAt());
        r.setUpdatedAt(cs.getUpdatedAt());
        return r;
    }
}