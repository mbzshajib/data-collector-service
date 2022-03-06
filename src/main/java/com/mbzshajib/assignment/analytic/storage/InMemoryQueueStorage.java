package com.mbzshajib.assignment.analytic.storage;

import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.model.TickDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class InMemoryQueueStorage implements QueueStorage {
    private static final int MIN_INDEX = 0;
    private final ApplicationConfiguration configuration;
    private List<Queue<TickDto>> storage;

    @PostConstruct
    private void init() {
        storage = new ArrayList<>(configuration.getCollectorThreadCount());
        IntStream.range(0, configuration.getCollectorThreadCount())
                .forEach(id -> {
                    storage.add(id, new LinkedBlockingQueue<>());
                });
    }

    public Queue<TickDto> getQueue(int id) {
        if (id < MIN_INDEX || id >= configuration.getCollectorThreadCount()) {
            throw new IllegalArgumentException("Invalid argument id. Should be in between 0<=id<"
                    + configuration.getCollectorThreadCount());
        }
        return storage.get(id);
    }
}
