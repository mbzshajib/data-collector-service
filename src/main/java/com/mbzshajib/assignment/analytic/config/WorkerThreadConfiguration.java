package com.mbzshajib.assignment.analytic.config;

import com.mbzshajib.assignment.analytic.storage.InMemoryAccumulatedStorage;
import com.mbzshajib.assignment.analytic.storage.InMemoryQueueStorage;
import com.mbzshajib.assignment.analytic.worker.AccumulatorWorker;
import com.mbzshajib.assignment.analytic.worker.SweeperWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WorkerThreadConfiguration {
    private final ApplicationConfiguration configuration;
    private final InMemoryQueueStorage inputStorage;
    private final InMemoryAccumulatedStorage outputStorage;
    private ExecutorService workerExecutor;
    private ExecutorService sweeperExecutor;


    @PostConstruct
    private void initializeWorkerThreads() {
        workerExecutor = Executors.newFixedThreadPool(configuration.getCollectorThreadCount());
        sweeperExecutor = Executors.newFixedThreadPool(1);
        IntStream.range(0, configuration.getCollectorThreadCount())
                .forEach(id -> {
                    workerExecutor.submit(new AccumulatorWorker(
                            id,
                            inputStorage.getQueue(id),
                            outputStorage.getMap(),
                            configuration.getWorkerThreadWaitTimeinmilis())
                    );
                });
        sweeperExecutor.submit(new SweeperWorker(
                0,
                outputStorage.getMap(),
                configuration.getWorkerThreadWaitTimeinmilis(),
                configuration.getSweeperProcessMaxItemsInBatch(),
                configuration.getWindowSizeInSecond())
        );
    }
}
