package com.rzsd.wechat.context;

import java.math.BigDecimal;

public class PriceContextDto {

    private BigDecimal weightFrom;
    private BigDecimal weightTo;
    private String priceType;
    private BigDecimal price;

    public BigDecimal getWeightFrom() {
        return weightFrom;
    }

    public void setWeightFrom(BigDecimal weightFrom) {
        this.weightFrom = weightFrom;
    }

    public BigDecimal getWeightTo() {
        return weightTo;
    }

    public void setWeightTo(BigDecimal weightTo) {
        this.weightTo = weightTo;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
