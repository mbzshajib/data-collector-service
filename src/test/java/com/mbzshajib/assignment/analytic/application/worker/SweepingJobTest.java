package com.mbzshajib.assignment.analytic.application.worker;

import com.mbzshajib.assignment.analytic.application.repository.InMemoryKeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.utils.Utility;
import com.mbzshajib.assignment.analytic.common.TestConstants;
import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SweepingJobTest {
    private Instant instant;
    private SweepingJob job;
    private KeyValueDataRepository repository;

    @BeforeEach
    void init() {
        instant = Instant.now();
        repository = new InMemoryKeyValueDataRepository(new HashMap<>());
        job = new SweepingJob(10, 5, repository);
    }

    @Test
    void testDoNowEmptyInput() {
        assertEquals(0, repository.size());
        job.doNow();
        assertEquals(0, repository.size());
    }

    @Test
    void testDoNowNotExpired() {
        assertEquals(0, repository.size());
        repository.saveOrUpdate(TestConstants.TestData.INSTRUMENT_ID[0],
                StatisticsDTO.builder()
                        .count(1)
                        .max(1)
                        .min(1)
                        .timeKey(Integer.parseInt(Utility.convertToKeyTime(instant)))
                        .build());
        job.doNow();
        assertEquals(1, repository.size());
    }

    @Test
    void testDoNowExpired() throws InterruptedException {
        assertEquals(0, repository.size());
        repository.saveOrUpdate(TestConstants.TestData.INSTRUMENT_ID[0],
                StatisticsDTO.builder()
                        .count(1)
                        .max(1)
                        .min(1)
                        .timeKey(Integer.parseInt(Utility.convertToKeyTime(instant)))
                        .build());
        Thread.sleep((job.getWindowSizeInSecond() + 2) * 1000);
        job.doNow();
        assertEquals(0, repository.size());
    }

    @Test
    void testDoNowExpiredFullBatch() throws InterruptedException {
        assertEquals(0, repository.size());
        IntStream.range(0, job.getProcessingBatchSize()).forEach(id ->
                repository.saveOrUpdate(Utility.formatStatisticStorageKey(
                                1,
                                Utility.convertToKeyTime(instant.plus(id, ChronoUnit.SECONDS)),
                                TestConstants.TestData.INSTRUMENT_ID[0]
                        ),
                        StatisticsDTO.builder()
                                .count(1).max(1).min(1)
                                .timeKey(Integer.parseInt(Utility.convertToKeyTime(instant)))
                                .build()));
        assertEquals(job.getProcessingBatchSize(), repository.size());
        Thread.sleep((job.getWindowSizeInSecond() + 2) * 1000);
        job.doNow();
        assertEquals(0, repository.size());
    }

}