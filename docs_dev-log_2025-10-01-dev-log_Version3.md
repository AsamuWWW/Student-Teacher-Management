# 开发日报（2025-10-01）

作者：AsamuWWW × Copilot  
项目：师生管理系统（Java / Spring Boot）

## 今日进展
1. 项目骨架（M1）
   - Spring Boot 3 + Web + Validation
   - Spring Data JPA + H2（文件模式）
   - Swagger UI（springdoc-openapi）
   - 统一返回结构 ApiResponse
   - 全局异常处理（400/404/409/500 等）

2. 数据层（M2-2A）
   - 基础实体：UserAccount, Teacher, Student, Course, ClassSection, Enrollment
   - 基类时间戳（createdAt/updatedAt）
   - 唯一约束：工号、学号、课程编码、教学班组合键等
   - JPA 仓库接口，部分带 Specification/统计

3. 教师与学生 CRUD（M2-2B，部分完成）
   - DTO 校验（必填、长度、邮箱/手机号格式）
   - Service 层：唯一性检查、分页查询
   - Controller：分页/详情/新增/编辑/删除
   - 删除接口调整为统一响应（便于在 Swagger 观察结果）

4. 可读性增强
   - 新增 ResponseBodyAdvice：在响应体内补充 httpStatus 与 statusNote（对 201/204/409 等状态的人类可读解释）
   - 优化异常消息：DuplicateKeyException（工号/学号已存在）、DataIntegrityViolationException（外键/唯一冲突给出建议）

## 关键配置与说明
- 数据库：H2 文件模式
  - URL：`jdbc:h2:file:./data/tsm;MODE=MySQL;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE`
  - 数据文件：`./data/tsm.mv.db`（相对项目运行目录）
  - 控制台：`/h2-console`，JDBC URL 填写上面的地址
  - 注意：切换运行目录会让相对路径变化，看起来像“数据丢失”。建议固定到 `${user.home}/tsm-data/tsm`。

- JPA
  - `ddl-auto=update`（不会删库，仅按实体变更升级）
  - 打印 SQL 与参数绑定日志，便于排查

## 遇到的问题与解决
- 端口被占用（8080）
  - 根因：本机 Tomcat11 占用
  - 处理：taskkill /PID 5884 /F 或停止 Windows 服务“Tomcat11”；或临时改端口

- Maven 报 “no POM in this directory”
  - 根因：在错误目录执行命令
  - 处理：切到项目根目录（含 pom.xml）再运行，或用 `-f` 指定 pom 路径

- Swagger 删除 204 被误解为失败
  - 调整：删除接口返回 ApiResponse（200 + {"code":0,"message":"deleted"}）

- Lombok 大量 “Cannot resolve method …”
  - 根因：IDE 未启用注解处理或未安装 Lombok 插件
  - 处理：安装插件 + Enable annotation processing；mvn clean compile + Rebuild

- ResponseBodyAdvice 编译失败（getStatusCode 不存在）
  - 根因：Spring 6 中 ServerHttpResponse 无 getStatusCode()
  - 处理：使用 `ServletServerHttpResponse` + `raw.getStatus()`，并 `HttpStatus.resolve`

- 409 冲突（新增教师/学生）
  - 根因：唯一约束冲突（重复工号/学号）
  - 处理：DuplicateKeyException 统一转为 40901 并提示具体字段；或先删除旧数据

- GET 400 常见原因总结
  - 路径参数非数字（`/teachers/{id}` 把 `{id}` 原样传过去）
  - 分页参数非法（`page<0`、`size<=0`）
  - 排序字段不存在或方向写错（应为 `asc/desc`）
  - 我们已将这些统一转为 400 并给出明确 message

## 今日主要接口样例
- 健康检查：GET `/api/v1/ping`
- 教师
  - 分页：GET `/api/v1/teachers?page=0&size=10&sort=createdAt,desc`
  - 详情：GET `/api/v1/teachers/{id}`（`{id}` 用数字）
  - 新增：POST `/api/v1/teachers`（成功 201）
  - 编辑：PUT `/api/v1/teachers/{id}`
  - 删除：DELETE `/api/v1/teachers/{id}`（返回 `{"code":0,"message":"deleted"}`）

所有响应体均增加：
```json
{
  "httpStatus": 201,
  "statusNote": "已创建（Created）：资源创建成功。"
}
```

## 明日计划（Next）
- 2C：课程（Course）与教学班（ClassSection）CRUD + 校验
- 2D：选课（Enrollment）增删与容量约束、教师查看花名册
- 3：登录与鉴权（JWT + 角色）
- 修订 README：追加“数据库持久化与常见错误 FAQ”

## 提交建议（Commit Message）
- feat(api): add teacher/student CRUD with validation and pagination
- feat(core): add ApiResponse status note advice for better HTTP status explanation
- fix(build): enable lombok annotation processing and update response advice for Spring 6
- chore(db): use H2 file mode with auto DDL update; add SQL logging
- docs: add 2025-10-01 dev log and usage notes
