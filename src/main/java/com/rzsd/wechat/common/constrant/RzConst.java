package com.rzsd.wechat.common.constrant;

import java.math.BigInteger;

import com.rzsd.wechat.configuration.PropertiesListenerConfig;

public class RzConst {

    public static final String WECHAT_MESSAGE = PropertiesListenerConfig.getProperty("text.message");

    public static BigInteger SYS_ADMIN_ID = new BigInteger("0");

    public static int CUSTOM_ID_LENGTH = 4;
}
