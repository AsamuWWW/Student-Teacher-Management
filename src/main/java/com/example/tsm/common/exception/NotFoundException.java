package com.example.tsm.common.exception;

public class NotFoundException extends RuntimeException {
    private final int code;

    public NotFoundException(String message) {
        super(message);
        this.code = 40400;
    }

    public int getCode() { return code; }
}