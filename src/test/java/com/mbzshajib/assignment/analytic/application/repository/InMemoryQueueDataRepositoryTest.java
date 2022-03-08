package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.application.exception.QueueOverFlowException;
import com.mbzshajib.assignment.analytic.models.TickDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryQueueDataRepositoryTest {
    private Integer size;
    private Integer maxQueueSize;
    private List<Queue<TickDto>> queue;
    private QueueDataRepository repository;

    @BeforeEach
    void init() {
        size = 10;
        maxQueueSize = 10;
        queue = new ArrayList<>(size);
        IntStream.range(0, size).forEach(id -> queue.add(new LinkedBlockingQueue<>()));
        repository = new InMemoryQueueDataRepository(size, maxQueueSize, queue);
    }

    @Test
    void testSize() {
        IntStream.range(0, size)
                .forEach(id -> assertEquals(0, repository.size(id)));
        IntStream.range(0, size)
                .forEach(id -> repository.add(id, TickDto.builder().build()));
        IntStream.range(0, size)
                .forEach(id -> assertEquals(1, repository.size(id)));
        IntStream.range(0, size)
                .forEach(id -> repository.poll(id));
        IntStream.range(0, size)
                .forEach(id -> assertEquals(0, repository.size(id)));
        IntStream.range(0, size * maxQueueSize)
                .forEach(id -> repository.add(id % size, TickDto.builder().build()));
        IntStream.range(0, size)
                .forEach(id -> assertEquals(maxQueueSize, repository.size(id)));
        assertThrows(QueueOverFlowException.class, () -> repository.add(0, TickDto.builder().build()));
    }

    @Test
    void testShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.size(100));
        assertThrows(IllegalArgumentException.class, () -> repository.add(100, TickDto.builder().build()));
        assertThrows(IllegalArgumentException.class, () -> repository.add(-1, TickDto.builder().build()));
        assertThrows(IllegalArgumentException.class, () -> repository.poll(100));
        assertThrows(IllegalArgumentException.class, () -> repository.poll(-1));
        assertThrows(IllegalArgumentException.class, () -> repository.size(100));
        assertThrows(IllegalArgumentException.class, () -> repository.size(-1));
    }

    @Test
    void testNullDto() {
        assertThrows(IllegalArgumentException.class, () -> repository.add(0, null));
    }

    @Test
    void testAddShouldInsertUpToMaxQueueSize() {
        IntStream.range(0, size)
                .forEach(id -> assertEquals(0, repository.size(id)));
        IntStream.range(0, size)
                .forEach(id -> repository.add(id, TickDto.builder().build()));
        IntStream.range(0, size)
                .forEach(id -> assertEquals(1, repository.size(id)));
        IntStream.range(0, size)
                .forEach(id -> repository.poll(id));
        IntStream.range(0, size)
                .forEach(id -> assertEquals(0, repository.size(id)));
        IntStream.range(0, size * maxQueueSize)
                .forEach(id -> repository.add(id % size, TickDto.builder().build()));
        IntStream.range(0, size)
                .forEach(id -> assertEquals(maxQueueSize, repository.size(id)));
        assertThrows(QueueOverFlowException.class, () -> repository.add(0, TickDto.builder().build()));

    }

    @Test
    void testAddShouldFailForOverFlow() {
        IntStream.range(0, size * maxQueueSize)
                .forEach(id -> repository.add(id % size, TickDto.builder().build()));
        IntStream.range(0, size)
                .forEach(id -> assertEquals(maxQueueSize, repository.size(id)));
        IntStream.range(0, size)
                .forEach(id -> assertThrows(QueueOverFlowException.class, () -> repository.add(id, TickDto.builder().build())));
    }

    @Test
    void testPoll() {
        IntStream.range(0, size)
                .forEach(id -> assertEquals(0, repository.size(id)));
        IntStream.range(0, size)
                .forEach(id -> assertFalse(repository.poll(id).isPresent()));

        IntStream.range(0, size * maxQueueSize)
                .forEach(id -> repository.add(id % size, TickDto.builder().build()));
        IntStream.range(0, size * maxQueueSize)
                .forEach(id -> assertTrue(repository.poll(id % size).isPresent()));
        IntStream.range(0, size)
                .forEach(id -> assertFalse(repository.poll(id).isPresent()));
    }


}