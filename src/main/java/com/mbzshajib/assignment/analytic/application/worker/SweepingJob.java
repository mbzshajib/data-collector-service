package com.mbzshajib.assignment.analytic.application.worker;

import com.mbzshajib.assignment.analytic.application.utils.Utility;
import com.mbzshajib.assignment.analytic.application.repository.KeyValueDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
public class SweepingJob implements Job {
    private final Integer windowSizeInSecond;
    private final KeyValueDataRepository repository;
    protected final Integer processingBatchSize;

    @Override
    public void doNow() {
        log.trace("Sweeping job started {} ", repository.hasData());
        if (repository.hasData()) {
            var currentTime = Instant.now().minus(windowSizeInSecond + 1, ChronoUnit.SECONDS);
            var currentTimeKey = Integer.parseInt(Utility.convertToKeyTime(currentTime));
            var markedKeys = repository.getPairList(processingBatchSize, (entry) -> entry.getTimeKey() < currentTimeKey);
            markedKeys.stream()
                    .forEach((pair) -> repository.cleanUp(pair.getKey()));
        }
    }
}
