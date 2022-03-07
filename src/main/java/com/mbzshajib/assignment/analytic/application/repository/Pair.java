package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair {
    private String key;
    private StatisticsDTO value;
}
