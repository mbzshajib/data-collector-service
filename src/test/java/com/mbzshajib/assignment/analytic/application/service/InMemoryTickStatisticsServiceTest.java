package com.mbzshajib.assignment.analytic.application.service;

import com.mbzshajib.assignment.analytic.application.exception.DataNotFoundException;
import com.mbzshajib.assignment.analytic.application.repository.InMemoryKeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.Pair;
import com.mbzshajib.assignment.analytic.application.utils.Utility;
import com.mbzshajib.assignment.analytic.configurations.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.models.StatisticResponse;
import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryTickStatisticsServiceTest {
    private InMemoryTickStatisticsService service;
    @MockBean
    private ApplicationConfiguration configuration;
    private KeyValueDataRepository repository;
    private Instant now;


    @BeforeEach
    void init() {
        now = Instant.now();
        configuration = mock(ApplicationConfiguration.class);
        repository = new InMemoryKeyValueDataRepository(new ConcurrentHashMap<>());
        when(configuration.getCollectorThreadCount()).thenReturn(10);
        when(configuration.getWindowSizeInSecond()).thenReturn(15);
        service = new InMemoryTickStatisticsService(configuration, repository);
    }

    @Test
    void testGetStatisticsForNoDataShouldThrowException() {
        assertThrows(DataNotFoundException.class, () -> service.getStatistics(now));
        repository.saveOrUpdate(key(1, "ABCD"), StatisticsDTO.builder().build());
        assertThrows(DataNotFoundException.class, () -> service.getStatistics(now));
        repository.saveOrUpdate(key(1, ""), StatisticsDTO.builder().build());
        assertDoesNotThrow(() -> service.getStatistics(now));
        repository.cleanUp(key(1, ""));
        assertThrows(DataNotFoundException.class, () -> service.getStatistics(now));
    }

    @Test
    void testGetStatisticsValidateCalculation() {
        List<Pair> sampleInput = sampleStatisticsDto("");
        insertSampleData(sampleInput);
        StatisticResponse result = service.getStatistics(now);
        assertNotNull(result);
        Double expectedMin = 101.0;
        Double expectedMax = 508.0;
        Integer expectedCount = IntStream.range(1, configuration.getCollectorThreadCount()).sum();
        Double expectedTotal = IntStream.range(1, configuration.getCollectorThreadCount()).map(id -> 50000 + id).asDoubleStream().sum();
        Double expectedAvg = expectedTotal / expectedCount;

        assertEquals(expectedMin, result.getMin());
        assertEquals(expectedMax, result.getMax());
        assertEquals(expectedAvg, result.getAvg());
        assertEquals(expectedCount, result.getCount());

    }

    @Test
    void testGetStatisticsByIdValidateCalculation() {
        List<Pair> sampleInput = new ArrayList<>();
        IntStream.rangeClosed(10,20).forEach((id)->sampleInput.addAll(  sampleStatisticsDto("instrument_id_"+id)));
        insertSampleData(sampleInput);
        IntStream.rangeClosed(10,20).forEach((index)-> {
            StatisticResponse result = service.getStatisticsByInstrumentId("instrument_id_"+index, now);
            assertNotNull(result);
            Double expectedMin = 101.0;
            Double expectedMax = 508.0;
            Integer expectedCount = IntStream.range(1, configuration.getCollectorThreadCount()).sum();
            Double expectedTotal = IntStream.range(1, configuration.getCollectorThreadCount()).map(id -> 50000 + id).asDoubleStream().sum();
            Double expectedAvg = expectedTotal / expectedCount;

            assertEquals(expectedMin, result.getMin());
            assertEquals(expectedMax, result.getMax());
            assertEquals(expectedAvg, result.getAvg());
            assertEquals(expectedCount, result.getCount());
        });

    }

    private String key(Integer processId, String instrument) {
        return Utility.formatStatisticStorageKey(processId, instrument, Utility.convertToKeyTime(now));
    }

    private List<Pair> sampleStatisticsDto(String instrument) {
        return IntStream.rangeClosed(1, configuration.getCollectorThreadCount())
                .mapToObj(id ->
                        Pair.builder()
                                .key(Utility.formatStatisticStorageKey(
                                        id,
                                        instrument,
                                        Utility.convertToKeyTime(now.minus(id % 60, ChronoUnit.SECONDS)))
                                )
                                .value(
                                        StatisticsDTO
                                                .builder()
                                                .min(100 + (id))
                                                .max(500 + (id % 9))
                                                .total(50000 + id)
                                                .count(id)
                                                .timeKey(Integer.parseInt(Utility.convertToKeyTime(now)))
                                                .build()
                                ).build()
                ).collect(Collectors.toList());
    }

    void insertSampleData(List<Pair> statisticsData) {
        statisticsData.forEach(pair -> repository.saveOrUpdate(pair.getKey(), pair.getValue()));
    }


}