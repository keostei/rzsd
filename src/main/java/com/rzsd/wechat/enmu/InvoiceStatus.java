package com.rzsd.wechat.enmu;

public enum InvoiceStatus {
    // 已预约
    YUYUE("1", "已预约"),
    // 已取货
    QUHUO("2", "待发货"),
    // 打包完成(未使用)
    DABAO("3", "打包完成"),
    // 已出库
    CHUKU("4", "已出库"),
    // 日本通关中
    RBTGZ("5", "日本通关中"),
    // 专机起飞
    ZJQF("6", "专机起飞"),
    // 到达清关口岸
    GNQGKA("7", "到达清关口岸"),
    // 已清关
    QGWC("8", "已清关"),;
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
