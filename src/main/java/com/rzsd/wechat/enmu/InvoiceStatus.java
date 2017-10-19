package com.rzsd.wechat.enmu;

public enum InvoiceStatus {
    YUYUE("1", "已预约"), QUHUO("2", "已取货"), DABAO("3", "打包完成"), CHUKU("4", "已出库"),;
    private String _code;
    private String _name;

    InvoiceStatus(String _code, String _name) {
        this._code = _code;
        this._name = _name;
    };

    public String getCode() {
        return this._code;
    }

    public String getName() {
        return this._name;
    }

    public static String getCodeAsName(String _code) {
        for (InvoiceStatus s : InvoiceStatus.values()) {
            if (_code.equals(s.getCode())) {
                return s.getName();
            }
        }
        return null;
    }
}
