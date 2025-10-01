package com.example.tsm.controller;

import com.example.tsm.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/api/v1/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("pong");
    }
}