package com.example.tsm.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentCreateReq {
    @NotBlank
    @Size(max = 32)
    private String code;

    @NotBlank
    @Size(max = 64)
    private String name;

    @Email(message = "邮箱格式不正确")
    @Size(max = 128)
    private String email;

    @Pattern(regexp = "^[0-9+\\-]{5,20}$", message = "手机号格式不正确")
    @Size(max = 32)
    private String phone;

    @Size(max = 16)
    private String grade;

    @Size(max = 64)
    private String major;

    @Size(max = 255)
    private String remark;
}