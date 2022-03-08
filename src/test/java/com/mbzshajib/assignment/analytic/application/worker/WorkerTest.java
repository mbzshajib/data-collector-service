package com.mbzshajib.assignment.analytic.application.worker;

import com.mbzshajib.assignment.analytic.application.worker.job.ExceptionGenerationJob;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class WorkerTest {
    @Test
    void testExceptionInThreads() throws InterruptedException {
        Worker worker = new Worker(1, 1, new ExceptionGenerationJob());
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        executorService.submit(worker);
        Thread.sleep(1000);
        assertEquals(1, executorService.getActiveCount());
        executorService.shutdown();
    }

}