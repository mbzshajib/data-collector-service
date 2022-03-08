package com.mbzshajib.assignment.analytic.application.service;

import com.mbzshajib.assignment.analytic.application.exception.DataNotFoundException;
import com.mbzshajib.assignment.analytic.application.repository.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.utils.Constants;
import com.mbzshajib.assignment.analytic.application.utils.Utility;
import com.mbzshajib.assignment.analytic.configurations.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.models.StatisticResponse;
import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static com.mbzshajib.assignment.analytic.application.utils.Constants.Common.GLOBAL_KEY;

@Service
@RequiredArgsConstructor
public class InMemoryTickStatisticsService implements StatisticsService {
    private final ApplicationConfiguration configuration;
    private final KeyValueDataRepository storage;

    @Override
    public StatisticResponse getStatistics(Instant windowEndTime) {
        return getStatisticsData(GLOBAL_KEY, windowEndTime);
    }

    @Override
    public StatisticResponse getStatisticsByInstrumentId(String instrumentId, Instant windowEndTime) {
        return getStatisticsData(instrumentId, windowEndTime);
    }

    private StatisticResponse getStatisticsData(String instrumentId, Instant windowEndTime) {
        var secondsInTimeWindow = Utility.getSecondsInATimeWindow(windowEndTime, configuration.getWindowSizeInSecond());
        var response = prepareEmptyResponse();
        var MIN_INDEX = 0;
        var found = new AtomicBoolean(false);
        IntStream.range(MIN_INDEX, configuration.getCollectorThreadCount())
                .forEach(id -> secondsInTimeWindow
                        .forEach(entry -> {
                            String key = Utility.formatStorageKey(id, instrumentId, entry);
                            Optional<StatisticsDTO> statisticsDTO = storage.get(key);
                            if (statisticsDTO.isPresent()) {
                                found.set(true);
                                var dto = statisticsDTO.get();
                                var count = response.getCount() + dto.getCount();
                                var total = response.getAvg() * response.getCount() + dto.getTotal();
                                var min = Math.min(response.getMin(), dto.getMin());
                                var max = Math.max(response.getMax(), dto.getMax());
                                response.setCount(count);
                                response.setAvg(total / count);
                                response.setMin(min);
                                response.setMax(max);
                            }
                        }));
        if (!found.get()) throw new DataNotFoundException("No statistics data found for '" + instrumentId + "'.");
        return response;
    }

    private StatisticResponse prepareEmptyResponse() {
        return StatisticResponse
                .builder()
                .avg(0.0)
                .count(0)
                .min(Double.MAX_VALUE)
                .max(Double.MIN_VALUE)
                .build();
    }
}