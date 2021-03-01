package com.solactive.controller;

import com.solactive.dto.Instrument;
import com.solactive.dto.Statistics;
import com.solactive.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Predicate;

@RestController
public class StatisticsController {

    private final StatisticService statisticService;
    public final int TIME_LIMIT;
    private final Predicate<Long> validateTime;

    @Autowired
    public StatisticsController(@Value("${time.interval}") Integer timeLimit, StatisticService statisticService) {
        this.TIME_LIMIT = timeLimit;
        validateTime = (timeStamp) -> {
            return System.currentTimeMillis() - timeStamp <= TIME_LIMIT;
        };
        this.statisticService = statisticService;
    }

    @PostMapping("/ticks")
    public ResponseEntity<Void> saveTick(@Valid @RequestBody Instrument instrument) {
        if (!this.validateTime.test(instrument.getTimestamp())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            this.statisticService.saveInstrument(instrument);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Statistics> getStatisticsOfInstruments() {
        return ResponseEntity.ok(statisticService.getStatistics());
    }

    @GetMapping("/statistics/{instrument_identifier}")
    public ResponseEntity<Statistics> getStatisticsOfInstruments(@PathVariable("instrument_identifier") String instrumentIdentifier) {
        return ResponseEntity.ok(statisticService.getInstrumentStatistics(instrumentIdentifier));
    }

}
