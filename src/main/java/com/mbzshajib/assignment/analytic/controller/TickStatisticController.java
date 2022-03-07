package com.mbzshajib.assignment.analytic.controller;

import com.mbzshajib.assignment.analytic.annotations.EnableResponseTimeWarning;
import com.mbzshajib.assignment.analytic.model.StatisticResponse;
import com.mbzshajib.assignment.analytic.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class TickStatisticController {
    private final StatisticsService service;

    @EnableResponseTimeWarning
    @GetMapping("/statistics")
    StatisticResponse getStatistics() {
        return service.getStatistics(Instant.now());

    }

    @EnableResponseTimeWarning
    @GetMapping("/statistics/{instrumentId}")
    StatisticResponse getStatisticsByInstrumentId(@PathVariable String instrumentId) {
        return service.getStatisticsByInstrumentId(instrumentId, Instant.now());

    }

}
