package com.mbzshajib.assignment.analytic.worker;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import com.mbzshajib.assignment.analytic.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Slf4j
class SweeperWorkerTest {
    @Test
    void testSweepingTask() throws InterruptedException {
        ConcurrentHashMap<String, StatisticsDTO> map = new ConcurrentHashMap<>();
        Instant now = Instant.now();
        putDataOnMap(map, 100, now);
        AbstractSweeperWorker sweeperWorker = new SweeperWorker(100, map, 100, 100, 5);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(sweeperWorker);

        assertEquals(100, map.size(), "Sweeping task will not clean present data");

        Thread.sleep(7200);
        now = Instant.now();
        assertEquals(0, map.size(), "Sweeping task did not cleaned the ");

        putDataOnMap(map, 1000, now.minus(5, ChronoUnit.SECONDS));
        Thread.sleep(2000);
        assertEquals(0, map.size(), "Map must clean old data");

        Instant futureTime = now.plus(5, ChronoUnit.SECONDS);
        putDataOnMap(map, 100, futureTime);
        Thread.sleep(100);
        assertEquals(100, map.size(), "Sweeping task will not clean future data");

        Thread.sleep(11000);
        assertEquals(0, map.size(), "Sweeping task will clean all data");
        executorService.shutdown();
    }

    private void putDataOnMap(ConcurrentHashMap<String, StatisticsDTO> map, int size, Instant now) {
        IntStream.rangeClosed(1, size)
                .forEach(id -> map.put(("Key:" + String.format("%3d", id)),
                                StatisticsDTO
                                        .builder()
                                        .timeKey(Integer.parseInt(Utility.convertToKeyTime(now)))
                                        .build()
                        )

                );
    }

}