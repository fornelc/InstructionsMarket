package com.jpmorgan.market.repository.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmorgan.market.model.Market;
import com.jpmorgan.market.repository.MarketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarketRepositoryImpl implements MarketRepository {

    private static final Logger LOG = LoggerFactory.getLogger(MarketRepositoryImpl.class);

    @Value("${markets.file:data.json}")
    private String marketsFile;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * Set List of markets by mapping data.json into List<Market>
     * @return
     */
    public List<Market> getMarketsData() {
        final Resource resource = new ClassPathResource(marketsFile);
        if (resource.isReadable()) {
            try {
                return objectMapper.readValue(resource.getFile(), new TypeReference<List<Market>>() {});
            } catch (IOException e) {
                LOG.error("An error occurred parsing markets JSON. ", e);
            }
        }

        return new ArrayList<>();
    }
}
