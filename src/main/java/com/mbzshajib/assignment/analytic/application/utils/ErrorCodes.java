package com.mbzshajib.assignment.analytic.application.utils;

public interface ErrorCodes {
    interface Codes {
        String INVALID_REQUEST_DATA = "2001";
        String TICK_FROM_FUTURE = "2002";
        String UNKNOWN_ERROR = "9999";
    }

    interface Messages {
        String UNKNOWN_ERROR_MSG = "Unknown error happened";
    }
}
