package com.mbzshajib.assignment.analytic.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsDTO {
    private int timeKey;
    private double total;
    private double min;
    private double max;
    private int count;
}
