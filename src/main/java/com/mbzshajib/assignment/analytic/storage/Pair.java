package com.mbzshajib.assignment.analytic.storage;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair {
    private String key;
    private StatisticsDTO value;
}
