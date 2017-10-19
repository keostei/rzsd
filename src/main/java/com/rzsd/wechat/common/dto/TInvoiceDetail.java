package com.rzsd.wechat.common.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TInvoiceDetail extends BaseDataAccessDto {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -797392369728824283L;
    private BigInteger invoiceId;
    private String rowNo;
    private String lotNo;
    private String trackingNo;
    private BigDecimal weight;
    private String status;
    private String statusName;

    public BigInteger getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(BigInteger invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }

    public String getLotNo() {
        return lotNo;
    }

    public void setLotNo(String lotNo) {
        this.lotNo = lotNo;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

}
