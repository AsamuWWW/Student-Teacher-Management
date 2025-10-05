package com.example.tsm.dto.enrollment;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Data;

@Data
public class EnrollmentGradeUpdateReq {

    @DecimalMin(value = "0.0", message = "成绩不能小于0")
    @DecimalMax(value = "100.0", message = "成绩不能大于100")
    @Digits(integer = 3, fraction = 2, message = "成绩格式最大 3 位整数 2 位小数")
    private String grade; // 用字符串方便前端传递，Service 再转 BigDecimal
}