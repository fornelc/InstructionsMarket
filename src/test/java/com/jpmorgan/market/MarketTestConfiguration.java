package com.jpmorgan.market;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmorgan.market.model.Market;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarketTestConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(MarketTestConfiguration.class);

    public static List<Market> getMarkets(String marketsFile, ObjectMapper objectMapper) {
        final Resource resource = new ClassPathResource(marketsFile);
        List<Market> markets = new ArrayList<>();
        if (resource.isReadable()) {
            try {
                markets = objectMapper.readValue(resource.getFile(), new TypeReference<List<Market>>() {});
            } catch (IOException e) {
                LOG.error("An error occurred parsing markets JSON. ", e);
            }
        }
        return markets;
    }
}
