package com.mbzshajib.assignment.analytic.annotations;

import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class ResponseTimeWarningAspect {
    private final ApplicationConfiguration configuration;

    @Pointcut("@annotation(EnableResponseTimeWarning)")
    public void loggablePointcut() {
    }

    @Around("loggablePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();
        try {
            Object result = joinPoint.proceed();
            Instant end = Instant.now();
            printLog(start, end);
            return result;
        } catch (Exception exception) {
            Instant end = Instant.now();
            printLog(start, end);
            throw exception;
        }
    }

    private void printLog(Instant start, Instant end) {
        boolean printable = isPrintable(start, end);
        if (printable)
            log.warn("Processing time required {}ms", (end.toEpochMilli() - start.toEpochMilli()));
    }

    private boolean isPrintable(Instant start, Instant end) {
        if ((end.toEpochMilli() - start.toEpochMilli()) > configuration.getMaxApiResponseTimeinmilis())
            return true;
        return false;
    }
}
