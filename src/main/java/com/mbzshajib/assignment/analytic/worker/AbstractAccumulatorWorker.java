package com.mbzshajib.assignment.analytic.worker;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import com.mbzshajib.assignment.analytic.model.TickDto;

import java.util.Map;
import java.util.Queue;

public abstract class AbstractAccumulatorWorker implements Runnable {
    protected final Integer id;
    protected final Queue<TickDto> inputQueue;
    protected final Map<String, StatisticsDTO> outputMap;
    protected final Integer threadWaitTimeInMilis;

    public AbstractAccumulatorWorker(Integer id, Queue<TickDto> inputQueue, Map<String, StatisticsDTO> outputMap, Integer threadWaitTimeInMilis) {
        this.id = id;
        this.inputQueue = inputQueue;
        this.outputMap = outputMap;
        this.threadWaitTimeInMilis = threadWaitTimeInMilis;
    }

}
