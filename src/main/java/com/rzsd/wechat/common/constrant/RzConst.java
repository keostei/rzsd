package com.rzsd.wechat.common.constrant;

import java.math.BigInteger;

public class RzConst {

    public static final String WECHAT_MESSAGE = "<xml><ToUserName><![CDATA[{0}]]></ToUserName>"
            + "<FromUserName><![CDATA[{1}]]></FromUserName>" + "<CreateTime>{2}</CreateTime>"
            + "<MsgType><![CDATA[{3}]]></MsgType>" + "<Content><![CDATA[{4}]]></Content></xml>";

    public static BigInteger SYS_ADMIN_ID = new BigInteger("0");
}
