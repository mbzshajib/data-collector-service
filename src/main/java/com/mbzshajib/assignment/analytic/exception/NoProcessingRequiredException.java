package com.mbzshajib.assignment.analytic.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoProcessingRequiredException extends RuntimeException {
    public NoProcessingRequiredException(String message){
        super(message);
    }
}
