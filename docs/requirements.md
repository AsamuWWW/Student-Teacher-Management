# 师生管理系统（Java）需求文档（常规教学制，无导师制）

版本：v1.1（MVP）
状态：草案
撰写人：AsamuWWW 与 Copilot
最后更新：2025-10-01

本版本根据你的新要求更新：
- 去除“导师制”与相关功能。
- 引入中国高校常规教学制度：课程、教学班、选课。
- 增加“登录功能（鉴权）”并带基础格式校验。
- 交互方式采用方案 A（REST + Swagger UI）。

## 1. 项目目标
- 简单易上手的基础师生管理系统，支持后续逐步扩展。
- MVP 目标：
  - 账号与登录：基于用户名/密码的登录，JWT 鉴权，基础角色与权限控制。
  - 教师/学生基础信息管理（增删改查）。
  - 课程与教学班管理：课程定义、教学班（开课信息）管理。
  - 学生选课：学生加入/退选教学班，教师查看花名册。
  - 基础查询与筛选、字段格式校验与统一错误处理。

## 2. 术语与角色
- 课程（Course）：学校设置的教学内容，如《数据结构》。
- 教学班（ClassSection/Section）：某门课程在某学期的开课实例，可指定授课教师、容量、上课时间地点等。
- 选课（Enrollment）：学生加入某教学班的记录，可包含成绩。
- 角色（MVP）：
  - 管理员（ADMIN）：管理所有数据（含用户、课程、教学班）。
  - 教师（TEACHER）：管理自己负责的教学班，查看/导出该班学生名单，登记成绩（可选）。
  - 学生（STUDENT）：查看课程与教学班，选课/退课，查看个人课表与成绩（可选）。

说明：为保证“易上手”，可在最初仅开放管理员端完成全流程（含为教师/学生创建账号与分配教学班），随后逐步开放教师/学生端操作。

## 3. 系统范围（MVP）
- 登录与鉴权（必做）
  - 用户名/密码登录，JWT 颁发与校验，基于角色的接口访问控制。
- 教师管理：新增、列表、详情、编辑、删除（若绑定教学班需限制删除）。
- 学生管理：新增、列表、详情、编辑、删除（若有选课记录，默认允许删除并同时清理关联，或阻止删除；MVP 建议阻止）。
- 课程管理：新增课程、列表、详情、编辑、删除（若已有关联教学班，禁止删除）。
- 教学班管理：创建教学班（指定课程、学期、教师、容量、时间地点）、列表、详情、编辑、删除（有选课记录时禁止删除）。
- 学生选课：学生加入教学班、退课；容量检查；教师查看花名册。
- 查询与筛选：关键词（名称/编码/学号等）、学期、院系、年级、专业；分页与排序。
- 数据校验：必填项、长度、格式（邮箱/手机号）、唯一性（课程编码、学号/工号、用户名等）。
- 统一错误处理：HTTP 4xx/5xx，带统一响应结构。

非目标（后续扩展）：
- 单点登录/第三方登录、权限细粒度策略、数据导入导出、排课冲突检测、成绩体系/绩点计算、消息通知、审计日志、多租户、国际化等。

## 4. 登录与权限（MVP）
- 认证方式：用户名 + 密码（BCrypt 加密存储）。
- 登录流程：POST /auth/login -> 校验 -> 颁发 JWT（含用户ID、角色、过期时间）。
- 鉴权方式：Bearer Token（JWT）放入 Authorization 头；无状态。
- 角色与访问控制（示例）：
  - ADMIN：可访问所有管理接口。
  - TEACHER：可管理自己授课的教学班、查看选课学生、登记成绩（可选）。
  - STUDENT：可查看课程/教学班、进行选课退课、查看个人课表。
- 安全策略（基础）：
  - 密码策略：长度 ≥ 8，包含字母与数字（可放宽/调整）。
  - 登录失败统一返回，不暴露具体原因（如“用户名或密码错误”）。
  - JWT 过期时间（如 2 小时），支持可选刷新令牌（Post-MVP）。

## 5. 功能需求
### 5.1 教师管理
- 字段：工号（唯一）、姓名、邮箱、手机号、院系、职称、备注、创建/更新时间。
- 约束：
  - 工号唯一；邮箱格式校验；手机号长度/格式校验。
  - 若教师仍绑定教学班，禁止删除（需先迁移或删除教学班）。

### 5.2 学生管理
- 字段：学号（唯一）、姓名、邮箱、手机号、年级、专业、备注、创建/更新时间。
- 约束：
  - 学号唯一；邮箱/手机号格式校验。
  - 若学生有选课记录，MVP 建议阻止删除（避免数据不一致）。

### 5.3 课程管理
- 字段：课程编码（唯一）、课程名称、学分（可选）、院系、描述、创建/更新时间。
- 约束：课程编码唯一；若已开课（有关联教学班），禁止删除。

### 5.4 教学班管理（开课）
- 字段：
  - 归属课程（courseId）
  - 教学班号（sectionCode，建议在课程内唯一，如 01、02）
  - 学期（term，如 2024-2025-1）
  - 任课教师（teacherId）
  - 容量（capacity）
  - 上课时间地点（schedule，如“周二 3-4 节@教一-201”；MVP 可存字符串）
  - 备注、创建/更新时间
- 约束：
  - 同一课程 + 学期 + 教学班号 组合唯一。
  - 容量需为正数。
  - 有选课记录时禁止删除；可编辑容量（>= 已选人数）。

### 5.5 学生选课（Enrollment）
- 操作：
  - 选课：学生加入某教学班；检查容量（已选 < 容量）。
  - 退课：学生从教学班移除；记录退课时间（可选）。
  - 教师查看花名册：教学班下所有学生列表。
- 字段：
  - 学生（studentId）、教学班（sectionId）唯一组合
  - 状态：enrolled/dropped（MVP 可不保留 dropped 记录，直接删除也可）
  - 成绩：score（可选，后续扩展）
  - 选课时间、退课时间（可选）
- 约束：
  - 重复选同一教学班返回 409 冲突。
  - 容量满返回 409 或 400（建议 409）。

### 5.6 查询与筛选
- 列表页支持参数：
  - keyword：名称/编码/学号/工号模糊
  - filters：学期、院系、年级、专业
  - sort：创建时间/名称/编码，asc/desc
  - page & size：分页；size 默认 ≤ 50

### 5.7 错误与校验
- 唯一性冲突：409 Conflict（学号/工号/课程编码/组合唯一约束等）。
- 字段校验：必填、长度、邮箱/手机号格式，400 Bad Request。
- 业务规则：容量不足、存在关联而不能删除等，400/409。
- 统一响应结构（建议）：
```json
{
  "code": 0,
  "message": "ok",
  "data": {},
  "traceId": "optional"
}
```

## 6. 非功能需求
- 易用性：API 清晰、Swagger UI 可在线调试。
- 性能：1k+ 教师/10k+ 学生/5k+ 选课记录下列表响应 < 200ms（开发机）；全部列表分页。
- 可靠性：异常与错误日志清晰；启动失败有明确提示。
- 安全性：密码加密；JWT 过期；基本输入校验与错误屏蔽。
- 可维护性：分层架构，Service 层单元测试。
- 可扩展性：课程/教学班/选课模型为后续冲突检测、成绩体系、导入导出等预留空间。
- 可部署性：可执行 JAR，默认 H2，可切换 MySQL/PostgreSQL。

## 7. 数据模型（MVP）
### 7.1 用户与账号
- UserAccount
  - id: Long, PK
  - username: String, 唯一, 必填, ≤ 32
  - passwordHash: String, 必填（BCrypt）
  - role: Enum[ADMIN, TEACHER, STUDENT], 必填
  - teacherId: Long, FK -> Teacher.id, 可空（当 role=TEACHER 关联）
  - studentId: Long, FK -> Student.id, 可空（当 role=STUDENT 关联）
  - createdAt, updatedAt: DateTime

说明：管理员账号不需要 teacherId/studentId 关联。

### 7.2 教师与学生
- Teacher
  - id: Long, PK
  - code: String, 工号, 唯一, 必填, ≤ 32
  - name: String, 必填, ≤ 64
  - email: String, 可选, 邮箱格式, ≤ 128
  - phone: String, 可选, ≤ 32
  - department: String, 可选, ≤ 64
  - title: String, 可选（讲师/副教授/教授…）, ≤ 64
  - remark: String, 可选, ≤ 255
  - createdAt, updatedAt

- Student
  - id: Long, PK
  - code: String, 学号, 唯一, 必填, ≤ 32
  - name: String, 必填, ≤ 64
  - email: String, 可选, 邮箱格式, ≤ 128
  - phone: String, 可选, ≤ 32
  - grade: String, 可选（如 2023）, ≤ 16
  - major: String, 可选, ≤ 64
  - remark: String, 可选, ≤ 255
  - createdAt, updatedAt

### 7.3 教学相关
- Course
  - id: Long, PK
  - code: String, 课程编码, 唯一, 必填, ≤ 32
  - name: String, 课程名称, 必填, ≤ 128
  - credits: BigDecimal/Float, 可选
  - department: String, 可选, ≤ 64
  - description: String, 可选, ≤ 1024
  - createdAt, updatedAt

- ClassSection
  - id: Long, PK
  - courseId: FK -> Course.id, 必填
  - sectionCode: String, 教学班号（课程内唯一，如 01、02）, 必填, ≤ 16
  - term: String, 学期（如 2024-2025-1）, 必填, ≤ 32
  - teacherId: FK -> Teacher.id, 必填
  - capacity: Integer, 必填, > 0
  - schedule: String, 可选（如“周二 3-4 节@教一-201”）, ≤ 128
  - classroom: String, 可选, ≤ 64
  - remark: String, 可选, ≤ 255
  - createdAt, updatedAt
  - 组合唯一：courseId + term + sectionCode

- Enrollment
  - id: Long, PK
  - studentId: FK -> Student.id, 必填
  - sectionId: FK -> ClassSection.id, 必填
  - status: Enum[ENROLLED], MVP 固定
  - score: Decimal, 可选（后续扩展）
  - enrolledAt: DateTime
  - 组合唯一：studentId + sectionId

### 7.4 示例数据
```json
{
  "users": [
    {"username":"admin","role":"ADMIN"},
    {"username":"t_zhang","role":"TEACHER"},
    {"username":"s_wang","role":"STUDENT"}
  ],
  "teachers": [
    {"code":"T1001","name":"张老师","department":"计算机学院","title":"讲师"}
  ],
  "students": [
    {"code":"S20230001","name":"王小明","grade":"2023","major":"软件工程"}
  ],
  "courses": [
    {"code":"CS1001","name":"数据结构","credits":3,"department":"计算机学院"}
  ],
  "sections": [
    {"courseCode":"CS1001","term":"2024-2025-1","sectionCode":"01","teacherCode":"T1001","capacity":60,"schedule":"周二3-4@教一-201"}
  ]
}
```

## 8. 接口设计（REST，推荐）
基础路径：/api/v1

- 认证
  - POST /auth/login
    - Body: { "username": "...", "password": "..." }
    - Resp: { "code": 0, "data": { "token": "JWT", "role": "ADMIN" } }
- 教师（需 ADMIN，或 TEACHER 仅能查看自己？MVP：仅 ADMIN 管理）
  - GET /teachers?keyword=&department=&sort=&page=&size=
  - GET /teachers/{id}
  - POST /teachers
  - PUT /teachers/{id}
  - DELETE /teachers/{id}
- 学生（ADMIN 管理；STUDENT 可查看自己信息）
  - GET /students?keyword=&grade=&major=&sort=&page=&size=
  - GET /students/{id}
  - POST /students
  - PUT /students/{id}
  - DELETE /students/{id}
- 课程
  - GET /courses?keyword=&department=&sort=&page=&size=
  - GET /courses/{id}
  - POST /courses
  - PUT /courses/{id}
  - DELETE /courses/{id}
- 教学班
  - GET /sections?courseId=&term=&teacherId=&keyword=&sort=&page=&size=
  - GET /sections/{id}
  - POST /sections
  - PUT /sections/{id}
  - DELETE /sections/{id}
- 选课
  - GET /sections/{id}/enrollments            // 教师/管理员查看花名册
  - POST /sections/{id}/enrollments           // 学生选课，或管理员代选
    - Body: { "studentId": 1 }（学生自己选课可忽略 body）
  - DELETE /sections/{id}/enrollments/{studentId}  // 退课

权限建议（示例，MVP 可简化为仅 ADMIN 可写）：
- ADMIN：上述所有接口的读写。
- TEACHER：读取课程/教学班；对自己教学班的 enrollments 读；可选写成绩。
- STUDENT：读取课程/教学班；对自己选课的创建/删除；读取自己的信息与选课。

## 9. 交互与界面
- 方案 A：REST + Swagger UI（springdoc-openapi）
  - 在线调试所有接口（含授权：支持在 Swagger UI 中填入 Bearer Token）。
  - 后续可接任何前端（Web/移动端）。

## 10. 技术选型
- Java 17+
- Spring Boot 3.x：Web、Validation、Security
- Spring Data JPA（Hibernate）
- 数据库：H2（开发/测试），可切换 MySQL/PostgreSQL
- 文档：springdoc-openapi-starter-webmvc-ui（Swagger UI）
- 安全：Spring Security + JWT（jjwt 或 spring-security-oauth2-jose）
- 测试：JUnit 5、Spring Boot Test、Testcontainers（可选）

## 11. 架构与目录
- 分层：Controller / Service / Repository / Domain / DTO / Config / Security
- 目录结构建议
```
com.example.tsm
├─ controller
├─ service
├─ repository
├─ domain          // JPA 实体
├─ dto             // 请求/响应
├─ security        // JWT、鉴权配置
├─ config
└─ util
```

## 12. 里程碑（建议）
- M1（0.5-1 天）：项目骨架、实体草拟、H2、Swagger、全局异常处理
- M2（1 天）：用户与登录（BCrypt、JWT、Spring Security RBAC）
- M3（1 天）：教师/学生 CRUD + 校验 + 唯一性约束
- M4（1 天）：课程/教学班 CRUD + 约束（容量、唯一组合）
- M5（0.5-1 天）：选课/退课 + 花名册 + 权限收敛
- M6（0.5 天）：打包与 README、示例数据、基础测试

## 13. 验收标准（MVP）
- 登录成功返回 JWT；未携带或无效 Token 的请求被拒（401/403）。
- 教师/学生/课程/教学班 CRUD 正常；唯一性冲突返回 409。
- 选课容量校验正确；重复选课或超容量有明确错误。
- 列表查询可关键词/筛选/分页/排序；Swagger UI 可调通全部接口。
- 系统一键启动（可执行 JAR + 内置 H2），日志清晰；基础单元测试通过。

## 14. 风险与缓解
- 权限复杂度上升：MVP 先以 ADMIN 写、其他读为主，逐步放开写权限。
- 选课规则复杂化（时间冲突/先修课等）：MVP 暂不做，预留字段 schedule，后续扩展校验服务。
- 数据迁移：Post-MVP 使用 Flyway/Liquibase 管理 schema 变更。

## 15. 后续扩展清单
1. 时间冲突检测、先修/限选规则、容量候补队列
2. 成绩/绩点体系、成绩导入导出
3. 批量导入导出（CSV/Excel）
4. 细粒度权限、审计日志、通知消息
5. 多学期日历、教学周、课表视图
6. 多校区/租户、国际化、缓存与性能优化
7. OAuth2/企业登录与 SSO、验证码、防爆破与限流

——

说明
- 若你确认本需求，我们可直接生成项目骨架（Spring Boot + Security + JPA + H2 + Swagger），并附带初始化脚本与示例数据，帮助你“开箱即用”地开始开发。