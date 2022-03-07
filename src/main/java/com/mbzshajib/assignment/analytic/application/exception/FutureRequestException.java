package com.mbzshajib.assignment.analytic.application.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FutureRequestException extends IllegalArgumentException {
    public FutureRequestException(String message) {
        super(message);
    }
}
