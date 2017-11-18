package com.rzsd.wechat.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import com.rzsd.wechat.util.InputMessage;

public interface WechatCustomIdLogic {

    String generateId(boolean isProxy, char proxyCd);

    void initUserInfo(InputMessage inputMsg, HttpServletResponse response);

    void queryCustomInfo(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException;

    void createCustomInfo(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException;

    void updateCustomInfo(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException;
}
