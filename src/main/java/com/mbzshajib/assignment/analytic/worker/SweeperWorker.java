package com.mbzshajib.assignment.analytic.worker;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import com.mbzshajib.assignment.analytic.utils.Utility;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author: Zaman Shajib
 * @email: md.shajib@bKash.com
 * Created on 3/6/22 at 12:09 PM.
 */
@Slf4j
public class SweeperWorker extends AbstractSweeperWorker {

    public SweeperWorker(Integer id, Map<String, StatisticsDTO> map, Integer threadWaitTimeInMilis, Integer sweeperProcessMaxItemsInBatch, Integer windowSizeInSecond) {
        super(id, map, threadWaitTimeInMilis, sweeperProcessMaxItemsInBatch, windowSizeInSecond);
    }

    @Override
    public void run() {
        while (true) {
            log.trace("Sweeping process started {}", Thread.currentThread().getName());
            if (!map.isEmpty()) {
                try {
                    var currentTime = Instant.now().minus(windowSizeInSecond + 1, ChronoUnit.SECONDS);
                    var currentTimeKey = Integer.parseInt(Utility.convertToKeyTime(currentTime));
                    var workingItemSize = Math.min(batchSize, map.size());
                    var markedKeys = new LinkedList<>();
                    var iterator = map.entrySet().iterator();
                    IntStream.range(0, workingItemSize)
                            .forEach(index -> {
                                Map.Entry<String, StatisticsDTO> entry = iterator.next();
                                if (entry.getValue().getTimeKey() < currentTimeKey) markedKeys.add(entry.getKey());
                            });
                    markedKeys.stream()
                            .forEach(key -> {
                                if (map.containsKey(key)) map.remove(key);
                            });
                } catch (Exception ex) {
                    log.error("Unexpected error occurred ", ex);
                }
            } else {
                try {
                    Thread.sleep(threadWaitTimeInMilis);
                } catch (InterruptedException e) {
                    log.error("Thread {} is encountering error", Thread.currentThread().getName(), e);
                }
            }
        }

    }
}
