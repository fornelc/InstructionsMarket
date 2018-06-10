package com.jpmorgan.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jpmorgan.market.controller.MarketController;
import com.jpmorgan.market.repository.MarketRepository;
import com.jpmorgan.market.repository.impl.MarketRepositoryImpl;
import com.jpmorgan.market.service.MarketService;
import com.jpmorgan.market.service.impl.MarketServiceImpl;
import com.jpmorgan.market.utils.LocalDateDeserializer;
import com.jpmorgan.market.utils.LocalDateSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@SpringBootApplication
public class MainController {

    public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MainController.class, args);

        MarketController bean = ctx.getBean(MarketController.class);
        bean.showReport();
    }

    @Bean
    public MarketService marketService() { return new MarketServiceImpl(); }

    @Bean
    public MarketRepository marketRepository() { return new MarketRepositoryImpl(); }

    @Bean
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

}
