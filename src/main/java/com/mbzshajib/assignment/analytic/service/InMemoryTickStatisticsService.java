package com.mbzshajib.assignment.analytic.service;

import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.model.StatisticResponse;
import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import com.mbzshajib.assignment.analytic.storage.MapStorage;
import com.mbzshajib.assignment.analytic.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class InMemoryTickStatisticsService implements StatisticsService {
    private final int MIN_INDEX = 0;
    private final ApplicationConfiguration configuration;
    private final MapStorage storage;

    @Override
    public StatisticResponse getStatistics(Instant windowEndTime) {
        return getStatisticsData("", windowEndTime);
    }

    @Override
    public StatisticResponse getStatisticsByInstrumentId(String instrumentId, Instant windowEndTime) {
        return getStatisticsData(instrumentId, windowEndTime);
    }

    private StatisticResponse getStatisticsData(String instrumentId, Instant windowEndTime) {
        List<String> secondsInTimeWindow = Utility.getAllSecondsInTimeWindow(windowEndTime, configuration.getWorkerThreadWaitTimeinmilis());
        StatisticResponse response = prepareEmptyResponse();
        IntStream.range(MIN_INDEX, configuration.getCollectorThreadCount())
                .forEach(id -> {
                    secondsInTimeWindow.stream()
                            .forEach(entry -> {
                                String key = Utility.formatStatisticStorageKey(id, instrumentId, entry);
                                if (storage.getMap().containsKey(key)) {
                                    StatisticsDTO dto = storage.getMap().get(key);
                                    var count = response.getCount() + dto.getCount();
                                    var total = response.getAvg() * response.getCount() + dto.getTotal();
                                    var min = Math.min(response.getMin(), dto.getMin());
                                    var max = Math.max(response.getMax(), dto.getMax());
                                    response.setCount(count);
                                    response.setAvg(total / count);
                                    response.setMin(min);
                                    response.setMax(max);
                                }
                            });
                });
        return response;
    }

    private StatisticResponse prepareEmptyResponse() {
        var response = StatisticResponse
                .builder()
                .avg(0.0)
                .count(0)
                .min(Double.MAX_VALUE)
                .max(Double.MIN_VALUE)
                .build();
        return response;
    }
}