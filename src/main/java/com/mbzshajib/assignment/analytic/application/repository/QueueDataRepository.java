package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.models.TickDto;

import java.util.Queue;

public interface QueueDataRepository {
    int size();

    void add(int queueIndex, TickDto data);

    Queue<TickDto> getQueue(int queueIndex);
}