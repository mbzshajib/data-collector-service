package com.mbzshajib.assignment.analytic.configurations;

import com.mbzshajib.assignment.analytic.application.repository.InMemoryKeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.InMemoryQueueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.repository.QueueDataRepository;
import com.mbzshajib.assignment.analytic.application.worker.jobs.AccumulatorJob;
import com.mbzshajib.assignment.analytic.application.worker.jobs.Job;
import com.mbzshajib.assignment.analytic.application.worker.jobs.SweepingJob;
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
public class WorkerConfiguration {

    @Bean
    public QueueDataRepository getQueueDataRepository(ApplicationConfiguration configuration) {
        List<Queue<TickDto>> queues = new ArrayList<>();
        IntStream.range(0, configuration.getCollectorThreadCount())
                .forEach(id -> queues.add(new LinkedBlockingQueue<>()));
        return new InMemoryQueueDataRepository(configuration.getCollectorThreadCount(), configuration.getMaxSizePerQueue(), queues);
    }

    @Bean
    public KeyValueDataRepository getKeyValueDataRepository() {
        return new InMemoryKeyValueDataRepository(new ConcurrentHashMap<>());
    }

    /**
     * Configuring sweeper and binding the output repository to be sweeped
     * @param configuration application configuration
     * @return sweeper executor service
     */
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
                            configuration.getCollectorThreadWaitTimeinmilis(),
                            accumulatingJob)
                    );
                });
        return accumulatorExecutor;
    }

    /**
     * Configuring sweeper and binding the output repository to be clean
     * @param configuration application configuration
     * @return sweeper executor service
     */
    @Bean
    public ExecutorService getSweeperExecutorService(
            ApplicationConfiguration configuration) {
        ExecutorService sweeperExecutor = Executors.newFixedThreadPool(configuration.getSweeperThreadCount());
        Job sweepingJob = new SweepingJob(
                configuration.getSweeperProcessMaxItemsInBatch(),
                configuration.getWindowSizeInSecond(),
                getKeyValueDataRepository()
        );
        sweeperExecutor.submit(new Worker(
                configuration.getSweeperThreadCount(),
                configuration.getSweeperThreadWaitTimeinmilis(),
                sweepingJob
        ));
        return sweeperExecutor;
    }
}
