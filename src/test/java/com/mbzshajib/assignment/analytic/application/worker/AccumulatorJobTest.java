package com.mbzshajib.assignment.analytic.application.worker;

import com.mbzshajib.assignment.analytic.application.repository.InMemoryKeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.utils.Utility;
import com.mbzshajib.assignment.analytic.models.TickDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import static com.mbzshajib.assignment.analytic.common.TestConstants.TestData.INSTRUMENT_ID;

class AccumulatorJobTest {
    private AccumulatorJob job;
    private Queue<TickDto> inputRepository;
    private KeyValueDataRepository outputRepository;

    @BeforeEach
    void init() {
        inputRepository = new LinkedBlockingQueue<>();
        outputRepository = new InMemoryKeyValueDataRepository(new HashMap<>());
        job = new AccumulatorJob(10, inputRepository, outputRepository);
    }

    @Test
    void testDoNowEmptyInput() {
        Assertions.assertEquals(10, job.getId());
        Assertions.assertEquals(0, job.getInputRepository().size());
        Assertions.assertEquals(0, job.getInputRepository().size());
        job.doNow();
        Assertions.assertEquals(10, job.getId());
        Assertions.assertEquals(0, job.getInputRepository().size());
        Assertions.assertEquals(0, job.getInputRepository().size());
    }

    @Test
    void testDoNowWithInput() {
        Assertions.assertEquals(10, job.getId());
        Assertions.assertEquals(0, job.getInputRepository().size());
        Assertions.assertEquals(0, job.getOutputRepository().size());
        Instant now = Instant.now();
        int count = 100;
        IntStream.range(0, count).forEach(id -> {
            inputRepository.add(
                    TickDto
                            .builder()
                            .instrument(INSTRUMENT_ID[id % INSTRUMENT_ID.length])
                            .price(100 * 1.0 * id)
                            .timeKey(Utility.convertToKeyTime(now)).build());
        });
        Assertions.assertEquals(10, job.getId());
        Assertions.assertEquals(count, job.getInputRepository().size());
        Assertions.assertEquals(0, job.getOutputRepository().size());
        IntStream.range(0, 100).forEach(id -> job.doNow());
        Assertions.assertEquals(10, job.getId());
        Assertions.assertEquals(0, job.getInputRepository().size());
        Assertions.assertEquals(INSTRUMENT_ID.length + 1, job.getOutputRepository().size());
    }

}