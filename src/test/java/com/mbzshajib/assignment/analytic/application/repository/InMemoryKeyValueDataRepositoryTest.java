package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.application.utils.Utility;
import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryKeyValueDataRepositoryTest {
    KeyValueDataRepository repository;
    Map<String, StatisticsDTO> map;

    @BeforeEach
    void init() {
        map = new ConcurrentHashMap<>();
        repository = new InMemoryKeyValueDataRepository(map);
    }

    @Test
    void testGet() {
        assertFalse(repository.get("").isPresent());
        repository.saveOrUpdate("ABCD", StatisticsDTO.builder().build());
        assertFalse(repository.get("").isPresent());
        assertEquals(1, repository.size());
        assertFalse(repository.get("abcd").isPresent());
        assertTrue(repository.get("ABCD").isPresent());
    }

    @Test
    void getPairListTestBatchSizeBelowAndAbove() {
        assertEquals(0, repository.size());
        IntStream.range(0, 10).forEach(id -> repository.saveOrUpdate("ABCD." + id, StatisticsDTO.builder().count(1).build()));
        assertEquals(5, repository.getPairList(5, (data) -> data.getCount() == 1).size());
        assertEquals(10, repository.getPairList(15, (data) -> data.getCount() == 1).size());
    }

    @Test
    void getPairListTestBatchContainsCorrectKey() {
        assertEquals(0, repository.size());
        List<String> keys = IntStream.range(0, 10)
                .mapToObj(id -> "ABCD." + id).collect(Collectors.toList());
        IntStream.range(0, 10).forEach(id -> repository.saveOrUpdate(keys.get(id), StatisticsDTO.builder().count(1).build()));
        List<Pair> returned = repository.getPairList(5, (data) -> data.getCount() == 1);
        assertEquals(5, returned.size());
        returned.forEach(data -> keys.contains(data.getKey()));
        assertEquals(10, repository.getPairList(15, (data) -> data.getCount() == 1).size());
        returned = repository.getPairList(15, (data) -> data.getCount() == 1);
        assertEquals(10, returned.size());
        returned.forEach(data -> keys.contains(data));
    }

    @Test
    void getPairListTestPredicateOnMinField() {
        assertEquals(0, repository.size());
        List<StatisticsDTO> dtoList = IntStream.range(0, 10)
                .mapToObj(id ->
                        StatisticsDTO.builder()
                                .min(100 + id)
                                .build()
                ).collect(Collectors.toList());
        IntStream.range(0, 10).forEach(id -> repository.saveOrUpdate("ABCD." + id, dtoList.get(id)));
        assertEquals(0, repository.getPairList(10, data -> data.getMin() < 100).size());
        assertEquals(1, repository.getPairList(10, data -> data.getMin() <= 100).size());
        assertEquals(1, repository.getPairList(10, data -> data.getMin() == 100).size());
        assertEquals(10, repository.getPairList(15, data -> data.getMin() <= 110).size());
        List<Pair> pairList = repository.getPairList(100, data -> true);
    }

    @Test
    void getPairListTestPredicateOnTimeKey() {
        assertEquals(0, repository.size());
        Instant now = Instant.now();
        List<StatisticsDTO> dtoList = IntStream.range(0, 10)
                .mapToObj(id ->
                        StatisticsDTO.builder()
                                .min(100 + id)
                                .timeKey(Integer.parseInt(Utility.convertToKeyTime(now.plus(id, ChronoUnit.SECONDS))))
                                .build()
                ).collect(Collectors.toList());
        IntStream.range(0, 10).forEach(id -> repository.saveOrUpdate("ABCD." + id, dtoList.get(id)));
        assertEquals(0, repository.getPairList(10, data ->
                data.getTimeKey() < Integer.parseInt(Utility.convertToKeyTime(now))).size());
        assertEquals(5, repository.getPairList(10, data ->
                data.getTimeKey() < Integer.parseInt(Utility.convertToKeyTime(now.plus(5, ChronoUnit.SECONDS)))).size());
        assertEquals(1, repository.getPairList(10, data -> data.getTimeKey() ==
                Integer.parseInt(Utility.convertToKeyTime(now.plus(9, ChronoUnit.SECONDS)))).size());
        assertEquals(10, repository.getPairList(15, data ->
                data.getTimeKey() <= Integer.parseInt(Utility.convertToKeyTime(now.plus(100, ChronoUnit.SECONDS)))).size());
    }

    @Test
    void testSaveOrUpdate() {
        assertEquals(0, repository.size());
        assertThrows(IllegalArgumentException.class, () -> repository.saveOrUpdate(null, null));
        assertEquals(0, repository.size());
        assertThrows(IllegalArgumentException.class, () -> repository.saveOrUpdate(null, StatisticsDTO.builder().build()));
        assertEquals(0, repository.size());
        assertThrows(IllegalArgumentException.class, () -> repository.saveOrUpdate("ABCD", null));
        assertEquals(0, repository.size());
        assertThrows(IllegalArgumentException.class, () -> repository.saveOrUpdate("", null));
        assertEquals(0, repository.size());
        repository.saveOrUpdate("", StatisticsDTO.builder().build());
        assertEquals(1, repository.size());
        assertTrue(repository.get("").isPresent());
        assertNotNull(repository.get("").get());
    }

    @Test
    void testCleanUp() {
        assertEquals(0, repository.size());
        repository.cleanUp("SOMEKEY");
        assertEquals(0, repository.size());
        repository.saveOrUpdate("ABCD", StatisticsDTO.builder().build());
        repository.saveOrUpdate("EFGH", StatisticsDTO.builder().build());
        IntStream.range(0, 10).forEach(id -> repository.saveOrUpdate("ABCD." + id, StatisticsDTO.builder().build()));
        assertEquals(12, repository.size());
        repository.cleanUp("ABCD");
        assertEquals(11, repository.size());
        repository.cleanUp("ABCD");
        assertEquals(11, repository.size());
        IntStream.range(0, 10).forEach(id -> repository.cleanUp("ABCD." + id));
        assertEquals(1, repository.size());
        repository.cleanUp("EFGH");
        assertEquals(0, repository.size());
        repository.cleanUp("EFGH");
        assertEquals(0, repository.size());
        repository.cleanUp("XYZ");
        assertEquals(0, repository.size());
        assertThrows(IllegalArgumentException.class, () -> repository.cleanUp(null));
    }

    @Test
    void testSize() {
        assertEquals(0, repository.size());
        repository.saveOrUpdate("ABCD", StatisticsDTO.builder().build());
        assertEquals(1, repository.size());
        repository.saveOrUpdate("ABCD", StatisticsDTO.builder().build());
        assertEquals(1, repository.size());
        repository.saveOrUpdate("EFGH", StatisticsDTO.builder().build());
        assertEquals(2, repository.size());
        IntStream.range(0, 10).forEach(id -> repository.saveOrUpdate("ABCD." + id, StatisticsDTO.builder().build()));
        assertEquals(12, repository.size());
    }

    @Test
    void testHasData() {
        assertFalse(repository.hasData());
        repository.saveOrUpdate("ABCD", StatisticsDTO.builder().build());
        repository.saveOrUpdate("ABCD", StatisticsDTO.builder().build());
        assertTrue(repository.hasData());
        repository.cleanUp("ABCD");
        assertFalse(repository.hasData());

    }

}