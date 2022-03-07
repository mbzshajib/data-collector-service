package com.mbzshajib.assignment.analytic.config;

import com.mbzshajib.assignment.analytic.model.TickDto;
import com.mbzshajib.assignment.analytic.storage.InMemoryKeyValueDataRepository;
import com.mbzshajib.assignment.analytic.storage.InMemoryQueueDataRepository;
import com.mbzshajib.assignment.analytic.storage.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.storage.QueueDataRepository;
import com.mbzshajib.assignment.analytic.worker.AccumulatorJob;
import com.mbzshajib.assignment.analytic.worker.Job;
import com.mbzshajib.assignment.analytic.worker.SweepingJob;
import com.mbzshajib.assignment.analytic.worker.Worker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WorkerThreadConfiguration {

    @Bean
    public QueueDataRepository getQueueDataRepository(ApplicationConfiguration configuration) {
        List<Queue<TickDto>> queues = new ArrayList<>();
        IntStream.range(0, configuration.getCollectorThreadCount())
                .forEach(id -> {
                    queues.add(new LinkedBlockingQueue<>());
                });
        return new InMemoryQueueDataRepository(configuration.getCollectorThreadCount(), configuration.getMaxQueueSize(), queues);
    }

    @Bean
    public KeyValueDataRepository getKeyValueDataRepository() {
        return new InMemoryKeyValueDataRepository(new ConcurrentHashMap<>());
    }

    @Bean
    public ExecutorService getAccumulatorExecutorService(
            ApplicationConfiguration configuration
    ) {
        ExecutorService accumulatorExecutor = Executors.newFixedThreadPool(configuration.getCollectorThreadCount());
        IntStream.range(0, configuration.getCollectorThreadCount())
                .forEach(id -> {
                    Job accumulatingJob = new AccumulatorJob(
                            id,
                            getQueueDataRepository(configuration).getQueue(id),
                            getKeyValueDataRepository()
                    );
                    accumulatorExecutor.submit(new Worker(
                            id,
                            configuration.getWorkerThreadWaitTimeinmilis(),
                            accumulatingJob)
                    );
                });
        return accumulatorExecutor;
    }

    @Bean
    public ExecutorService getSweeperExecutorService(
            ApplicationConfiguration configuration) {
        ExecutorService sweeperExecutor = Executors.newFixedThreadPool(1);
        Job sweepingJob = new SweepingJob(
                configuration.getWindowSizeInSecond(),
                getKeyValueDataRepository(),
                configuration.getSweeperProcessMaxItemsInBatch()
        );
        sweeperExecutor.submit(new Worker(
                100,
                configuration.getWorkerThreadWaitTimeinmilis(),
                sweepingJob
        ));
        return sweeperExecutor;
    }
}
