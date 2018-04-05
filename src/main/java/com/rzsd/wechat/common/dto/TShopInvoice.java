package com.rzsd.wechat.common.dto;

import java.math.BigInteger;

public class TShopInvoice extends BaseDataAccessDto {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3153610246665158492L;
    private BigInteger shopInvoiceId;
    private BigInteger shopId;
    private String barcode;
    private BigInteger count;
    private String type;
    private String reason;

    public BigInteger getShopInvoiceId() {
        return shopInvoiceId;
    }

    public void setShopInvoiceId(BigInteger shopInvoiceId) {
        this.shopInvoiceId = shopInvoiceId;
    }

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

    public BigInteger getCount() {
        return count;
    }

    public void setCount(BigInteger count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
