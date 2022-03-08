package com.mbzshajib.assignment.analytic.application.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueueOverFlowException extends RuntimeException {
    public QueueOverFlowException(String message) {
        super(message);
    }
}
