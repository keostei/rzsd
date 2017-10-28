package com.rzsd.wechat.common.dto;

import java.io.Serializable;

public class BaseJsonDto implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7422784647678069246L;

    private boolean isFail;
    private String msgId;
    private String message;
    private String optStr;

    public boolean isFail() {
        return isFail;
    }

    public void setFail(boolean isFail) {
        this.isFail = isFail;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOptStr() {
        return optStr;
    }

    public void setOptStr(String optStr) {
        this.optStr = optStr;
    }

}
