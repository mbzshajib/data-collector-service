package com.mbzshajib.assignment.analytic.endpoints.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbzshajib.assignment.analytic.application.exception.FutureRequestException;
import com.mbzshajib.assignment.analytic.application.exception.NoProcessingRequiredException;
import com.mbzshajib.assignment.analytic.application.service.CollectorService;
import com.mbzshajib.assignment.analytic.application.utils.Constants;
import com.mbzshajib.assignment.analytic.configurations.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.models.TickRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                        .content(asJsonString(createRequest(PRICE_LIST[0], Instant.now())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnResponseCodeNoContent() throws Exception {

        Mockito.doThrow(NoProcessingRequiredException.class).when(collectorService).collect(any(TickRequest.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constants.Api.TICK)
                        .content(asJsonString(createRequest(PRICE_LIST[0], Instant.now().minus(60, ChronoUnit.SECONDS))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnBadRequest() throws Exception {
        Mockito.doThrow(FutureRequestException.class).when(collectorService).collect(any(TickRequest.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constants.Api.TICK)
                        .content(asJsonString(createRequest(PRICE_LIST[0], Instant.now().plus(60, ChronoUnit.SECONDS))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private TickRequest createRequest(Double price, Instant instant) {
        return TickRequest
                .builder()
                .instrument(TickControllerTest.INSTRUMENT_ID)
                .price(price)
                .timestamp(instant.toEpochMilli())
                .build();
    }
}