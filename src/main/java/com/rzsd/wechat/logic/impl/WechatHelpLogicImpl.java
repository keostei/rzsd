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
import com.rzsd.wechat.util.WechatMessageUtil;

@Component
public class WechatHelpLogicImpl implements WechatHelpLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatHelpLogicImpl.class.getName());
    public static final String WECHAT_NEWS = "<xml><ToUserName><![CDATA[{0}]]></ToUserName>"
            + "<FromUserName><![CDATA[{1}]]></FromUserName>" + "<CreateTime>{2}</CreateTime>"
            + "<MsgType><![CDATA[{3}]]></MsgType>" + "<ArticleCount>1</ArticleCount>" + "<Articles>" + "<item>"
            + "<Title><![CDATA[欢迎添加取货客服]]></Title> " + "<Description><![CDATA[一号客服：张三]]></Description>"
            + "<PicUrl><![CDATA[https://mmbiz.qpic.cn/mmbiz_jpg/JakMBTAzDtD8VicMOb27cvNZpq9P3fCKGVib3PmWYQysMOKE1Z8bZbpib4ShbaXD95UctIgLr5pwzdXxnfW6H4uIw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1]]></PicUrl>"
            + "<Url><![CDATA[https://mp.weixin.qq.com/s/T6dIo5abpS8UNhZywRMcpg]]></Url>" + "</item></Articles>"
            + "</xml>";

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

    @Override
    public void doHelp(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        String msg = WechatMessageUtil.getTextMessage("text.service.help", inputMsg.getFromUserName(),
                inputMsg.getToUserName());
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;

    }

    @Override
    public void doUnknown(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        LOGGER.warn("无法识别的命令：" + inputMsg.getContent());
        String msg = WechatMessageUtil.getTextMessage("text.service.error.unknown", inputMsg.getFromUserName(),
                inputMsg.getToUserName());
        LOGGER.debug(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
    }

}
