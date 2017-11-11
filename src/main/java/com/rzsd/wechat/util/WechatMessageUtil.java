package com.rzsd.wechat.util;

import java.text.MessageFormat;
import java.util.Calendar;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.configuration.PropertiesListenerConfig;

public class WechatMessageUtil {

    public static String getTextMessage(String msgId, String openId, String wechatName, Object... params) {
        Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;
        return MessageFormat.format(RzConst.WECHAT_MESSAGE, openId, wechatName, String.valueOf(returnTime), "text",
                MessageFormat.format(PropertiesListenerConfig.getProperty(msgId), params));
    }
}
