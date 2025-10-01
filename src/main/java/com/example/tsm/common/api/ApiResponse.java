package com.example.tsm.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一响应结构
 * - code: 业务码（0 成功，非 0 为错误）
 * - message: 文本描述
 * - data: 业务数据
 * - traceId: 可选链路追踪
 * - httpStatus: 实际 HTTP 状态码（例如 200/201/404/409/500 等）
 * - statusNote: 对 HTTP 状态的人类可读解释（便于非专业人员理解）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;           // 0 表示成功，其它表示错误
    private String message;     // 描述
    private T data;             // 结果数据
    private String traceId;     // 可选：链路追踪

    // 新增：便于理解 HTTP 状态
    private Integer httpStatus; // 实际 HTTP 状态码（由 ResponseBodyAdvice 自动填充）
    private String statusNote;  // 对状态码的解释（由 ResponseBodyAdvice 自动填充）

    public ApiResponse() {}

    public ApiResponse(int code, String message, T data, String traceId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "ok", data, null);
    }

    public static <T> ApiResponse<T> okMsg(String message) {
        return new ApiResponse<>(0, message, null, null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, null);
    }

    // getters & setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public Integer getHttpStatus() { return httpStatus; }
    public void setHttpStatus(Integer httpStatus) { this.httpStatus = httpStatus; }
    public String getStatusNote() { return statusNote; }
    public void setStatusNote(String statusNote) { this.statusNote = statusNote; }
}