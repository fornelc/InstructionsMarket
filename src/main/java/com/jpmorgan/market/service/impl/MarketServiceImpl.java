package com.jpmorgan.market.service.impl;

import com.jpmorgan.market.model.Currencies;
import com.jpmorgan.market.model.Market;
import com.jpmorgan.market.repository.MarketRepository;
import com.jpmorgan.market.service.MarketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class MarketServiceImpl implements MarketService {
    private static final Logger LOG = LoggerFactory.getLogger(MarketServiceImpl.class);

    @Autowired
    MarketRepository marketRepository;

    /**
     * Set list of markets object by getting the data.json values from the repository
     * @return
     */
    public List<Market> getMarkets() {
        try {
            List<Market> markets = marketRepository.getMarketsData();
            markets.forEach(m -> m.setSettlementDate(checkMarketSettlementDate(m.getCurrency(), m.getSettlementDate())));

            return markets;
        } catch (Exception e) {
            LOG.error("Could not get the markets.", e);
            return null;
        }
    }

    /**
     * Set market SettlementDate depending on the region
     * @param currency
     * @param settlementDate
     * @return
     */
    private LocalDate checkMarketSettlementDate(String currency, LocalDate settlementDate) {
        if(isMyRegion(currency)) {
            return setSettlementDateMyRegion(settlementDate);
        }
        else {
            return setSettlementDateOtherRegion(settlementDate);
        }
    }

    /**
     * if the region is not being AED or SAR, then returns true, and the weekend will be on Saturday and Sunday
     * if not the weekend will be Friday and Saturday
     * @param currency
     * @return
     */
    private boolean isMyRegion(String currency) {
        Currencies currencies = Currencies.valueOf(currency);
        switch (currencies) {
            case AED :
            case SAR :
                return false;
            default :
                return true;
        }
    }

    /**
     * set settlementdate taking in account that if the settlement date is on Saturday or Sunday
     * @param localDate
     * @return
     */
    private LocalDate setSettlementDateMyRegion(LocalDate localDate) {
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        switch (dayOfWeek) {
            case SATURDAY :
                return localDate.plusDays(2);
            case SUNDAY :
                return localDate.plusDays(1);
            default :
                return localDate;
        }
    }

    /**
     * set settlementdate taking in account that if the settlement date is on friday or Saturday
     * @param localDate
     * @return
     */
    private LocalDate setSettlementDateOtherRegion(LocalDate localDate) {
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        switch (dayOfWeek) {
            case FRIDAY:
                return localDate.plusDays(2);
            case SATURDAY:
                return localDate.plusDays(1);
            default :
                return localDate;
        }
    }
}