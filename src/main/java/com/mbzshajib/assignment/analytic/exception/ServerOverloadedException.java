package com.mbzshajib.assignment.analytic.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerOverloadedException extends RuntimeException {
    public ServerOverloadedException(String message){
        super(message);
    }
}
