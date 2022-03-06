package com.mbzshajib.assignment.analytic.service;

import com.mbzshajib.assignment.analytic.model.StatisticResponse;

import java.time.Instant;

public interface StatisticsService {
    StatisticResponse getStatistics(Instant windowEndTime);

    StatisticResponse getStatisticsByInstrumentId(String instrumentId, Instant windowEndTime);
}
