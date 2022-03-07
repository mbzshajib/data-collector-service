package com.mbzshajib.assignment.analytic.application.worker;

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
            log.trace("Worker ID {} Worker process processing {}", id, Thread.currentThread().getName());
            try {
                job.doNow();
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("Thread {} is encountering error", Thread.currentThread().getName(), e);
            }

        }

    }
}
