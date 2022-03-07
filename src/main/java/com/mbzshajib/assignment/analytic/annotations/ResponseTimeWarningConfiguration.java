package com.mbzshajib.assignment.analytic.annotations;

import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class ResponseTimeWarningConfiguration {

    public ResponseTimeWarningAspect loggingAspect(ApplicationConfiguration configuration) {
        return new ResponseTimeWarningAspect(configuration);
    }
}
