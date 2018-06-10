package com.jpmorgan.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmorgan.market.MarketTestConfiguration;
import com.jpmorgan.market.model.Instruction;
import com.jpmorgan.market.model.Market;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MarketControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(MarketController.class);

    @Autowired
    ObjectMapper objectMapper;

    @Value("${markets.file:data.json}")
    private String marketsFile;

    private List<Market> markets;

    private MarketController sut;

    private static final String METHOD_NAME_AMOUNT_SETTLED = "getReportAmountSettled";

    private static final String METHOD_NAME_RANKING_ENTITIES = "getRankingEntities";

    @Before
    public void setUp() {
        if(sut == null) {
            sut = new MarketController();
        }

        if(markets == null) {
            markets = MarketTestConfiguration.getMarkets(marketsFile, objectMapper);
        }
    }

    @Test
    public void getReportAmountSettledSell() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getPrivateMethod(METHOD_NAME_AMOUNT_SETTLED);
        Map<LocalDate, BigDecimal> map = (Map<LocalDate, BigDecimal>) method.invoke(sut, markets, Instruction.S);

        assertThat(map, is(expectedSettledSellMap()));
    }

    @Test
    public void getReportAmountSettledBuy() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getPrivateMethod(METHOD_NAME_AMOUNT_SETTLED);
        Map<LocalDate, BigDecimal> map = (Map<LocalDate, BigDecimal>) method.invoke(sut, markets, Instruction.B);

        assertThat(map, is(expectedSettledBuyMap()));
    }

    @Test
    public void getRankingEntitiesSell() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getPrivateMethod(METHOD_NAME_RANKING_ENTITIES);
        Map<String, BigDecimal> map = (Map<String, BigDecimal>) method.invoke(sut, markets, Instruction.S);

        assertThat(map, is(expectedRankingEntitiesSellMap()));
    }

    @Test
    public void getRankingEntitiesBuy() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getPrivateMethod(METHOD_NAME_RANKING_ENTITIES);
        Map<String, BigDecimal> map = (Map<String, BigDecimal>) method.invoke(sut, markets, Instruction.B);

        assertThat(map, is(expectedRankingEntitiesBuyMap()));
    }

    private Method getPrivateMethod(String methodName) throws NoSuchMethodException {
        Method method = MarketController.class.getDeclaredMethod(methodName, List.class, Enum.class);
        method.setAccessible(true);

        return method;
    }

    private Map<LocalDate, BigDecimal> expectedSettledSellMap() {
        Map<LocalDate, BigDecimal> expected = new HashMap<>();
        expected.put(LocalDate.of(2018, 06, 30), getBigDecimal(14320.72));
        expected.put(LocalDate.of(2018, 05, 07), getBigDecimal(6952.52));
        expected.put(LocalDate.of(2018, 05, 04), getBigDecimal(4041.79));
        expected.put(LocalDate.of(2018, 06, 04), getBigDecimal(14464.43));
        expected.put(LocalDate.of(2018, 05, 03), getBigDecimal(1033.95));
        return expected;
    }

    private Map<LocalDate, BigDecimal> expectedSettledBuyMap() {
        Map<LocalDate, BigDecimal> expected = new HashMap<>();
        expected.put(LocalDate.of(2018, 04, 30), getBigDecimal(2161.89));
        expected.put(LocalDate.of(2018, 06, 30), getBigDecimal(16143.84));
        expected.put(LocalDate.of(2018, 05, 07), getBigDecimal(958.97));
        expected.put(LocalDate.of(2018, 05, 04), getBigDecimal(5357.72));
        expected.put(LocalDate.of(2018, 06, 03), getBigDecimal(4235.44));
        return expected;
    }

    private Map<String, BigDecimal> expectedRankingEntitiesSellMap() {
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("HSBC", getBigDecimal(21416.95));
        expected.put("BNP", getBigDecimal(14320.72));
        expected.put("Citi", getBigDecimal(5075.74));
        return expected;
    }

    private Map<String, BigDecimal> expectedRankingEntitiesBuyMap() {
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("BNP", getBigDecimal(16143.84));
        expected.put("Citi", getBigDecimal(7519.61));
        expected.put("HSBC", getBigDecimal(5194.41));
        return expected;
    }

    private BigDecimal getBigDecimal(double value) {
        BigDecimal b = new BigDecimal(value, MathContext.DECIMAL64);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
