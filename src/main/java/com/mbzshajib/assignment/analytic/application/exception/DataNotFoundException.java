package com.mbzshajib.assignment.analytic.application.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
