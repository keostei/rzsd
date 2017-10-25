package com.rzsd.wechat.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import com.rzsd.wechat.util.InputMessage;

public interface WechatHelpLogic {

    void doAppointment(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException;
}
