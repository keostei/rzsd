package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.logic.WechatHelpLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;

@Component
public class WechatHelpLogicImpl implements WechatHelpLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatHelpLogicImpl.class.getName());
    public static final String WECHAT_NEWS = "<xml><ToUserName><![CDATA[{0}]]></ToUserName>"
            + "<FromUserName><![CDATA[{1}]]></FromUserName>" + "<CreateTime>{2}</CreateTime>"
            + "<MsgType><![CDATA[{3}]]></MsgType>" + "<ArticleCount>1</ArticleCount>" + "<Articles>" + "<item>"
            + "<Title><![CDATA[欢迎添加取货客服]]></Title> " + "<Description><![CDATA[一号客服：张三]]></Description>"
            + "<PicUrl><![CDATA[https://mmbiz.qpic.cn/mmbiz_jpg/JakMBTAzDtD8VicMOb27cvNZpq9P3fCKGVib3PmWYQysMOKE1Z8bZbpib4ShbaXD95UctIgLr5pwzdXxnfW6H4uIw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1]]></PicUrl>"
            + "<Url><![CDATA[https://mp.weixin.qq.com/s?__biz=MjM5NDM0NzcyMg==&tempkey=OTI4X0UybzV6MDRvUXZTcmFOZDFpRkxpTk0xY0lNaUNUVWdhaTRJMF9Za0JmY1hjLW4zNTRRWWZvSkh2SlBTUEtvVnFKaWhIUlRLNWZjdHBOZFFoanU5d2RKVkZrbERPWHMwX1Q0dGhhVlUwVTRaYXQ0clo1aGFvS3VuQVoyS1BRYmtOcHRwVnd5eF9BVFcweGRqUWROWkZUU09OY2JPb240cWltR2lMU0F%2Bfg%3D%3D&chksm=25f3f837128471214b5c286427d04421349effa037366c3f1f9cd5e028e9db854623961090d9#rd]]></Url>"
            + "</item></Articles>" + "</xml>";

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
        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;

        StringBuilder sb = new StringBuilder();
        sb.append("本公众号为XX提供快捷的预约以及查询服务。\n");
        sb.append("通过发送指令可以快速预约取件，申请发货，查询物流信息。\n");
        sb.append("【添加地址】快速添加收件人信息，最多可以添加3个常用收件人信息。\n");
        sb.append("例：添加地址 张三 13912345678 江苏省南京市玄武区北京东路1号4栋203 \n");
        sb.append("【修改地址】快速修改收件人信息。\n");
        sb.append("例：修改地址 AXKJS1 张三 13912345678 江苏省南京市玄武区北京东路1号4栋203 \n");
        sb.append("【查询地址】查询系统中设置的常用收件人信息。\n");
        sb.append("例：查询地址 \n");
        sb.append("【创建账号】可以通过指令快速创建登陆账号密码，登陆我们辅助系统查询更多更详细信息。\n");
        sb.append("例：创建账号 zhangsan 张三 passW0rd \n");
        sb.append("【修改密码】修改登陆我们辅助系统的密码。\n");
        sb.append("例：修改密码 passW0rd \n");
        sb.append("【发货申请】提出发货申请，创建流水号，用于跟踪今后物流信息。\n");
        sb.append("例：发货 AJKXS1 \n");
        sb.append("【查询物流】查询物流信息。\n");
        sb.append("例：查询 \n");
        sb.append("注意指令之间的有空格，同一个项目之间不要又空格，特别是地址信息。");
        String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(), inputMsg.getToUserName(),
                returnTime, "text", sb.toString());
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;

    }

}
