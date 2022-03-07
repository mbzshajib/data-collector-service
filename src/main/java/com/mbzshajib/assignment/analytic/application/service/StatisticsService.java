package com.mbzshajib.assignment.analytic.application.service;

import com.mbzshajib.assignment.analytic.models.StatisticResponse;

import java.time.Instant;

public interface StatisticsService {
    StatisticResponse getStatistics(Instant windowEndTime);

    StatisticResponse getStatisticsByInstrumentId(String instrumentId, Instant windowEndTime);
}
