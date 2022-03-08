package com.mbzshajib.assignment.analytic.application.worker;

import com.mbzshajib.assignment.analytic.application.worker.jobs.Job;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Worker implements Runnable {
    protected final Integer id;
    protected final Job job;

    public Worker(Integer id, Integer waitTime, Job job) {
        this.id = id;
        this.job = job;
    }


    @Override
    public void run() {
        while (true) {
            try {
                job.doNow();
            } catch (Exception e) {
                log.error("Thread is encountering error", e);
            }
        }

    }
}
