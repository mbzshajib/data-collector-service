package com.mbzshajib.assignment.analytic.endpoints.controller;

import com.mbzshajib.assignment.analytic.application.service.StatisticsService;
import com.mbzshajib.assignment.analytic.application.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = TickStatisticController.class)
class TickStatisticControllerTest {
    private static final String INSTRUMENT_ID = "ABCD";
    private static final Double[] PRICE_LIST = new Double[]{100.0, 101.01, 104.99};
    @MockBean
    StatisticsService service;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void statisticsControllerShouldReturnOk() throws Exception {
        Instant now = Instant.now();
        mockMvc.perform(MockMvcRequestBuilders
                        .get(Constants.Api.STATISTICS))
                .andExpect(status().isOk());
    }

    @Test
    public void statisticsControllerByIdShouldReturnOk() throws Exception {
        Instant now = Instant.now();
        mockMvc.perform(MockMvcRequestBuilders
                        .get(Constants.Api.STATISTICS_BY_INSTRUMENT + INSTRUMENT_ID))
                .andExpect(status().isOk());
    }

}