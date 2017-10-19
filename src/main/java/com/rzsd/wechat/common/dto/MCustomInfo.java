package com.rzsd.wechat.common.dto;

public class MCustomInfo extends BaseDataAccessDto {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2458536620951462012L;
    private String oldCustomId;
    private String customId;
    private String rowNo;
    private String name;
    private String telNo;
    private String address;

    public String getOldCustomId() {
        return oldCustomId;
    }

    public void setOldCustomId(String oldCustomId) {
        this.oldCustomId = oldCustomId;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
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
}
