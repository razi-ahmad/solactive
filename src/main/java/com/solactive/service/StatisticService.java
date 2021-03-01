package com.solactive.service;

import com.solactive.dto.Instrument;
import com.solactive.dto.Statistics;

public interface StatisticService {

     void saveInstrument(Instrument instrument);

    void updateStatistics();

    Statistics getStatistics();

    Statistics getInstrumentStatistics(String instrumentIdentifier);
}
