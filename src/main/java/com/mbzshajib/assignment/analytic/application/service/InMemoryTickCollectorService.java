package com.mbzshajib.assignment.analytic.application.service;

import com.mbzshajib.assignment.analytic.application.exception.FutureRequestException;
import com.mbzshajib.assignment.analytic.application.exception.NoProcessingRequiredException;
import com.mbzshajib.assignment.analytic.application.repository.QueueDataRepository;
import com.mbzshajib.assignment.analytic.application.utils.Utility;
import com.mbzshajib.assignment.analytic.configurations.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.models.TickDto;
import com.mbzshajib.assignment.analytic.models.TickRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryTickCollectorService implements CollectorService {
    private final QueueDataRepository storage;
    private final ApplicationConfiguration configuration;

    @Override
    public void collect(TickRequest request) {
        var now = Instant.now();
        validateRequestTime(request, now);
        var dto = convertRequestToDto(request);
        storage.add(getQueueBucketId(dto.getInstrument(), dto.getTimeKey()), dto);

    }

    private void validateRequestTime(TickRequest request, Instant startTime) {
        var timeDiffSeconds = Utility.getTimeDiffInSeconds(request.getTimestamp(), startTime.toEpochMilli());
        if (timeDiffSeconds < 0)
            throw new FutureRequestException("Future tick not supported");
        if (timeDiffSeconds > configuration.getWindowSizeInSecond())
            throw new NoProcessingRequiredException("Tick received before window time.");
    }

    private int getQueueBucketId(String instrument, String time) {
        var finalHash = Math.abs(instrument.hashCode() + time.hashCode());
        return finalHash % configuration.getCollectorThreadCount();
    }

    private TickDto convertRequestToDto(TickRequest request) {
        return TickDto
                .builder()
                .instrument(request.getInstrument())
                .price(request.getPrice())
                .timeKey(Utility.convertToKeyTime(request.getTimestamp()))
                .build();
    }
}
