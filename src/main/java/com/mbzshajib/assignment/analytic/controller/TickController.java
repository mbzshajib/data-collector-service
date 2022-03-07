package com.mbzshajib.assignment.analytic.controller;

import com.mbzshajib.assignment.analytic.annotations.EnableResponseTimeWarning;
import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.model.TickRequest;
import com.mbzshajib.assignment.analytic.service.CollectorService;
import com.mbzshajib.assignment.analytic.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class TickController {
    private final CollectorService collectorService;
    private final ApplicationConfiguration configuration;

    @EnableResponseTimeWarning
    @PostMapping(Constants.Api.TICK)
    @ResponseStatus(HttpStatus.CREATED)
    public void collect(@Valid @RequestBody TickRequest request) {
        collectorService.collect(request);
    }


}
