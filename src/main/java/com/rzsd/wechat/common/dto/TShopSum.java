package com.rzsd.wechat.common.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class TShopSum extends BaseDataAccessDto {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6542386864415078930L;
    private BigInteger shopId;
    private String barcode;
    private String itemName;
    private BigDecimal priceRmb;
    private BigDecimal priceRmbSum;
    private BigDecimal priceShow;
    private BigDecimal priceShowSum;
    private BigInteger sumCnt;
    private Date dateFrom;
    private Date dateTo;

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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getPriceRmb() {
        return priceRmb;
    }

    public void setPriceRmb(BigDecimal priceRmb) {
        this.priceRmb = priceRmb;
    }

    public BigDecimal getPriceRmbSum() {
        return priceRmbSum;
    }

    public void setPriceRmbSum(BigDecimal priceRmbSum) {
        this.priceRmbSum = priceRmbSum;
    }

    public BigDecimal getPriceShow() {
        return priceShow;
    }

    public void setPriceShow(BigDecimal priceShow) {
        this.priceShow = priceShow;
    }

    public BigDecimal getPriceShowSum() {
        return priceShowSum;
    }

    public void setPriceShowSum(BigDecimal priceShowSum) {
        this.priceShowSum = priceShowSum;
    }

    public BigInteger getSumCnt() {
        return sumCnt;
    }

    public void setSumCnt(BigInteger sumCnt) {
        this.sumCnt = sumCnt;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

}
