package com.example.tsm.common.web;

import com.example.tsm.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 在响应写出前，为 ApiResponse 自动填充 httpStatus 与 statusNote。
 */
@RestControllerAdvice
public class StatusNoteResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 放行所有响应，在 beforeBodyWrite 中判断是否为 ApiResponse
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (!(body instanceof ApiResponse<?> resp)) {
            return body;
        }

        // 对于 Spring MVC，这里通常是 ServletServerHttpResponse
        HttpStatus status = null;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            HttpServletResponse raw = servletResponse.getServletResponse();
            status = HttpStatus.resolve(raw.getStatus());
        }
        if (status == null) {
            status = HttpStatus.OK; // 兜底
        }

        if (resp.getHttpStatus() == null) {
            resp.setHttpStatus(status.value());
        }
        if (resp.getStatusNote() == null) {
            resp.setStatusNote(explain(status));
        }
        return resp;
    }

    private String explain(HttpStatus status) {
        int s = status.value();
        return switch (s) {
            case 200 -> "请求成功（OK）：操作已成功执行。";
            case 201 -> "已创建（Created）：资源创建成功。";
            case 204 -> "成功但无内容（No Content）：操作成功，但没有返回体。";
            case 400 -> "请求无效（Bad Request）：参数或数据格式有误。";
            case 401 -> "未认证（Unauthorized）：请先登录再访问。";
            case 403 -> "已认证但无权限（Forbidden）：当前账号无访问权限。";
            case 404 -> "未找到（Not Found）：资源不存在或已被删除。";
            case 409 -> "冲突（Conflict）：唯一约束或业务规则冲突（如工号/学号重复或存在关联未解除）。";
            case 422 -> "无法处理（Unprocessable Entity）：语义正确但业务校验未通过。";
            case 429 -> "请求过多（Too Many Requests）：请稍后重试。";
            case 500 -> "服务器内部错误（Internal Server Error）：请联系管理员或查看日志。";
            default -> "HTTP " + s + "：" + status.getReasonPhrase();
        };
    }
}