package com.rzsd.wechat.entity;

import java.io.Serializable;

public class BarCodeItem implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8257954290033718802L;
    private String type;
    private String result;
    private String file;
    private int cnt;
    private String dt;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
