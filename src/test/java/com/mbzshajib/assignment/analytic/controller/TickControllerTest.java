package com.mbzshajib.assignment.analytic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.model.TickRequest;
import com.mbzshajib.assignment.analytic.service.CollectorService;
import com.mbzshajib.assignment.analytic.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author: Zaman Shajib
 * @email: md.shajib@bKash.com
 * Created on 3/6/22 at 4:02 PM.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = TickController.class)
class TickControllerTest {
    private static final String INSTRUMENT_ID = "ABCD";
    private static final Double[] PRICE_LIST = new Double[]{100.0, 101.01, 104.99};
    @MockBean
    ApplicationConfiguration configuration;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CollectorService collectorService;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(Constants.Api.TICK)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void shouldReturnResponseCodeCreated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constants.Api.TICK)
                        .content(asJsonString(createRequest(INSTRUMENT_ID, PRICE_LIST[0], Instant.now())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnResponseCodeNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constants.Api.TICK)
                        .content(asJsonString(createRequest(INSTRUMENT_ID, PRICE_LIST[0], Instant.now().minus(60, ChronoUnit.SECONDS))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constants.Api.TICK)
                        .content(asJsonString(createRequest(INSTRUMENT_ID, PRICE_LIST[0], Instant.now().plus(60, ChronoUnit.SECONDS))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private TickRequest createRequest(String instrumentName, Double price, Instant instant) {
        return TickRequest
                .builder()
                .instrument(instrumentName)
                .price(price)
                .timestamp(instant.toEpochMilli())
                .build();
    }

}
