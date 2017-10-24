package com.rzsd.wechat.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import com.rzsd.wechat.util.InputMessage;

public interface WechatInvoiceLogic {

    void createInvoice(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException;

    void queryInvoice(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException;
}
