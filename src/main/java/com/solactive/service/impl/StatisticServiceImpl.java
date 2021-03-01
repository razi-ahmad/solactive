package com.solactive.service.impl;

import com.solactive.dto.Instrument;
import com.solactive.dto.Statistics;
import com.solactive.service.StatisticService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StatisticServiceImpl implements StatisticService {
    private static final int QUEUE_INITIAL_CAPACITY = 60;
    private static final Statistics statistics = new Statistics();
    @Getter
    private static final Map<String, Statistics> instrumentStatistics = new HashMap<>();
    @Getter
    private static final Map<String, PriorityBlockingQueue<Instrument>> tickLastSixtySeconds = new ConcurrentHashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    @Override
    public void saveInstrument(Instrument instrument) {
        writeLock.lock();
        try {
            if (!tickLastSixtySeconds.containsKey(instrument.getInstrument())) {
                tickLastSixtySeconds.put(instrument.getInstrument(), new PriorityBlockingQueue<>(QUEUE_INITIAL_CAPACITY, Comparator.comparing(Instrument::getTimestamp)));
            }

            tickLastSixtySeconds.get(instrument.getInstrument()).offer(instrument);
        } finally {
            writeLock.unlock();
        }
        updateStatistics();
    }

    public void updateStatistics() {
        writeLock.lock();
        try {
            tickLastSixtySeconds.forEach((s, ticks) -> instrumentStatistics
                    .put(s, calculateInstrumentsStat(ticks.stream())));
            calculateStat(tickLastSixtySeconds.values().stream());
        } finally {
            writeLock.unlock();
        }
    }

    private Statistics calculateInstrumentsStat(Stream<Instrument> instrument) {
        Statistics statistics = new Statistics();
        DoubleSummaryStatistics ticksLastMinute = instrument
                .map(Instrument::getPrice)
                .collect(Collectors.summarizingDouble(Double::doubleValue));
        statistics.setCount(ticksLastMinute.getCount());
        statistics.setAvg(ticksLastMinute.getAverage());
        statistics.setMax(ticksLastMinute.getMax());
        statistics.setMin(ticksLastMinute.getMin());
        return statistics;
    }

    private void calculateStat(Stream<? extends Collection<Instrument>> instruments) {
        DoubleSummaryStatistics stats = instruments
                .flatMap(ticks -> ticks.stream()
                        .map(Instrument::getPrice))
                .collect(Collectors.summarizingDouble(Double::doubleValue));
        statistics.setCount(stats.getCount());
        statistics.setAvg(stats.getAverage());
        statistics.setMax(stats.getMax());
        statistics.setMin(stats.getMin());
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public Statistics getInstrumentStatistics(String instrumentIdentifier) {
        readLock.lock();
        try {
            return instrumentStatistics.get(instrumentIdentifier);
        } finally {
            readLock.unlock();
        }
    }
}
