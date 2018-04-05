package com.rzsd.wechat.entity;

import java.io.Serializable;
import java.util.List;

public class BulkShopInvoice implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1028264599958384786L;
    private int shopId;
    private String type;
    private String reason;
    private List<BarCodeItem> barCodeItemLst;

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
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

    public List<BarCodeItem> getBarCodeItemLst() {
        return barCodeItemLst;
    }

    public void setBarCodeItemLst(List<BarCodeItem> barCodeItemLst) {
        this.barCodeItemLst = barCodeItemLst;
    }
}
