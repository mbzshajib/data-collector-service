package com.mbzshajib.assignment.analytic.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: Zaman Shajib
 * @email: md.shajib@bKash.com
 * Created on 3/6/22 at 10:57 AM.
 */
@Slf4j
public class UtilityTest {
    @Nested
    @DisplayName("Test ")
    class TestGetTimeDiffInSeconds {
        @Test
        void testGetTimeDiffInSeconds() {
            Instant now = Instant.now();
            assertThrows(IllegalArgumentException.class, () -> Utility.getTimeDiffInSeconds(null, now.toEpochMilli()), "Should throw Illegal Argument Exception");
            assertThrows(IllegalArgumentException.class, () -> Utility.getTimeDiffInSeconds(now.toEpochMilli(), null), "Should throw Illegal Argument Exception");
            assertEquals(0, Utility.getTimeDiffInSeconds(now.toEpochMilli(), now.toEpochMilli()));
            assertEquals(-100, Utility.getTimeDiffInSeconds(now.toEpochMilli(), now.minus(100, ChronoUnit.SECONDS).toEpochMilli()));
            assertEquals(100, Utility.getTimeDiffInSeconds(now.toEpochMilli(), now.plus(100, ChronoUnit.SECONDS).toEpochMilli()));
            assertEquals(100, Utility.getTimeDiffInSeconds(now.minus(100, ChronoUnit.SECONDS).toEpochMilli(), now.toEpochMilli()));
            assertEquals(-100, Utility.getTimeDiffInSeconds(now.plus(100, ChronoUnit.SECONDS).toEpochMilli(), now.toEpochMilli()));
        }

        @Test
        void testConvertToKeyTime() {
            Instant instant = LocalDateTime.of(2022, 2, 1, 12, 12, 12)
                    .atZone(ZoneOffset.systemDefault()).toInstant();
            assertEquals("121212",Utility.convertToKeyTime(instant));
            assertEquals("121212",Utility.convertToKeyTime(instant.toEpochMilli()));
            instant = LocalDateTime.of(2022, 2, 1, 22, 12, 59)
                    .atZone(ZoneOffset.systemDefault()).toInstant();
            assertEquals("221259",Utility.convertToKeyTime(instant));
            assertEquals("221259",Utility.convertToKeyTime(instant.toEpochMilli()));

            Instant nullInstant = null;
            assertThrows(IllegalArgumentException.class, () -> Utility.convertToKeyTime(nullInstant), "instant must not be null");
            Long nullMilis = null;
            assertThrows(IllegalArgumentException.class, () -> Utility.convertToKeyTime(nullMilis), "instant must not be null");
        }

        @Test
        void testGetAllSecondsInTimeWindow() {
            int windowSize = 5;
            Instant endTime = Instant.now();
            List<String> expected = new ArrayList<>();
            IntStream.rangeClosed(1, 5)
                    .forEach(i -> {
                        expected.add(Utility.convertToKeyTime(endTime.minus(i, ChronoUnit.SECONDS)));
                    });
            List<String> result = Utility.getAllSecondsInTimeWindow(endTime, windowSize);
            assertEquals(windowSize, result.size());
            assertEquals(windowSize, result.size());
            assertEquals(expected.size(), result.size());
            assertTrue(expected.containsAll(result));

            assertThrows(IllegalArgumentException.class, () -> Utility.getAllSecondsInTimeWindow(endTime, 0), "window time can not be null");
        }

        @Test
        void testFormatStatisticStorageKey() {
            assertEquals("testDateId.hello.100", Utility.formatStatisticStorageKey(100, "testDateId", "hello"));
            assertEquals("testDateId. .100", Utility.formatStatisticStorageKey(100, "testDateId", " "));
            assertThrows(IllegalArgumentException.class, () -> Utility.formatStatisticStorageKey(-1, "testDateId", " "), "Negative is not allowed in process ID");
            assertThrows(IllegalArgumentException.class, () -> Utility.formatStatisticStorageKey(100, null, ""), "Data Id can not be null");
            assertThrows(IllegalArgumentException.class, () -> Utility.formatStatisticStorageKey(100, "dataId", null), "Time Id can not be null");
        }
    }

}