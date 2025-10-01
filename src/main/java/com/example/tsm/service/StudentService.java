package com.example.tsm.service;

import com.example.tsm.common.api.PageResult;
import com.example.tsm.common.exception.NotFoundException;
import com.example.tsm.domain.Student;
import com.example.tsm.dto.student.StudentCreateReq;
import com.example.tsm.dto.student.StudentResp;
import com.example.tsm.dto.student.StudentUpdateReq;
import com.example.tsm.repository.StudentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentResp create(StudentCreateReq req) {
        if (studentRepository.existsByCode(req.getCode())) {
            throw new DuplicateKeyException("学号已存在");
        }
        Student s = new Student();
        copy(req, s);
        Student saved = studentRepository.save(s);
        return toResp(saved);
    }

    @Transactional
    public StudentResp update(Long id, StudentUpdateReq req) {
        Student s = studentRepository.findById(id).orElseThrow(() -> new NotFoundException("学生不存在"));
        if (!s.getCode().equals(req.getCode()) && studentRepository.existsByCode(req.getCode())) {
            throw new DuplicateKeyException("学号已存在");
        }
        copy(req, s);
        Student saved = studentRepository.save(s);
        return toResp(saved);
    }

    @Transactional(readOnly = true)
    public StudentResp get(Long id) {
        Student s = studentRepository.findById(id).orElseThrow(() -> new NotFoundException("学生不存在"));
        return toResp(s);
    }

    @Transactional
    public void delete(Long id) {
        Student s = studentRepository.findById(id).orElseThrow(() -> new NotFoundException("学生不存在"));
        studentRepository.delete(s);
    }

    @Transactional(readOnly = true)
    public PageResult<StudentResp> page(String keyword, String grade, String major, Pageable pageable) {
        Specification<Student> spec = (root, query, cb) -> {
            var preds = new ArrayList<Predicate>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                preds.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("code")), like)
                ));
            }
            if (grade != null && !grade.isBlank()) {
                preds.add(cb.equal(root.get("grade"), grade));
            }
            if (major != null && !major.isBlank()) {
                preds.add(cb.equal(root.get("major"), major));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };
        Page<Student> page = studentRepository.findAll(spec, pageable);
        return PageResult.from(page.map(this::toResp));
    }

    private void copy(StudentCreateReq req, Student s) {
        s.setCode(req.getCode());
        s.setName(req.getName());
        s.setEmail(req.getEmail());
        s.setPhone(req.getPhone());
        s.setGrade(req.getGrade());
        s.setMajor(req.getMajor());
        s.setRemark(req.getRemark());
    }

    private void copy(StudentUpdateReq req, Student s) {
        s.setCode(req.getCode());
        s.setName(req.getName());
        s.setEmail(req.getEmail());
        s.setPhone(req.getPhone());
        s.setGrade(req.getGrade());
        s.setMajor(req.getMajor());
        s.setRemark(req.getRemark());
    }

    private StudentResp toResp(Student s) {
        StudentResp r = new StudentResp();
        r.setId(s.getId());
        r.setCode(s.getCode());
        r.setName(s.getName());
        r.setEmail(s.getEmail());
        r.setPhone(s.getPhone());
        r.setGrade(s.getGrade());
        r.setMajor(s.getMajor());
        r.setRemark(s.getRemark());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        return r;
    }
}