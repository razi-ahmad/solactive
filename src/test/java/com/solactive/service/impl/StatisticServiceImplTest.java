package com.solactive.service.impl;

import com.solactive.dto.Instrument;
import com.solactive.dto.Statistics;
import com.solactive.service.StatisticService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatisticServiceImplTest {

    private StatisticService underTestService;

    @BeforeEach
    public void setup() {
        underTestService = new StatisticServiceImpl();
    }

    @AfterEach
    public void clear() {
        StatisticServiceImpl.getTickLastSixtySeconds().clear();
        StatisticServiceImpl.getInstrumentStatistics().clear();
    }

    @Test
    public void testSaveInstrument() {
        underTestService.saveInstrument(buildInstrument());
        Assertions.assertTrue(StatisticServiceImpl.getTickLastSixtySeconds().size() > 0);
    }

    @Test
    public void testGetStatistics() {
        Instrument instrument = buildInstrument();
        underTestService.saveInstrument(instrument);
        Statistics statistics = underTestService.getStatistics();

        Assertions.assertNotNull(statistics);
        Assertions.assertEquals(instrument.getPrice(), statistics.getAvg());
        Assertions.assertEquals(1, statistics.getCount());
        Assertions.assertEquals(instrument.getPrice(), statistics.getMin());
        Assertions.assertEquals(instrument.getPrice(), statistics.getMax());
    }

    @Test
    public void testGetInstrumentStatistics() {
        Instrument instrument = buildInstrument();
        underTestService.saveInstrument(instrument);
        Statistics statistics = underTestService.getInstrumentStatistics(instrument.getInstrument());

        Assertions.assertNotNull(statistics);
        Assertions.assertEquals(instrument.getPrice(), statistics.getAvg());
        Assertions.assertEquals(1, statistics.getCount());
        Assertions.assertEquals(instrument.getPrice(), statistics.getMin());
        Assertions.assertEquals(instrument.getPrice(), statistics.getMax());
    }

    private Instrument buildInstrument() {
        return Instrument
                .builder()
                .instrument("IBM.N")
                .price(143.82)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}