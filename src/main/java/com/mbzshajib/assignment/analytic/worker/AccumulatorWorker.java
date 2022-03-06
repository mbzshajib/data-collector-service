package com.mbzshajib.assignment.analytic.worker;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import com.mbzshajib.assignment.analytic.model.TickDto;
import com.mbzshajib.assignment.analytic.utils.Utility;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Queue;

@Slf4j
public class AccumulatorWorker extends AbstractAccumulatorWorker {


    private static final String GLOBAL_KEY_IDENTIFIER = "";

    public AccumulatorWorker(Integer id, Queue<TickDto> storage, Map<String, StatisticsDTO> outputMap, Integer threadWaitTimeInMilis) {
        super(id, storage, outputMap, threadWaitTimeInMilis);
    }


    @Override
    public void run() {
        while (true) {
            log.trace("Thread {} is checking for new items to process.", Thread.currentThread().getName());

            if (!inputQueue.isEmpty()) {
                try {
                    log.trace("Found task to process.");
                    var tick = inputQueue.poll();
                    var instrumentKey = Utility.formatStatisticStorageKey(id, tick.getInstrument(), tick.getTime());
                    var globalKey = Utility.formatStatisticStorageKey(id, GLOBAL_KEY_IDENTIFIER, tick.getTime());
                    saveOrUpdate(tick, globalKey);
                    saveOrUpdate(tick, instrumentKey);

                } catch (Exception ex) {
                    log.error("Unexpected error occurred ", ex);
                }
            } else {
                log.trace("Found no task to process");
                try {
                    Thread.sleep(threadWaitTimeInMilis);
                } catch (InterruptedException e) {
                    log.error("Thread {} is encountering error", Thread.currentThread().getName(), e);
                }
            }
        }
    }

    private void saveOrUpdate(TickDto tick, String key) {
        StatisticsDTO statisticsDTOForInstrument;
        if (!outputMap.containsKey(key)) {
            statisticsDTOForInstrument = createNewFromTick(tick);
        } else {
            statisticsDTOForInstrument = outputMap.get(key);
            statisticsDTOForInstrument.setCount(statisticsDTOForInstrument.getCount() + 1);
            statisticsDTOForInstrument.setTotal(statisticsDTOForInstrument.getTotal() + tick.getPrice());
            statisticsDTOForInstrument.setMax(Math.max(statisticsDTOForInstrument.getTotal(), tick.getPrice()));
            statisticsDTOForInstrument.setMin(Math.min(statisticsDTOForInstrument.getMin(), tick.getPrice()));
        }
        outputMap.put(key, statisticsDTOForInstrument);
    }

    private StatisticsDTO createNewFromTick(TickDto tick) {
        StatisticsDTO dto = StatisticsDTO
                .builder()
                .timeKey(Integer.parseInt(tick.getTime()))
                .count(1)
                .total(tick.getPrice())
                .max(tick.getPrice())
                .min(tick.getPrice())
                .build();
        return dto;
    }
}
