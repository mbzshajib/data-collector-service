package com.mbzshajib.assignment.analytic.worker;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;

import java.util.Map;

/**
 * @author: Zaman Shajib
 * @email: md.shajib@bKash.com
 * Created on 3/6/22 at 12:10 PM.
 */
public abstract class AbstractSweeperWorker implements Runnable {
    protected final Integer id;
    protected final Map<String, StatisticsDTO> map;
    protected final Integer threadWaitTimeInMilis;
    protected final Integer batchSize;
    protected final Integer windowSizeInSecond;

    public AbstractSweeperWorker(Integer id, Map<String, StatisticsDTO> map, Integer threadWaitTimeInMilis, Integer batchSize, Integer windowSizeInSecond) {
        this.id = id;
        this.map = map;
        this.threadWaitTimeInMilis = threadWaitTimeInMilis;
        this.batchSize = batchSize;
        this.windowSizeInSecond = windowSizeInSecond;
    }
}
