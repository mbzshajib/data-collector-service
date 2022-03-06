package com.mbzshajib.assignment.analytic.service;

import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.exception.ServerOverloadedException;
import com.mbzshajib.assignment.analytic.model.TickDto;
import com.mbzshajib.assignment.analytic.storage.InMemoryQueueStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryTickCollectorService implements CollectorService {
    private final InMemoryQueueStorage storage;
    private final ApplicationConfiguration configuration;

    @Override
    public void collect(TickDto request) {
        Queue<TickDto> queue = storage.getQueue(getQueueBucketId(request.getInstrument(), request.getTime()));
        if (queue.size() > configuration.getMaxQueueSize()) {
            throw new ServerOverloadedException("Discarding the request due to server queue is full");
        }
        queue.add(request);
    }

    private int getQueueBucketId(String instrument, String time) {
        int finalHash = Math.abs(instrument.hashCode()) + Math.abs(time.hashCode());
        Integer bucket = (finalHash % configuration.getCollectorThreadCount());
        return bucket;
    }
}
