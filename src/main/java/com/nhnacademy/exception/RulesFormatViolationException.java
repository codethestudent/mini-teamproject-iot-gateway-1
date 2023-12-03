package com.nhnacademy.exception;

public class RulesFormatViolationException extends RuntimeException {
    public RulesFormatViolationException() {
        super();
    }

    public RulesFormatViolationException(String message) {
        super(message);
    }
}