# 问题与解决方案日志（2C & 2D 阶段）

时间：2025-10-05  
范围：Course / ClassSection / Enrollment 模块开发与调试

---

## 1. 编译错误：强制类型转换失败
- 现象：`Inconvertible types; cannot cast CourseUpdateReq to CourseCreateReq`
- 根因：Service 中复用 copy 方法时用 `(CreateReq)` 强转 `UpdateReq`
- 解决：为 Create/Update 分别写两个 `copy` 重载，移除强转。

## 2. “找不到包”异常
- 现象：`package com.example.tsm.dto.course does not exist`
- 根因：目录结构不匹配或文件未在 `src/main/java`；有时未保存文件
- 排查清单：
  1. 确认物理路径：`src/main/java/com/example/tsm/dto/course`
  2. Source Root 标记正确
  3. 包名全部小写一致
  4. `mvn clean compile` 后再看 IDE 红线
- 解决：修正路径 + 重新导入 Maven。

## 3. 409 冲突（课程编码 / 教学班组合）
- 触发：重复 code 或 (courseId, term, sectionCode)
- 机制：先应用层 `existsBy...`；数据库层唯一约束兜底
- 优化：捕获 DuplicateKeyException 友好返回业务码 40901。

## 4. 容量判断并发安全
- 背景：同时多个线程选同一教学班时可能超过 capacity
- 措施：`ClassSectionRepository.lockById(id)` 使用悲观写锁
- 说明：H2 支持基本排他锁，但更严格行为需真实数据库验证（MySQL InnoDB）。

## 5. 重复选课未生效
- 可能原因：
  - 缺少唯一约束 uk_enrollment_student_section
  - `existsByStudentIdAndClassSectionId` 方法名拼写错误
- 解决：
  - 校验实体 `@UniqueConstraint` 是否正确
  - 启动时关注 PropertyReferenceException

## 6. 成绩更新格式异常
- 现象：PUT /grade 返回 400
- 根因：非数值或超范围
- 校验：
  - DTO 注解：`@DecimalMin("0.0") @DecimalMax("100.0") @Digits(3,2)`
  - Service 再做 BigDecimal 解析
- 建议：若要支持“清空成绩”允许空字符串，当前已支持（空 => null）。

## 7. 花名册接口为何自动出现
- 原因：新增 `@RestController` + `@RequestMapping` 后 springdoc 自动收集并在 `/v3/api-docs` 输出 → Swagger UI 前端渲染出按钮。
- 机制：ComponentScan → HandlerMapping → OpenAPI 文档 → Swagger UI。

## 8. 路径参数导致 400
- 现象：GET `/api/v1/courses/{id}` 用 `{id}` 字面量
- 错误码：40002
- 解决：确保使用实际数字；前端调试时注意 Swagger “Try it out” 已默认替换占位值。

## 9. 学分 / 成绩解析异常
- 学分：非数字时抛 `IllegalArgumentException("学分格式不正确")`
- 成绩：数字格式非法 → 40003
- 改进建议：可统一包装成 ValidationException，便于前端区分字段级错误。

## 10. 删除资源的关联影响（未封装业务反馈）
- 风险：删除课程后数据库层面若无外键限制，教学班失联
- 解决策略（未实现）：
  - 添加外键 `ON DELETE RESTRICT`
  - 删除前逻辑检查：若存在教学班 / 选课 → 409 & 提示

## 11. SQL 日志未打印
- 原因：未在 application.yml 设置日志级别
- 设置：
  ```yaml
  logging:
    level:
      org.hibernate.SQL: debug
      org.hibernate.orm.jdbc.bind: trace
  ```

## 12. 悲观锁看似不起效果
- 可能误解：
  - 单线程手动测试感知不到
  - H2 对锁的表现与生产数据库略有差异
- 验证方式：
  - 使用 2 个并发请求（Postman Runner / curl & sleep）对同一教学班做容量极限选择（capacity=1）

## 13. Swagger 返回 500 而不是 409/404
- 根因：早期未加 DuplicateKeyException / IllegalArgumentException 专门处理
- 解决：GlobalExceptionHandler 增加对应 @ExceptionHandler 分支。

## 14. ResponseBodyAdvice 状态获取 null
- 原因：使用 `ServerHttpResponse.getStatusCode()`（在 Spring 6 接口里不可用）
- 修复：强转 `ServletServerHttpResponse` → `raw.getStatus()` → `HttpStatus.resolve(...)`

## 15. 未来潜在问题（提前记录）
| 问题 | 潜在风险 | 预防 |
|------|----------|------|
| 并发锁粒度粗 | 高并发影响吞吐 | 后期换行锁 + 乐观版本号 |
| 分页排序字段未白名单 | SQL 注入（排序字段） | 限制允许字段列表 |
| 成绩权限未限制 | 任意人可改成绩 | 引入角色 + 方法级鉴权 |
| Enrollment 查询拼接字段多 | 可读性降低 | 拆分 ViewObject / QueryService |
| 事务边界过大 | 性能问题 | 精简事务，仅锁关键语句 |

---

## 快速排查清单（Troubleshooting Checklist）

| 现象 | 立刻检查 |
|------|----------|
| 409 冲突但消息空 | 是否捕获的是 DataIntegrityViolationException 而非 DuplicateKeyException |
| 一直容量未满 | 是否缺少悲观锁 lockById 或事务未生效 (@Transactional proxy) |
| `existsBy...` 报属性错误 | 方法名与实体字段/关联名称不匹配 |
| Swagger 无新接口 | Controller 不在主包子路径 / 少 @RestController / 缓存未刷新 |
| 400 参数错误但不知来源 | 查看响应中的 code（40001 / 40002 / 40003）和 GlobalExceptionHandler |

---

## 建议改进（Action Items）
| 编号 | 动作 | 级别 |
|------|------|------|
| A1 | 删除前关联检查统一策略（课程/教学班/学生） | 高 |
| A2 | 引入角色 & JWT 鉴权 | 高 |
| A3 | 参数排序字段白名单 | 中 |
| A4 | Enrollment 批量导出（CSV/Excel） | 中 |
| A5 | 课程/教学班统计接口 | 中 |
| A6 | 统一 Validation 错误结构（字段级 details） | 中 |
| A7 | 集成测试（WebMvcTest + DataJpaTest） | 中 |
| A8 | Docker 化（H2 → MySQL） | 低 |

---

## 提交建议（与开发日志保持一致）
```
docs: add 2025-10-05 dev and problem logs for course, section, enrollment modules
```

---

## 总结
2C/2D 阶段核心价值：搭建了完整“课程 → 教学班 → 选课”主线，验证了动态查询、唯一约束、并发控制与统一错误返回模式，为后续权限和统计扩展提供了稳定基础。下一阶段优先补齐鉴权和删除约束，以免数据进入不一致状态。

> 如需把本日志拆成多个 Issue，可直接按 Action Items 分拆，我可以再生成 issue 草稿模板。

完成。