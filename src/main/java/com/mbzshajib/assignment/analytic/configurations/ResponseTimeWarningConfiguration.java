package com.mbzshajib.assignment.analytic.configurations;

import com.mbzshajib.assignment.analytic.application.annotations.ResponseTimeWarningAspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class ResponseTimeWarningConfiguration {
    public ResponseTimeWarningAspect loggingAspect(ApplicationConfiguration configuration) {
        return new ResponseTimeWarningAspect(configuration);
    }
}
