package com.example.tsm.service;

import com.example.tsm.common.api.PageResult;
import com.example.tsm.common.exception.NotFoundException;
import com.example.tsm.domain.Course;
import com.example.tsm.dto.course.CourseCreateReq;
import com.example.tsm.dto.course.CourseResp;
import com.example.tsm.dto.course.CourseUpdateReq;
import com.example.tsm.repository.CourseRepository;
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
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public CourseResp create(CourseCreateReq req) {
        if (courseRepository.existsByCode(req.getCode())) {
            throw new DuplicateKeyException("课程编码已存在");
        }
        Course c = new Course();
        copy(req, c); // 不做强转，直接调用 create 版
        Course saved = courseRepository.save(c);
        return toResp(saved);
    }

    @Transactional
    public CourseResp update(Long id, CourseUpdateReq req) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("课程不存在"));
        if (!c.getCode().equals(req.getCode()) && courseRepository.existsByCode(req.getCode())) {
            throw new DuplicateKeyException("课程编码已存在");
        }
        copy(req, c); // 调用 update 版
        Course saved = courseRepository.save(c);
        return toResp(saved);
    }

    @Transactional(readOnly = true)
    public CourseResp get(Long id) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("课程不存在"));
        return toResp(c);
    }

    @Transactional
    public void delete(Long id) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("课程不存在"));
        courseRepository.delete(c);
    }

    @Transactional(readOnly = true)
    public PageResult<CourseResp> page(String keyword, String department, Pageable pageable) {
        Specification<Course> spec = (root, query, cb) -> {
            var preds = new ArrayList<Predicate>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                preds.add(cb.or(
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("name")), like)
                ));
            }
            if (department != null && !department.isBlank()) {
                preds.add(cb.equal(root.get("department"), department));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };
        Page<Course> page = courseRepository.findAll(spec, pageable);
        return PageResult.from(page.map(this::toResp));
    }

    /* ================== 内部复制方法（两个重载，无强转） ================== */
    private void copy(CourseCreateReq req, Course c) {
        c.setCode(req.getCode());
        c.setName(req.getName());
        c.setDepartment(req.getDepartment());
        c.setDescription(req.getDescription());
        setCredits(req.getCredits(), c);
    }

    private void copy(CourseUpdateReq req, Course c) {
        c.setCode(req.getCode());
        c.setName(req.getName());
        c.setDepartment(req.getDepartment());
        c.setDescription(req.getDescription());
        setCredits(req.getCredits(), c);
    }

    private void setCredits(String creditsStr, Course c) {
        if (creditsStr == null || creditsStr.isBlank()) {
            c.setCredits(null);
            return;
        }
        try {
            c.setCredits(new BigDecimal(creditsStr));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("学分格式不正确，应为数字，例如 3 或 3.5");
        }
    }

    private CourseResp toResp(Course c) {
        CourseResp r = new CourseResp();
        r.setId(c.getId());
        r.setCode(c.getCode());
        r.setName(c.getName());
        r.setDepartment(c.getDepartment());
        r.setDescription(c.getDescription());
        r.setCredits(c.getCredits());
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
        return r;
    }
}