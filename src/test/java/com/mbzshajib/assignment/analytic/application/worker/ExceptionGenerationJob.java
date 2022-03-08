package com.mbzshajib.assignment.analytic.application.worker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionGenerationJob implements Job{
    @Override
    public void doNow() {
        log.info("Job error testing for {} ",Thread.currentThread().getName());
        throw new IllegalArgumentException("Testing");

    }
}
