package com.rzsd.wechat.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import com.rzsd.wechat.util.InputMessage;

public interface WechatUserLogic {

    void createUser(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException;

    void updatePassword(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException;

}
