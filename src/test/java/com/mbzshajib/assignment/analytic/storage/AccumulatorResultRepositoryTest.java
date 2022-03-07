//package com.mbzshajib.assignment.analytic.storage;
//
//import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
//import com.mbzshajib.assignment.analytic.utils.Utility;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Predicate;
//import java.util.stream.IntStream;
//
//import static org.junit.jupiter.api.Assertions.*;
//

//class AccumulatorResultRepositoryTest {
//    private Map<String, StatisticsDTO> map;
//    private AccumulatorResultRepository repository;
//    private Instant now;
//
//    @BeforeEach
//    void init() {
//        map = new ConcurrentHashMap<>();
//        int timeWindow = 10;
//        now = Instant.now();
//        Predicate<StatisticsDTO> function = (data) ->
//                data.getTimeKey() < Integer.parseInt(Utility.convertToKeyTime(now.minus(timeWindow, ChronoUnit.SECONDS)));
//        repository = new AccumulatorResultRepository(map, function);
//    }
//
//    @Test
//    void testGet() {
//        int size = repository.size();
//        assertEquals(0, size);
//        assertTrue(repository.get("ABCD").isEmpty());
//        IntStream.range(0, 10)
//                .forEach((id) ->
//                        map.put(Utility.formatStatisticStorageKey(0, "abcd" + id, Utility.convertToKeyTime(now)),
//                                StatisticsDTO.builder().build())
//                );
//        IntStream.range(1, 10)
//                .forEach((id) -> assertNotNull(repository.get(Utility.formatStatisticStorageKey(0, "abcd" + id, Utility.convertToKeyTime(now)))));
//    }
//
//}