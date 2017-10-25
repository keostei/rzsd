package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rzsd.wechat.logic.WechatHelpLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;

@Component
public class WechatHelpLogicImpl implements WechatHelpLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatHelpLogicImpl.class.getName());
    public static final String WECHAT_NEWS = "<xml><ToUserName><![CDATA[{0}]]></ToUserName>"
            + "<FromUserName><![CDATA[{1}]]></FromUserName>" + "<CreateTime>{2}</CreateTime>"
            + "<MsgType><![CDATA[{3}]]></MsgType>" + "<ArticleCount>2</ArticleCount>" + "<Articles>" + "<item>"
            + "<Title><![CDATA[欢迎添加取货客服]]></Title> " + "<Description><![CDATA[一号客服：张三]]></Description>"
            + "<PicUrl><![CDATA[https://mmbiz.qpic.cn/mmbiz_jpg/JakMBTAzDtD8VicMOb27cvNZpq9P3fCKGVib3PmWYQysMOKE1Z8bZbpib4ShbaXD95UctIgLr5pwzdXxnfW6H4uIw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1]]></PicUrl>"
            + "<Url><![CDATA[https://mp.weixin.qq.com/s?__biz=MjM5NDM0NzcyMg==&tempkey=OTI4X0UybzV6MDRvUXZTcmFOZDFpRkxpTk0xY0lNaUNUVWdhaTRJMF9Za0JmY1hjLW4zNTRRWWZvSkh2SlBTUEtvVnFKaWhIUlRLNWZjdHBOZFFoanU5d2RKVkZrbERPWHMwX1Q0dGhhVlUwVTRaYXQ0clo1aGFvS3VuQVoyS1BRYmtOcHRwVnd5eF9BVFcweGRqUWROWkZUU09OY2JPb240cWltR2lMU0F%2Bfg%3D%3D&chksm=25f3f837128471214b5c286427d04421349effa037366c3f1f9cd5e028e9db854623961090d9#rd]]></Url>"
            + "</item>" + "<item>" + "<Title><![CDATA[欢迎添加取货客服]]></Title>"
            + "<Description><![CDATA[一号客服：李四]]></Description>"
            + "<PicUrl><![CDATA[https://mmbiz.qpic.cn/mmbiz_jpg/JakMBTAzDtD8VicMOb27cvNZpq9P3fCKGJpjs2mGYBMia9baXS9gL66yleNcYQVk1mTF21BJhKYhWic8ibO4FGhbnA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=]]></PicUrl>"
            + "<Url><![CDATA[http://122.112.229.36]]></Url>" + "</item>" + "</Articles>" + "</xml>";

    @Override
    public void doAppointment(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {

        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;

        String msg = MessageFormat.format(WECHAT_NEWS, inputMsg.getFromUserName(), inputMsg.getToUserName(), returnTime,
                "news");
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;

    }

}
