package com.mbzshajib.assignment.analytic.controller;

import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.model.TickDto;
import com.mbzshajib.assignment.analytic.model.TickRequest;
import com.mbzshajib.assignment.analytic.service.CollectorService;
import com.mbzshajib.assignment.analytic.utils.Constants;
import com.mbzshajib.assignment.analytic.utils.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class TickController {
    private final CollectorService collectorService;
    private final ApplicationConfiguration configuration;

    @PostMapping(Constants.Api.TICK)
    ResponseEntity<Void> collect(@Valid @RequestBody TickRequest request) {
        Instant startTime = Instant.now();
        Instant endTime;
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.CREATED);

        long timeDiffSeconds = Utility.getTimeDiffInSeconds(request.getTimestamp(), startTime.toEpochMilli());
        if (timeDiffSeconds < 0) {
            throw new IllegalArgumentException("Future tick not supported");
        }
        if (timeDiffSeconds > configuration.getWindowSizeInSecond()) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            collectorService.collect(convertRequestToDto(request));
        }
        endTime = Instant.now();
        if ((endTime.toEpochMilli() - startTime.toEpochMilli()) > configuration.getMaxApiResponseTimeinmilis()) {
            log.warn("Request processing time is greater than {} ms", configuration.getMaxApiResponseTimeinmilis());
        }
        return response;
    }


    private TickDto convertRequestToDto(TickRequest request) {
        return TickDto
                .builder()
                .instrument(request.getInstrument())
                .price(request.getPrice())
                .time(Utility.convertToKeyTime(request.getTimestamp()))
                .build();
    }
}
