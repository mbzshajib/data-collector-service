package com.mbzshajib.assignment.analytic.application.service;

import com.mbzshajib.assignment.analytic.models.TickRequest;

public interface CollectorService {

    /**
     * @param request for handling incoming request
     */
    void collect(TickRequest request);
}
