package com.mbzshajib.assignment.analytic.worker;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import com.mbzshajib.assignment.analytic.model.TickDto;
import com.mbzshajib.assignment.analytic.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class AccumulatorWorkerTest {
    private AbstractAccumulatorWorker worker1;
    private AbstractAccumulatorWorker worker2;
    private Queue<TickDto> inputQueue1;
    private Queue<TickDto> inputQueue2;
    private Map<String, StatisticsDTO> output;
    private String INSTRUMENTID[] = new String[]{"ABCD", "EFGH", "XYZ"};
    private ExecutorService executorService;
    private Instant startTime;


    @Test
    @DisplayName("Simulating Two thread with ")
    void testTwoThreadSimulations() throws InterruptedException {
        inputQueue1 = new LinkedBlockingQueue<>();
        inputQueue2 = new LinkedBlockingQueue<>();
        startTime = Instant.now();
        IntStream.rangeClosed(1, 10000)
                .forEach(
                        id -> {
                            inputQueue1.add(TickDto
                                    .builder()
                                    .instrument(INSTRUMENTID[id % INSTRUMENTID.length])
                                    .price((id % 200) * 1.0)
                                    .time(Utility.convertToKeyTime(startTime))
                                    .build());
                            inputQueue2.add(TickDto
                                    .builder()
                                    .instrument(INSTRUMENTID[id % INSTRUMENTID.length])
                                    .price((id % 200) * 2.0)
                                    .time(Utility.convertToKeyTime(startTime))
                                    .build());
                        }
                );
        output = new ConcurrentHashMap<>();
        this.worker1 = new AccumulatorWorker(1, inputQueue1, output, 0);
        this.worker2 = new AccumulatorWorker(2, inputQueue2, output, 0);
        executorService = Executors.newFixedThreadPool(2);
        executorService.submit(worker1);
        executorService.submit(worker2);
        Thread.sleep(5000);
        executorService.shutdown();
        assertEquals(0, inputQueue1.size(), "For worker 1 for Output queue 1 processing should be complete");
        assertEquals(0, inputQueue2.size(), "For worker 2 for Output queue 2 processing should be complete");
        assertEquals(8, output.size(), "Queue processing must be completed");
        executorService.shutdown();
    }

    @Test
    @DisplayName("Simulate Correctness for all keys processed")
    void testCorrectness() throws InterruptedException {
        inputQueue1 = new LinkedBlockingQueue<>();
        inputQueue2 = new LinkedBlockingQueue<>();
        startTime = Instant.now();
        IntStream.rangeClosed(1, 10000)
                .forEach(
                        id -> {
                            inputQueue1.add(TickDto
                                    .builder()
                                    .instrument(INSTRUMENTID[id % INSTRUMENTID.length])
                                    .price((id % 200) * 1.0)
                                    .time(Utility.convertToKeyTime(startTime))
                                    .build());
                            inputQueue2.add(TickDto
                                    .builder()
                                    .instrument(INSTRUMENTID[id % INSTRUMENTID.length])
                                    .price((id % 200) * 2.0)
                                    .time(Utility.convertToKeyTime(startTime))
                                    .build());
                        }
                );
        output = new ConcurrentHashMap<>();
        this.worker1 = new AccumulatorWorker(1, inputQueue1, output, 0);
        this.worker2 = new AccumulatorWorker(2, inputQueue2, output, 0);
        executorService = Executors.newFixedThreadPool(2);
        executorService.submit(worker1);
        executorService.submit(worker2);
        Thread.sleep(5000);
        executorService.shutdown();
        String keyForTime = Utility.convertToKeyTime(startTime.plus(1, ChronoUnit.MILLIS));
        Set<String> keyList = new LinkedHashSet<>();
        Arrays.stream(INSTRUMENTID)
                .forEach(instrumentId -> {
                            keyList.add(Utility.formatStatisticStorageKey(1, instrumentId, keyForTime));
                            keyList.add(Utility.formatStatisticStorageKey(2, instrumentId, keyForTime));

                        }
                );
        keyList.add(Utility.formatStatisticStorageKey(1, "", keyForTime));
        keyList.add(Utility.formatStatisticStorageKey(2, "", keyForTime));
        assertEquals(0, inputQueue1.size(), "For worker 1 for Output queue 1 processing should be complete");
        assertEquals(0, inputQueue2.size(), "For worker 2 for Output queue 2 processing should be complete");
        assertEquals(8, output.size(), "Queue processing must be completed");
        assertTrue(output.keySet().containsAll(keyList), "Must contains all keys");
        executorService.shutdown();
    }
}