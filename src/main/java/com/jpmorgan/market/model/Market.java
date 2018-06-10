package com.jpmorgan.market.model;


import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;

public class Market {

    private String entity;
    private String buySell;
    private Double agreedFx;
    private String currency;
    private LocalDate instructionDate;
    private LocalDate settlementDate;
    private Integer units;
    private Double priceUnit;
    private BigDecimal usdAmountTrade;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getBuySell() {
        return buySell;
    }

    public void setBuySell(String buySell) {
        this.buySell = buySell;
    }

    public Double getAgreedFx() {
        return agreedFx;
    }

    public void setAgreedFx(Double agreedFx) {
        this.agreedFx = agreedFx;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getInstructionDate() {
        return instructionDate;
    }

    public void setInstructionDate(LocalDate instructionDate) {
        this.instructionDate = instructionDate;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Double getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Double priceUnit) {
        this.priceUnit = priceUnit;
    }

    public BigDecimal getUSDAmountTrade() {
        if(usdAmountTrade == null) {
            double amountTrade = priceUnit * units * agreedFx;
            BigDecimal b = new BigDecimal(amountTrade, MathContext.DECIMAL64);
            usdAmountTrade = b.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return usdAmountTrade;
    }
}
