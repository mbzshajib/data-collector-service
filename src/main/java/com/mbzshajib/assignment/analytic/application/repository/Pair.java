package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Pair {
    private String key;
    private StatisticsDTO value;
}
