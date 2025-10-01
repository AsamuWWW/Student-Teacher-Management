package com.example.tsm.service;

import com.example.tsm.common.api.PageResult;
import com.example.tsm.common.exception.NotFoundException;
import com.example.tsm.domain.Teacher;
import com.example.tsm.dto.teacher.TeacherCreateReq;
import com.example.tsm.dto.teacher.TeacherResp;
import com.example.tsm.dto.teacher.TeacherUpdateReq;
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
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public TeacherResp create(TeacherCreateReq req) {
        if (teacherRepository.existsByCode(req.getCode())) {
            throw new DuplicateKeyException("工号已存在");
        }
        Teacher t = new Teacher();
        copy(req, t);
        Teacher saved = teacherRepository.save(t);
        return toResp(saved);
    }

    @Transactional
    public TeacherResp update(Long id, TeacherUpdateReq req) {
        Teacher t = teacherRepository.findById(id).orElseThrow(() -> new NotFoundException("教师不存在"));
        // 如果修改了 code，做唯一性检查
        if (!t.getCode().equals(req.getCode()) && teacherRepository.existsByCode(req.getCode())) {
            throw new DuplicateKeyException("工号已存在");
        }
        copy(req, t);
        Teacher saved = teacherRepository.save(t);
        return toResp(saved);
    }

    @Transactional(readOnly = true)
    public TeacherResp get(Long id) {
        Teacher t = teacherRepository.findById(id).orElseThrow(() -> new NotFoundException("教师不存在"));
        return toResp(t);
    }

    @Transactional
    public void delete(Long id) {
        Teacher t = teacherRepository.findById(id).orElseThrow(() -> new NotFoundException("教师不存在"));
        teacherRepository.delete(t);
    }

    @Transactional(readOnly = true)
    public PageResult<TeacherResp> page(String keyword, String department, Pageable pageable) {
        Specification<Teacher> spec = (root, query, cb) -> {
            var preds = new ArrayList<Predicate>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                preds.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("code")), like)
                ));
            }
            if (department != null && !department.isBlank()) {
                preds.add(cb.equal(root.get("department"), department));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };
        Page<Teacher> page = teacherRepository.findAll(spec, pageable);
        return PageResult.from(page.map(this::toResp));
    }

    // mapping
    private void copy(TeacherCreateReq req, Teacher t) {
        t.setCode(req.getCode());
        t.setName(req.getName());
        t.setEmail(req.getEmail());
        t.setPhone(req.getPhone());
        t.setDepartment(req.getDepartment());
        t.setTitle(req.getTitle());
        t.setRemark(req.getRemark());
    }

    private void copy(TeacherUpdateReq req, Teacher t) {
        t.setCode(req.getCode());
        t.setName(req.getName());
        t.setEmail(req.getEmail());
        t.setPhone(req.getPhone());
        t.setDepartment(req.getDepartment());
        t.setTitle(req.getTitle());
        t.setRemark(req.getRemark());
    }

    private TeacherResp toResp(Teacher t) {
        TeacherResp r = new TeacherResp();
        r.setId(t.getId());
        r.setCode(t.getCode());
        r.setName(t.getName());
        r.setEmail(t.getEmail());
        r.setPhone(t.getPhone());
        r.setDepartment(t.getDepartment());
        r.setTitle(t.getTitle());
        r.setRemark(t.getRemark());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        return r;
    }
}