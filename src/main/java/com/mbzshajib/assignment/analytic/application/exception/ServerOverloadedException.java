package com.mbzshajib.assignment.analytic.application.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerOverloadedException extends RuntimeException {
    public ServerOverloadedException(String message) {
        super(message);
    }
}
