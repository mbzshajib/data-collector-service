package com.mbzshajib.assignment.analytic.storage;

import com.mbzshajib.assignment.analytic.model.TickDto;

import java.util.Queue;

public interface QueueDataRepository {
    int size();

    void add(int queueIndex, TickDto data);

    Queue<TickDto> getQueue(int queueIndex);
}
