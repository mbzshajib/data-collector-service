package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.models.TickDto;
import lombok.NonNull;

import java.util.Optional;
import java.util.Queue;

public interface QueueDataRepository {
    int size(int queueIndex);

    void add(int queueIndex, @NonNull TickDto data);

    Optional<TickDto> poll(int queueIndex);

    Queue<TickDto> getQueue(int queueIndex);
}
