package com.mbzshajib.assignment.analytic.application.repository;

import com.mbzshajib.assignment.analytic.models.StatisticsDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InMemoryKeyValueDataRepository implements KeyValueDataRepository {
    @NonNull
    private final Map<String, StatisticsDTO> repository;


    @Override
    public Optional<StatisticsDTO> get(String key) {
        if (repository.containsKey(key)) {
            return Optional.of(repository.get(key));
        } else return Optional.empty();
    }

    @Override
    public List<Pair> getPairList(int size, Predicate<StatisticsDTO> predicate) {
        final int lookUpSize = Math.min(size, repository.size());
        Set<Map.Entry<String, StatisticsDTO>> entries = repository.entrySet();
        return entries.stream()
                .filter((entry) -> predicate.test(entry.getValue()))
                .limit(lookUpSize)
                .map(entry -> new Pair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

    }

    @Override
    public void saveOrUpdate(String key, StatisticsDTO value) {
        if (key == null || value == null)
            throw new IllegalArgumentException("key/value can not be null");
        if (repository.containsKey(key)) {
            StatisticsDTO fromStore = repository.get(key);
            fromStore.setCount(fromStore.getCount() + 1);
            fromStore.setMax(Math.max(fromStore.getMax(), value.getMax()));
            fromStore.setMin(Math.min(fromStore.getMin(), value.getMin()));
            fromStore.setTotal(fromStore.getTotal() + value.getTotal());
            fromStore.setTimeKey(value.getTimeKey());
            repository.put(key, fromStore);
        } else {
            repository.put(key, value);
        }
    }

    @Override
    public void cleanUp(String key) {
        if (key == null) throw new IllegalArgumentException("key can not be null");
        if (repository.containsKey(key))
            repository.remove(key);
    }


    @Override
    public int size() {
        return repository.size();
    }

    @Override
    public boolean hasData() {
        return !repository.isEmpty();
    }
}