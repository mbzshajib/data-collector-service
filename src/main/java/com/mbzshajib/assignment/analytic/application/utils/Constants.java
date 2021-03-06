package com.mbzshajib.assignment.analytic.application.utils;

public interface Constants {
    interface Common {
        String KEY_SEPARATOR = ".";
        String KEY_DATE_FORMAT = "HHmmss";
        String KEY_THREAD_ID_FORMAT = "%02d";
        String GLOBAL_KEY = "";
        Integer PRECISION = 2;
        String PRECISION_FORMAT = "#0.00";
    }

    interface HttpMethod {
        String POST = "POST";
        String GET = "GET";
    }

    interface Api {
        String TICK = "/ticks";
        String STATISTICS = "/statistics";
        String STATISTICS_BY_INSTRUMENT = "/statistics/";
    }
}
