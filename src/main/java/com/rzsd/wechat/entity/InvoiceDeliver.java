package com.rzsd.wechat.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class InvoiceDeliver implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3636866474835393380L;
    private BigInteger invoiceId;
    private Date invoiceDate;
    private BigDecimal weight;
    private String strWeight;
    private String customCd;
    private String name;
    private String telNo;
    private String address;
    private String invoiceStatus;
    private String invoiceStatusName;
    private BigDecimal totalWeight;
    private BigDecimal invoiceAmountJpy;
    private BigDecimal invoiceAmountCny;
    private BigDecimal price;
    private String strPrice;
    private String logNo;
    private String trackingNo;
    private String newTrackingNo;
    private String importResult;

    public BigInteger getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(BigInteger invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getStrWeight() {
        return strWeight;
    }

    public void setStrWeight(String strWeight) {
        this.strWeight = strWeight;
    }

    public String getCustomCd() {
        return customCd;
    }

    public void setCustomCd(String customCd) {
        this.customCd = customCd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceStatusName() {
        return invoiceStatusName;
    }

    public void setInvoiceStatusName(String invoiceStatusName) {
        this.invoiceStatusName = invoiceStatusName;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getInvoiceAmountJpy() {
        return invoiceAmountJpy;
    }

    public void setInvoiceAmountJpy(BigDecimal invoiceAmountJpy) {
        this.invoiceAmountJpy = invoiceAmountJpy;
    }

    public BigDecimal getInvoiceAmountCny() {
        return invoiceAmountCny;
    }

    public void setInvoiceAmountCny(BigDecimal invoiceAmountCny) {
        this.invoiceAmountCny = invoiceAmountCny;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStrPrice() {
        return strPrice;
    }

    public void setStrPrice(String strPrice) {
        this.strPrice = strPrice;
    }

    public String getLogNo() {
        return logNo;
    }

    public void setLogNo(String logNo) {
        this.logNo = logNo;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getNewTrackingNo() {
        return newTrackingNo;
    }

    public void setNewTrackingNo(String newTrackingNo) {
        this.newTrackingNo = newTrackingNo;
    }

    public String getImportResult() {
        return importResult;
    }

    public void setImportResult(String importResult) {
        this.importResult = importResult;
    }

}
