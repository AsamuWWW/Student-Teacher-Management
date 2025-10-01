# FAQ（常见问题与排查）

## 1. 刷新页面数据会消失吗？
不会。我们使用 H2 的“文件模式”，数据存储在 `./data/tsm.mv.db`。只要从同一个项目根目录启动，数据会一直保留。若看起来“丢了”，多数是切换了工作目录导致 H2 指向了新的相对路径。

建议把 URL 改为绝对路径，避免混淆：
```yaml
spring:
  datasource:
    url: jdbc:h2:file:${user.home}/tsm-data/tsm;MODE=MySQL;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE
```

## 2. 为什么 GET 返回 400？
- 路径参数不是数字：`/teachers/{id}` 里的 `{id}` 必须是数字
- 分页参数非法：`page` 需 ≥ 0，`size` > 0
- 排序参数错误：`sort=字段,asc|desc` 中字段不存在或方向拼写错误
- 错误码对照：
  - 40001：字段校验（@Valid）不通过（POST/PUT）
  - 40002：路径参数类型错误（如 id 非数字）
  - 40003：请求参数不合法（如 page/size 非法）

## 3. 为什么新增返回 201？删除返回 204/200？
- 201 Created：资源创建成功（REST 标准）
- 204 No Content：操作成功但无响应体（常用于删除）；我们已将删除改为 200 + 统一响应，便于观察结果
- 我们在响应体里加入了 `httpStatus` 与 `statusNote`，便于非专业人员理解

## 4. 409 冲突的常见原因？
- 唯一约束冲突：工号/学号/课程编码重复
- 关联未解除：删除教师/学生时存在教学班/选课关联
- 应对：
  - 换一个唯一值或先删除冲突数据
  - 或按业务要求迁移/解除关联后再删除

## 5. Lombok 提示 get/set 方法不存在？
- 安装 Lombok 插件，开启 Annotation Processing
- 重新导入 Maven 并 Rebuild
- 命令行可用 `mvn clean compile` 验证

## 6. H2 控制台怎么连？
- 地址：`/h2-console`
- JDBC URL：与 application.yml 中的数据源一致
- 用户名：`sa`，密码留空（默认）
