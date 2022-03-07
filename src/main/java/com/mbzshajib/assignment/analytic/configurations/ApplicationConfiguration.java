package com.mbzshajib.assignment.analytic.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "tick.analytic")
public class ApplicationConfiguration {
    private Integer collectorThreadCount;
    private Integer windowSizeInSecond;
    private Integer maxQueueSize;
    private Integer workerThreadWaitTimeinmilis;
    private Integer maxApiResponseTimeinmilis;
    private Integer sweeperProcessMaxItemsInBatch;
}
