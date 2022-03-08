package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.application.exception.QueueOverFlowException;
import com.mbzshajib.assignment.analytic.models.TickDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

@Slf4j
@NonNull
@RequiredArgsConstructor
public class InMemoryQueueDataRepository implements QueueDataRepository {
    private static final int MIN_INDEX = 0;
    @NonNull
    private final Integer size;
    @NonNull
    private final Integer maxQueueSize;
    @NonNull
    private final List<Queue<TickDto>> queues;


    @Override
    public int size(int queueIndex) {
        checkIndex(queueIndex);
        return queues.get(queueIndex).size();
    }

    @Override
    public void add(int queueIndex, TickDto data) {
        if(data==null)throw new IllegalArgumentException("data can not be null");
        checkIndex(queueIndex);
        checkLimit(queueIndex);
        queues.get(queueIndex).add(data);
    }

    @Override
    public Optional<TickDto> poll(int queueIndex) {
        checkIndex(queueIndex);
        return Optional.ofNullable(queues.get(queueIndex).poll());
    }

    @Override
    public Queue<TickDto> getQueue(int queueIndex) {
        checkIndex(queueIndex);
        return queues.get(queueIndex);
    }

    private void checkLimit(int queueIndex) {
        if (size(queueIndex) >= maxQueueSize) {
            throw new QueueOverFlowException("Discarding the request due to server queue is full");
        }
    }

    private void checkIndex(int queueIndex) {
        if (queueIndex < MIN_INDEX || queueIndex >= size) {
            throw new IllegalArgumentException("Invalid argument queueIndex. Should be in between 0<=queueSize"
                    + size+"<");
        }
    }
}
