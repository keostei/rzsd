package com.rzsd.wechat.common.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class TInvoice extends BaseDataAccessDto {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6774073877276105064L;
    private BigInteger invoiceId;
    private Date invoiceDate;
    private String invoiceTimeCd;
    private String invoideAddress;
    private BigDecimal weight;
    private String customCd;
    private String name;
    private String telNo;
    private String address;
    private String invoiceRequirement;
    private String invoiceStatus;
    private String invoiceStatusName;
    private BigDecimal totalWeight;
    private BigDecimal invoiceAmountJpy;
    private BigDecimal invoiceAmountCny;

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

    public String getInvoiceTimeCd() {
        return invoiceTimeCd;
    }

    public void setInvoiceTimeCd(String invoiceTimeCd) {
        this.invoiceTimeCd = invoiceTimeCd;
    }

    public String getInvoideAddress() {
        return invoideAddress;
    }

    public void setInvoideAddress(String invoideAddress) {
        this.invoideAddress = invoideAddress;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
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

    public String getInvoiceRequirement() {
        return invoiceRequirement;
    }

    public void setInvoiceRequirement(String invoiceRequirement) {
        this.invoiceRequirement = invoiceRequirement;
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
}
