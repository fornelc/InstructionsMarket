package com.jpmorgan.market.repository;

import com.jpmorgan.market.model.Market;

import java.util.List;

public interface MarketRepository {
    List<Market> getMarketsData();
}
