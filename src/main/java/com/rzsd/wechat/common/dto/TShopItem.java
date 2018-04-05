package com.rzsd.wechat.common.dto;

import java.math.BigInteger;

public class TShopItem extends BaseDataAccessDto {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1849251900273318127L;

    public BigInteger getShopId() {
        return shopId;
    }

    public void setShopId(BigInteger shopId) {
        this.shopId = shopId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigInteger getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigInteger totalAmount) {
        this.totalAmount = totalAmount;
    }

    private BigInteger shopId;
    private String barcode;
    private BigInteger totalAmount;

}
