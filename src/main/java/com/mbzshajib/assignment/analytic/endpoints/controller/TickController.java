package com.mbzshajib.assignment.analytic.endpoints.controller;

import com.mbzshajib.assignment.analytic.application.annotations.EnableResponseTimeWarning;
import com.mbzshajib.assignment.analytic.application.service.CollectorService;
import com.mbzshajib.assignment.analytic.application.utils.Constants;
import com.mbzshajib.assignment.analytic.models.TickRequest;
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

    @EnableResponseTimeWarning
    @PostMapping(Constants.Api.TICK)
    @ResponseStatus(HttpStatus.CREATED)
    public void collect(@Valid @RequestBody TickRequest request) {
        collectorService.collect(request);
    }


}
