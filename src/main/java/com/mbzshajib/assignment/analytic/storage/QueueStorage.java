package com.mbzshajib.assignment.analytic.storage;

import com.mbzshajib.assignment.analytic.model.TickDto;

import java.util.Queue;

public interface QueueStorage {
    Queue<TickDto> getQueue(int queueIndex);
}
