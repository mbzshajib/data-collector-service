package com.mbzshajib.assignment.analytic.storage;

import com.mbzshajib.assignment.analytic.config.ApplicationConfiguration;
import com.mbzshajib.assignment.analytic.model.StatisticsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
//TODO: configure 10 maps
public class InMemoryAccumulatedStorage implements MapStorage {
    private static final int MIN_INDEX = 0;
    private final ApplicationConfiguration configuration;
    private Map<String, StatisticsDTO> storage;

    @PostConstruct
    private void init() {
        storage = new ConcurrentHashMap<>();
    }


    @Override
    public Map<String, StatisticsDTO> getMap() {
        return storage;
    }
}
