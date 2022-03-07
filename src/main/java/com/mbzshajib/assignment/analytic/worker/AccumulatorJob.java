package com.mbzshajib.assignment.analytic.worker;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import com.mbzshajib.assignment.analytic.model.TickDto;
import com.mbzshajib.assignment.analytic.storage.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.utils.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
public class AccumulatorJob implements Job {
    private static final String GLOBAL_KEY_IDENTIFIER = "";

    private final Integer id;
    private final Queue<TickDto> input;
    private final KeyValueDataRepository outputRepository;

    @Override
    public void doNow() {
        log.trace("Accumulation job started input {} output {} ", input.hashCode(),outputRepository.hashCode());
        if (!input.isEmpty()) {
            var tick = input.poll();
            var instrumentKey = Utility.formatStatisticStorageKey(id, tick.getInstrument(), tick.getTimeKey());
            var globalKey = Utility.formatStatisticStorageKey(id, GLOBAL_KEY_IDENTIFIER, tick.getTimeKey());
            outputRepository.saveOrUpdate(instrumentKey, createNewFromTick(tick));
            outputRepository.saveOrUpdate(globalKey, createNewFromTick(tick));
        }
    }

    private StatisticsDTO createNewFromTick(TickDto tick) {
        StatisticsDTO dto = StatisticsDTO
                .builder()
                .timeKey(Integer.parseInt(tick.getTimeKey()))
                .count(1)
                .total(tick.getPrice())
                .max(tick.getPrice())
                .min(tick.getPrice())
                .build();
        return dto;
    }
}
