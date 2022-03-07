package com.mbzshajib.assignment.analytic.configurations;

import com.mbzshajib.assignment.analytic.application.repository.InMemoryKeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.InMemoryQueueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.QueueDataRepository;
import com.mbzshajib.assignment.analytic.application.worker.AccumulatorJob;
import com.mbzshajib.assignment.analytic.application.worker.Job;
import com.mbzshajib.assignment.analytic.application.worker.SweepingJob;
import com.mbzshajib.assignment.analytic.application.worker.Worker;
import com.mbzshajib.assignment.analytic.models.TickDto;
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
                .forEach(id -> queues.add(new LinkedBlockingQueue<>()));
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
                configuration.getSweeperProcessMaxItemsInBatch(),
                configuration.getWindowSizeInSecond(),
                getKeyValueDataRepository()
        );
        sweeperExecutor.submit(new Worker(
                100,
                configuration.getWorkerThreadWaitTimeinmilis(),
                sweepingJob
        ));
        return sweeperExecutor;
    }
}
