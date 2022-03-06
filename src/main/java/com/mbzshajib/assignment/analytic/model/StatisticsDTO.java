package com.mbzshajib.assignment.analytic.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsDTO {
    private Integer timeKey;
    private double total;
    private double min;
    private double max;
    private Integer count;
}
