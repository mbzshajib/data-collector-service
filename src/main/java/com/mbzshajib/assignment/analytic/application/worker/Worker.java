package com.mbzshajib.assignment.analytic.application.worker;

import com.mbzshajib.assignment.analytic.application.worker.jobs.Job;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Worker implements Runnable {
    protected final Integer id;
    protected final Integer waitTime;
    protected final Job job;

    public Worker(Integer id, Integer waitTime, Job job) {
        this.id = id;
        this.waitTime = waitTime;
        this.job = job;
    }


    @Override
    public void run() {
        while (true) {
            try {
                job.doNow();
                Thread.sleep(waitTime);
            } catch (Exception e) {
                log.error("Thread id is encountering error", e);
            }
        }

    }
}
