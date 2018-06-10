package com.jpmorgan.market.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmorgan.market.MarketTestConfiguration;
import com.jpmorgan.market.model.Currencies;
import com.jpmorgan.market.model.Market;
import com.jpmorgan.market.service.impl.MarketServiceImpl;
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
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MarketServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(MarketServiceImpl.class);

    private static final String METHOD_NAME_IS_MY_REGION = "isMyRegion";

    MarketService sut;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${markets.file:data.json}")
    private String marketsFile;

    List<Market> markets;

    @Before
    public void setUp() {
        if(sut == null)
            sut = new MarketServiceImpl();

        if(markets == null) {
            markets = MarketTestConfiguration.getMarkets(marketsFile, objectMapper);
        }
    }

    @Test
    public void isMyRegion() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getPrivateMethod(METHOD_NAME_IS_MY_REGION, String.class);
        boolean isMyRegion = (boolean) method.invoke(sut, String.valueOf(Currencies.AED));
        assertThat(isMyRegion, is(false));
        isMyRegion = (boolean) method.invoke(sut, String.valueOf(Currencies.SAR));
        assertThat(isMyRegion, is(false));
        isMyRegion = (boolean) method.invoke(sut, String.valueOf(Currencies.SGP));
        assertThat(isMyRegion, is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isMyRegionIllegalArgumentException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getPrivateMethod(METHOD_NAME_IS_MY_REGION, String.class);
        try {
            method.invoke(sut, "other");
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException();
        }

    }

    @Test(expected = NullPointerException.class)
    public void isMyRegionNullPointerException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getPrivateMethod(METHOD_NAME_IS_MY_REGION, String.class);
        try {
            method.invoke(sut, new Object[]{ null });
        } catch (InvocationTargetException e) {
            throw new NullPointerException();
        }
    }

    @Test
    public void getSettlementDateMyRegion() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getPrivateMethod("setSettlementDateMyRegion", LocalDate.class);

        LocalDate localDate = LocalDate.of(2018, 02, 03);
        localDate = (LocalDate) method.invoke(sut, localDate);
        LocalDate localDatePlus2Days = LocalDate.of(2018, 02, 05);
        assertThat(localDate, is(localDatePlus2Days));

        localDate = LocalDate.of(2018, 02, 04);
        localDate = (LocalDate) method.invoke(sut, localDate);
        LocalDate localDatePlus1Day = LocalDate.of(2018, 02, 05);
        assertThat(localDate, is(localDatePlus1Day));

        localDate = LocalDate.of(2018, 02, 05);
        localDate = (LocalDate) method.invoke(sut, localDate);
        assertThat(localDate, is(localDate));
    }

    @Test
    public void getSettlementDateOtherRegion() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = getPrivateMethod("setSettlementDateOtherRegion", LocalDate.class);

        LocalDate localDate = LocalDate.of(2018, 03, 9);
        localDate = (LocalDate) method.invoke(sut, localDate);
        LocalDate localDatePlus2Days = LocalDate.of(2018, 03, 11);
        assertThat(localDate, is(localDatePlus2Days));

        localDate = LocalDate.of(2018, 03, 17);
        localDate = (LocalDate) method.invoke(sut, localDate);
        LocalDate localDatePlus1Day = LocalDate.of(2018, 03, 18);
        assertThat(localDate, is(localDatePlus1Day));

        localDate = LocalDate.of(2018, 03, 25);
        localDate = (LocalDate) method.invoke(sut, localDate);
        assertThat(localDate, is(localDate));
    }

    private Method getPrivateMethod(String methodName, Class<?> cl) throws NoSuchMethodException {
        Method method = MarketServiceImpl.class.getDeclaredMethod(methodName, cl);
        method.setAccessible(true);

        return method;
    }
}
