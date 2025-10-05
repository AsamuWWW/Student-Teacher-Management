# 开发日志 - 2025-10-05
项目：Student-Teacher-Management  
阶段：2C（课程 & 教学班）+ 2D（选课 Enrollment）  
作者：AsamuWWW × Copilot

---

## 1. 今日完成概览
| 模块 | 内容 | 状态 |
|------|------|------|
| Course | 实体 / DTO / Repository / Service / Controller / 分页过滤 / 唯一编码 | 完成 |
| ClassSection | 实体 / DTO / Repository / Service / Controller / 课程+学期+班号唯一 / 分页组合过滤 | 完成 |
| Enrollment | 实体 / DTO / Repository / Service / Controller / 选课、退选、成绩更新、花名册 | 完成 |
| 错误处理 | DuplicateKey / NotFound / 400 参数错误 / 容量与重复选课冲突 | 已统一 |
| 状态可读性 | httpStatus + statusNote（ResponseBodyAdvice） | 已继承 |
| 并发控制 | 教学班选课悲观锁（防止容量超卖） | 完成 |
| 代码风格 | 保持与 Teacher/Student 模块一致 | 完成 |

---

## 2. 课程（Course）模块要点
- 唯一键：`code`（uk_course_code）
- 支持过滤：keyword（匹配 code/name）、department
- 字段：code, name, department, description, credits（可空）
- 错误：
  - 409：重复课程编码
  - 404：课程不存在
  - 400：学分格式非法（转换 BigDecimal 失败）

---

## 3. 教学班（ClassSection）模块要点
- 组合唯一（course_id + term + section_code）→ uk_section_course_term_code
- 支持过滤：courseId / teacherId / term / keyword（课程名、课程编码、教学班号）
- 容量字段 capacity（后续 Enrollment 会使用）
- 暂未阻止删除课程或教师导致“孤立的教学班”——未来可加外键约束或业务校验
- 错误：
  - 409：教学班重复
  - 404：课程 / 教师 / 教学班不存在
  - 400：参数不合法

---

## 4. 选课（Enrollment）模块要点
| 功能 | 描述 |
|------|------|
| 选课 | POST /api/v1/enrollments |
| 退选 | DELETE /api/v1/enrollments/{id} |
| 成绩更新 | PUT /api/v1/enrollments/{id}/grade |
| 详情 | GET /api/v1/enrollments/{id} |
| 分页查询 | GET /api/v1/enrollments（多条件过滤） |
| 花名册 | GET /api/v1/sections/{sectionId}/enrollments |

### 选课规则
1. 学生存在 + 教学班存在（404 否则）
2. 悲观锁锁定教学班（`@Lock(PESSIMISTIC_WRITE)`）
3. 重复选课阻止：`existsByStudentIdAndClassSectionId`
4. 容量限制：`countByClassSectionId < capacity`
5. 成绩：可空，范围 0–100，保留两位小数

### 错误
| 场景 | HTTP | 业务码 | Message 示例 |
|------|------|--------|--------------|
| 重复选课 | 409 | 40901 | 已选该教学班，不能重复选课 |
| 容量满 | 409 | 40901 | 教学班容量已满 |
| 选课记录不存在 | 404 | 40400 | 选课记录不存在 |
| 成绩格式非法 | 400 | 40003 | 成绩格式不正确 |
| 参数类型错误（id 非数字） | 400 | 40002 | id 必须为数字 |

---

## 5. 统一响应与状态说明
- 所有成功返回：`ApiResponse{ code=0, data=..., httpStatus, statusNote }`
- 常见状态 note 示例：
  - 201：已创建（Created）：资源创建成功。
  - 409：冲突（Conflict）：唯一约束或业务规则冲突…
  - 40002：路径参数类型错误：id 必须为数字。

---

## 6. 关键类/接口列表
| 层 | 课程 | 教学班 | 选课 |
|----|------|--------|------|
| Entity | Course | ClassSection | Enrollment |
| DTO(Create/Update/Resp) | Course* | ClassSection* | EnrollmentCreateReq / GradeUpdateReq / Resp |
| Repository | CourseRepository | ClassSectionRepository（含 lockById） | EnrollmentRepository |
| Service | CourseService | ClassSectionService | EnrollmentService |
| Controller | CourseController | ClassSectionController | EnrollmentController / ClassSectionRosterControllerExtension |

---

## 7. 日志与调试建议
```yaml
logging:
  level:
    org.hibernate.SQL: debug
    org.hibernate.orm.jdbc.bind: trace
    org.springframework.data.jpa.repository.query: debug
```
- 查看动态方法（existsBy + countBy）生成的 SQL。
- 并发测试建议使用 JMeter / Postman Runner 对同一教学班并行选课。

---

## 8. 当前可改进点（Backlog）
| 优先级 | 事项 | 说明 |
|--------|------|------|
| 高 | 删除课程/教学班前的关联检测 | 有选课记录时阻止或级联策略 |
| 中 | 选课成绩审核状态 | 增加状态：DRAFT / PUBLISHED |
| 中 | 登录/鉴权（角色） | Teacher / Student / Admin 分权 |
| 中 | 接口分组与简化 | Swagger tags 分模块 / 隐藏内部接口 |
| 低 | 批量导入选课 | Excel / CSV |
| 低 | 统计接口 | 课程满班率 / 教师带班数 / 学生选课数 |

---

## 9. 明日/下一阶段计划（建议优先级）
1. 3A：鉴权与用户登录（JWT / Spring Security）
2. 3B：角色权限（限制选课、成绩修改仅教师）
3. 3C：删除约束增强（级联检查 + 友好 409 提示）
4. 3D：统计/报表接口
5. 4：前端或简单管理界面（如 Vue）

---

## 10. 验证脚本示例（cURL）
```bash
# 创建课程
curl -X POST http://localhost:8080/api/v1/courses -H "Content-Type: application/json" -d '{"code":"CS101","name":"程序设计","department":"计算机","description":"基础","credits":"3"}'

# 创建教学班
curl -X POST http://localhost:8080/api/v1/sections -H "Content-Type: application/json" -d '{"courseId":1,"teacherId":1,"term":"2024-2025-1","sectionCode":"01","capacity":60}'

# 选课
curl -X POST http://localhost:8080/api/v1/enrollments -H "Content-Type: application/json" -d '{"studentId":1,"classSectionId":1}'

# 更新成绩
curl -X PUT http://localhost:8080/api/v1/enrollments/1/grade -H "Content-Type: application/json" -d '{"grade":"95.5"}'

# 花名册
curl http://localhost:8080/api/v1/sections/1/enrollments?page=0&size=10
```

---

## 11. Commit Message 建议
```
feat(course): add course CRUD with validation and pagination
feat(section): implement class section CRUD with composite uniqueness and filtering
feat(section): add pessimistic lock support for capacity control
feat(enrollment): implement enrollment (add/drop) with capacity & duplicate checks
feat(enrollment): add grade update and roster endpoint
docs: add 2025-10-05 dev log and problem log
```

---

## 12. 快速健康检查（完成判定）
- [x] /api/v1/courses POST/GET 正常
- [x] /api/v1/sections POST/GET 正常
- [x] /api/v1/enrollments 选课成功 + 重复 409
- [x] 成绩更新 200
- [x] 花名册分页正常
- [x] 状态说明字段 httpStatus + statusNote 正常显示

> 若其中任一步失败，优先检查：包扫描、实体字段、Repository 方法命名、数据库唯一约束命名冲突、事务锁是否被数据库支持（H2 支持基本悲观锁语义）。

---

完成。下一阶段推荐从鉴权开始。