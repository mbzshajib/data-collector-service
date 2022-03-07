package com.mbzshajib.assignment.analytic.application.worker;

import com.mbzshajib.assignment.analytic.application.repository.KeyValueDataRepository;
import com.mbzshajib.assignment.analytic.application.utils.Utility;
import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import com.mbzshajib.assignment.analytic.models.TickDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;

@Slf4j
@Getter
@RequiredArgsConstructor
public class AccumulatorJob implements Job {
    private static final String GLOBAL_KEY_IDENTIFIER = "";
    private final Integer id;
    private final Queue<TickDto> inputRepository;
    private final KeyValueDataRepository outputRepository;

    @Override
    public void doNow() {
        log.trace("Accumulation job started input {} output {} ", inputRepository.hashCode(), outputRepository.hashCode());
        if (!inputRepository.isEmpty()) {
            var tick = inputRepository.poll();
            var instrumentKey = Utility.formatStatisticStorageKey(id, tick.getInstrument(), tick.getTimeKey());
            var globalKey = Utility.formatStatisticStorageKey(id, GLOBAL_KEY_IDENTIFIER, tick.getTimeKey());
            outputRepository.saveOrUpdate(instrumentKey, createNewFromTick(tick));
            outputRepository.saveOrUpdate(globalKey, createNewFromTick(tick));
        }
    }

    private StatisticsDTO createNewFromTick(TickDto tick) {
        return StatisticsDTO
                .builder()
                .timeKey(Integer.parseInt(tick.getTimeKey()))
                .count(1)
                .total(tick.getPrice())
                .max(tick.getPrice())
                .min(tick.getPrice())
                .build();
    }
}
