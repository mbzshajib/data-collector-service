package com.mbzshajib.assignment.analytic.storage;

import com.mbzshajib.assignment.analytic.exception.ServerOverloadedException;
import com.mbzshajib.assignment.analytic.model.TickDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
public class InMemoryQueueDataRepository implements QueueDataRepository {
    private static final int MIN_INDEX = 0;
    private final Integer size;
    private final Integer maxQueueSize;
    private final List<Queue<TickDto>> queues;


    @Override
    public int size() {
        log.info("Hash code {}", this.hashCode());
        return queues.size();
    }

    @Override
    public void add(int queueIndex, TickDto data) {
        log.info("Hash code {}", this.hashCode());
        if (size() > maxQueueSize) {
            throw new ServerOverloadedException("Discarding the request due to server queue is full");
        }
        getQueue(queueIndex).add(data);
    }

    @Override
    public Queue<TickDto> getQueue(int id) {
        log.info("Hash code {}", this.hashCode());
        if (id < MIN_INDEX || id >= size) {
            throw new IllegalArgumentException("Invalid argument id. Should be in between 0<=id<"
                    + size);
        }
        return queues.get(id);
    }
}
