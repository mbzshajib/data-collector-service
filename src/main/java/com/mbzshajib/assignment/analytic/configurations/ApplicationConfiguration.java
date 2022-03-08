package com.mbzshajib.assignment.analytic.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "data.collector")
public class ApplicationConfiguration {
    private Integer windowSizeInSecond;
    private Integer maxSizePerQueue;
    private Integer maxApiResponseTimeinmilis;
    //Collector Configuration
    private Integer collectorThreadCount;
    private Integer collectorThreadWaitTimeinmilis;
    //Sweeper Configuration
    private Integer sweeperThreadCount;
    private Integer sweeperThreadWaitTimeinmilis;
    private Integer sweeperProcessMaxItemsInBatch;
}
