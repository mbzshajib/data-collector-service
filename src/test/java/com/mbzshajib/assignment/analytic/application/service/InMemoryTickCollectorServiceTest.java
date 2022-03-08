package com.mbzshajib.assignment.analytic.application.service;

import com.mbzshajib.assignment.analytic.application.exception.FutureRequestException;
import com.mbzshajib.assignment.analytic.application.exception.NoProcessingRequiredException;
import com.mbzshajib.assignment.analytic.application.repository.QueueDataRepository;
import com.mbzshajib.assignment.analytic.configurations.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.models.TickRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class InMemoryTickCollectorServiceTest {
    private CollectorService service;
    @MockBean
    private QueueDataRepository storage;

    @MockBean
    private ApplicationConfiguration configuration;

    @BeforeEach
    void init() {
        configuration = Mockito.mock(ApplicationConfiguration.class);
        Mockito.when(configuration.getCollectorThreadCount()).thenReturn(1);
        Mockito.when(configuration.getWindowSizeInSecond()).thenReturn(60);
        service = new InMemoryTickCollectorService(storage, configuration);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 50, 59, 60})
    void testCollectSuccessfulInWindowTime(int number) {

        assertDoesNotThrow(() -> service.collect(TickRequest
                .builder()
                .instrument("ABCD")
                .price(100.1)
                .timestamp(Instant.now().minus(number, ChronoUnit.SECONDS).toEpochMilli())
                .build())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 10, 100, 1000})
        // six numbers
    void testCollectFutureTickShouldThrowFutureRequestException(int number) {
        assertThrows(FutureRequestException.class, () -> service.collect(TickRequest
                .builder()
                .instrument("ABCD")
                .price(100.1)
                .timestamp(Instant.now().plus(number, ChronoUnit.SECONDS).toEpochMilli())
                .build())
        );

    }

    @ParameterizedTest
    @ValueSource(ints = {61, 62, 100, 1000})
        // six numbers
    void testCollectOldTickShouldThrowNoProcessingRequiredException(int number) {
        assertThrows(NoProcessingRequiredException.class, () -> service.collect(TickRequest
                .builder()
                .instrument("ABCD")
                .price(100.1)
                .timestamp(Instant.now().minus(number, ChronoUnit.SECONDS).toEpochMilli())
                .build())
        );
    }

}