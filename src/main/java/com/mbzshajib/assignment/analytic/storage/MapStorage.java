package com.mbzshajib.assignment.analytic.storage;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;

import java.util.Map;


public interface MapStorage {
    Map<String, StatisticsDTO> getMap();
}

