package com.rzsd.wechat.enmu;

public enum InvoiceDetailStatus {
    DABAO("1", "打包完成"), CHUKU("2", "已出库"), RBHGCK("3", "到达日本海关仓库"), RBHGQGWC("4", "日本海关清关完成"), ZJQF("5",
            "专机起飞"), GNHGCK("6", "到达国内海关仓库"), GNHGQGWC("7", "国内清关完成"),;
    private String _code;
    private String _name;

    InvoiceDetailStatus(String _code, String _name) {
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
        for (InvoiceDetailStatus s : InvoiceDetailStatus.values()) {
            if (_code.equals(s.getCode())) {
                return s.getName();
            }
        }
        return null;
    }
}
