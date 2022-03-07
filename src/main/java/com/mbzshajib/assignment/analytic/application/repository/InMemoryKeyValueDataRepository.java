package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InMemoryKeyValueDataRepository implements KeyValueDataRepository {
    @Getter
    private final Map<String, StatisticsDTO> storage;



    @Override
    public Optional<StatisticsDTO> get(String key) {
        if (storage.containsKey(key)) {
            return Optional.of(storage.get(key));
        } else return Optional.ofNullable(null);
    }

    @Override
    public List<Pair> getPairList(int size, Predicate<StatisticsDTO> predicate) {
        final int lookUpSize = Math.min(size, storage.size());
        Set<Map.Entry<String, StatisticsDTO>> entries = storage.entrySet();
        return entries.stream()
                .filter((entry) -> predicate.test(entry.getValue()))
                .limit(lookUpSize)
                .map(entry -> new Pair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

    }

    @Override
    public void saveOrUpdate(String key, StatisticsDTO value) {
        if (storage.containsKey(key)) {
            StatisticsDTO fromStore = storage.get(key);
            fromStore.setCount(fromStore.getCount() + 1);
            fromStore.setMax(Math.max(fromStore.getMax(), value.getMax()));
            fromStore.setMin(Math.min(fromStore.getMin(), value.getMin()));
            fromStore.setTotal(fromStore.getTotal() + value.getTotal());
            fromStore.setTimeKey(value.getTimeKey());
            storage.put(key, fromStore);
        } else {
            storage.put(key, value);
        }
    }

    @Override
    public void cleanUp(String key) {
        if (storage.containsKey(key)) {
            storage.remove(key);
        }
    }


    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public boolean hasData() {
        return !storage.isEmpty();
    }
}