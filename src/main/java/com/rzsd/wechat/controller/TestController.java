package com.rzsd.wechat.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzsd.wechat.annotation.WebAuth;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatHelpLogic;
import com.rzsd.wechat.logic.WechatInvoiceLogic;
import com.rzsd.wechat.logic.WechatQuickLogic;
import com.rzsd.wechat.logic.WechatSubscribeLogic;
import com.rzsd.wechat.logic.WechatUnSubscribeLogic;
import com.rzsd.wechat.logic.WechatUserLogic;
import com.rzsd.wechat.util.InputMessage;

@RestController
@RequestMapping("/rest")
public class TestController {

    @Autowired
    private WechatInvoiceLogic wechatInvoiceLogicImpl;
    @Autowired
    private WechatUserLogic wechatUserLogicImpl;
    @Autowired
    private WechatHelpLogic wechatHelpLogicImpl;
    @Autowired
    private WechatSubscribeLogic wechatSubscribeLogicImpl;
    @Autowired
    private WechatUnSubscribeLogic wechatUnSubscribeLogicImpl;
    @Autowired
    private WechatCustomIdLogic wechatCustomIdLogicImpl;
    @Autowired
    private WechatQuickLogic wechatQuickLogicImpl;

    @RequestMapping("test")
    @WebAuth
    public void test(Model model, HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {

        InputMessage inputMsg = new InputMessage();
        inputMsg.setFromUserName("openId-test-1112001");
        inputMsg.setToUserName("KEOSIMAGE");
        inputMsg.setContent("2");
        wechatSubscribeLogicImpl.execute(inputMsg, response);
        // wechatUnSubscribeLogicImpl.execute(inputMsg, response);
        // wechatCustomIdLogicImpl.createCustomInfo(inputMsg, response);
        // wechatInvoiceLogicImpl.createInvoice(inputMsg, response);
        // wechatInvoiceLogicImpl.queryInvoice(inputMsg, response);
        // wechatUserLogicImpl.createUser(inputMsg, response);
        // wechatHelpLogicImpl.doAppointment(inputMsg, response);
        // wechatQuickLogicImpl.execute(inputMsg, response);
        // inputMsg.setContent("张三new");
        // wechatQuickLogicImpl.execute(inputMsg, response);
        // inputMsg.setContent("13951741096");
        // wechatQuickLogicImpl.execute(inputMsg, response);
        // inputMsg.setContent("江苏省南京市奥体大街69号新城科技园5栋5楼");
        // wechatQuickLogicImpl.execute(inputMsg, response);
    }
}
