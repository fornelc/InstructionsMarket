package com.jpmorgan.market.controller;

import com.jpmorgan.market.model.Instruction;
import com.jpmorgan.market.model.Market;
import com.jpmorgan.market.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MarketController {

    @Autowired
    MarketService marketService;

    /**
     * Set list of market class by getting the data from data.json
     * @throws JsonParseException
     */
    public void showReport() throws JsonParseException {
        List<Market> markets = marketService.getMarkets();

        if(markets != null && !markets.isEmpty()) {
            showAmmountUSDSettled(markets);
            printRankingEntitiesInConsole(markets);
        } else {
            System.out.println("---Something is going not very well :( , check the logs.. :)");
        }
    }

    /**
     * Call to the method that print the ranking of entities on the console
     * @param markets
     */
    private void printRankingEntitiesInConsole(List<Market> markets) {
        System.out.println("---Ranking of entities based on incoming----");
        Map<String, BigDecimal> rankingEntitiesSell = getRankingEntities(markets, Instruction.S);
        printRankingEntitiesInConsole(rankingEntitiesSell);
        System.out.println("---Ranking of entities based on outgoing----");
        Map<String, BigDecimal> rankingEntitiesBuy = getRankingEntities(markets, Instruction.B);
        printRankingEntitiesInConsole(rankingEntitiesBuy);
    }

    /**
     * Call to the method that print Ammount USD settled on the console
     * @param markets
     */
    private void showAmmountUSDSettled(List<Market> markets) {
        System.out.println("---Amount in USD settled incoming everyday----");
        Map<LocalDate, BigDecimal> amountSettledSell = getReportAmountSettled(markets, Instruction.S);
        printAmountSettledInConsole(amountSettledSell);
        System.out.println("---Amount in USD settled outgoing everyday----");
        Map<LocalDate, BigDecimal> amountSettledBuy = getReportAmountSettled(markets, Instruction.B);
        printAmountSettledInConsole(amountSettledBuy);
    }

    /**
     * Get report ammount settled by iterating the list of market class
     * @param markets
     * @param instruction
     * @return
     */
    private Map<LocalDate, BigDecimal> getReportAmountSettled(List<Market> markets, Enum instruction) {
        Map<LocalDate, BigDecimal> map = markets.stream()
                .filter(m -> m.getBuySell().equals(String.valueOf(instruction)))
                .collect(Collectors.groupingBy(Market::getInstructionDate,
                        Collectors.reducing(BigDecimal.ZERO,
                                Market::getUSDAmountTrade,
                                BigDecimal::add)));
        return map;
    }

    /**
     * Get ranking of entities by iterating the list of market class
     * @param markets
     * @param instruction
     * @return
     */
    private Map<String, BigDecimal> getRankingEntities(List<Market> markets, Enum instruction) {
        Map<String, BigDecimal> map = markets.stream()
                .filter(m -> m.getBuySell().equals(String.valueOf(instruction)))
                .collect(Collectors.groupingBy(Market::getEntity,
                        Collectors.reducing(BigDecimal.ZERO,
                                Market::getUSDAmountTrade,
                                BigDecimal::add)));

        map = orderMap(map);

        return map;
    }

    /**
     * Order map with to get the entities ranking
     * @param unsortMap
     * @return
     */
    private Map<String, BigDecimal> orderMap(Map<String, BigDecimal> unsortMap) {
        Map<String, BigDecimal> result = unsortMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }

    /**
     * print Ammount USD settled on the console
     * @param amountSettled
     */
    private void printAmountSettledInConsole(Map<LocalDate, BigDecimal> amountSettled) {
        amountSettled.forEach((entity,amountTrade)->System.out.println(entity+"\t"+ amountTrade));
    }

    /**
     * print ranking of entities on the console
     * @param rankingEntities
     */
    private void printRankingEntitiesInConsole(Map<String, BigDecimal> rankingEntities) {
        final int[] count = {0};
        rankingEntities.forEach((entity, amountTrade)-> {
                System.out.println(++count[0] +": "+entity+"\t"+amountTrade);
            });
    }
}
