package com.solactive.scheduler;

import com.solactive.dto.Instrument;
import com.solactive.service.StatisticService;
import com.solactive.service.impl.StatisticServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Predicate;

@Component
public class InstrumentScheduler {

    private static final int POLLING_INTERVAL_RATE_MILLIS = 1000;

    private final int TIME_LIMIT;
    private final StatisticService statisticService;
    private final Predicate<Long> validateTime;

    @Autowired
    public InstrumentScheduler(@Value("${time.interval}") Integer timeLimit,
                               StatisticService statisticService) {
        this.TIME_LIMIT = timeLimit;
        validateTime = (timeStamp) -> System.currentTimeMillis() - timeStamp <= TIME_LIMIT;
        this.statisticService = statisticService;
    }

    @Scheduled(fixedRate = POLLING_INTERVAL_RATE_MILLIS)
    protected void removeOlderInstruments() {
        Map<String, PriorityBlockingQueue<Instrument>> tickLast60Seconds = StatisticServiceImpl.getTickLastSixtySeconds();
        tickLast60Seconds.forEach((s, ticks) -> {
            while (!ticks.isEmpty() && !this.validateTime.test(ticks.peek().getTimestamp())) {
                ticks.poll();
            }
        });
        statisticService.updateStatistics();
    }
}
