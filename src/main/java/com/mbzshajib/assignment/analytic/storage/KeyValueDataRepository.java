package com.mbzshajib.assignment.analytic.storage;

import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
public interface KeyValueDataRepository {

    Optional<StatisticsDTO> get(String key);

    List<Pair> getPairList(int size, Predicate<StatisticsDTO> predicate);

    void saveOrUpdate(String key, StatisticsDTO value);

    void cleanUp(String key);

    int size();

    boolean hasData();
}
