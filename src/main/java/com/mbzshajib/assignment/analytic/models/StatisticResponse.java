package com.mbzshajib.assignment.analytic.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticResponse {

    private Double avg;
    private Integer count;
    private Double max;
    private Double min;

}
