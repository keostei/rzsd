package com.rzsd.wechat.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.enmu.MsgType;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatHelpLogic;
import com.rzsd.wechat.logic.WechatInvoiceLogic;
import com.rzsd.wechat.logic.WechatSubscribeLogic;
import com.rzsd.wechat.logic.WechatUnSubscribeLogic;
import com.rzsd.wechat.logic.WechatUserLogic;
import com.rzsd.wechat.util.ImageMessage;
import com.rzsd.wechat.util.InputMessage;
import com.rzsd.wechat.util.OutputMessage;
import com.rzsd.wechat.util.SerializeXmlUtil;
import com.thoughtworks.xstream.XStream;

@Service
public class WechatCmdServiceImpl implements WechatCmdService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatCmdServiceImpl.class.getName());

    @Autowired
    private WechatSubscribeLogic wechatSubscribeLogicImpl;
    @Autowired
    private WechatUnSubscribeLogic wechatUnSubscribeLogicImpl;
    @Autowired
    private WechatCustomIdLogic wechatCustomIdLogicImpl;
    @Autowired
    private WechatUserLogic wechatUserLogicImpl;
    @Autowired
    private WechatInvoiceLogic wechatInvoiceLogicImpl;
    @Autowired
    private WechatHelpLogic wechatHelpLogicImpl;

    @Override
    public void acceptMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 处理接收消息
        ServletInputStream in = request.getInputStream();
        // 将POST流转换为XStream对象
        XStream xs = SerializeXmlUtil.createXstream();
        xs.processAnnotations(InputMessage.class);
        xs.processAnnotations(OutputMessage.class);
        // 将指定节点下的xml节点数据映射为对象
        xs.alias("xml", InputMessage.class);
        // 将流转换为字符串
        StringBuilder xmlMsg = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            xmlMsg.append(new String(b, 0, n, "UTF-8"));
        }
        // 将xml内容转换为InputMessage对象
        InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());

        String serverName = inputMsg.getToUserName();// 服务端
        String custermName = inputMsg.getFromUserName();// 客户端
        long createTime = inputMsg.getCreateTime();// 接收时间
        Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;// 返回时间

        // 取得消息类型
        String msgType = inputMsg.getMsgType();
        LOGGER.info("消息類型：" + msgType);
        // 根据消息类型获取对应的消息内容
        if (msgType.equals(MsgType.Event.toString())) {
            LOGGER.info("用戶操作事件：" + inputMsg.getEvent());
            if ("subscribe".equals(inputMsg.getEvent())) {
                wechatSubscribeLogicImpl.execute(inputMsg, response);
            } else if ("unsubscribe".equals(inputMsg.getEvent())) {
                wechatUnSubscribeLogicImpl.execute(inputMsg, response);
            }
        } else if (msgType.equals(MsgType.Text.toString())) {
            // 文本消息
            LOGGER.info("开发者微信号：" + inputMsg.getToUserName());
            LOGGER.info("发送方帐号：" + inputMsg.getFromUserName());
            LOGGER.info("消息创建时间：" + inputMsg.getCreateTime() + new Date(createTime * 1000l));
            LOGGER.info("消息内容：" + inputMsg.getContent());
            LOGGER.info("消息Id：" + inputMsg.getMsgId());
            if (inputMsg.getContent().startsWith("添加地址")) {
                wechatCustomIdLogicImpl.createCustomInfo(inputMsg, response);
                return;
            }
            if (inputMsg.getContent().startsWith("修改地址")) {
                wechatCustomIdLogicImpl.updateCustomInfo(inputMsg, response);
                return;
            }
            if (inputMsg.getContent().startsWith("查询地址")) {
                wechatCustomIdLogicImpl.queryCustomInfo(inputMsg, response);
                return;
            }
            if (inputMsg.getContent().startsWith("创建账号")) {
                wechatUserLogicImpl.createUser(inputMsg, response);
                return;
            }
            if (inputMsg.getContent().startsWith("修改密码")) {
                wechatUserLogicImpl.updatePassword(inputMsg, response);
                return;
            }
            if (inputMsg.getContent().startsWith("发货")) {
                wechatInvoiceLogicImpl.createInvoice(inputMsg, response);
                return;
            }
            if (inputMsg.getContent().startsWith("查询")) {
                wechatInvoiceLogicImpl.queryInvoice(inputMsg, response);
                return;
            }

            if (inputMsg.getContent().startsWith("预约") || inputMsg.getContent().startsWith("取件")
                    || inputMsg.getContent().startsWith("快递")) {
                wechatHelpLogicImpl.doAppointment(inputMsg, response);
                return;
            }

            if (inputMsg.getContent().startsWith("帮助")) {
                wechatHelpLogicImpl.doHelp(inputMsg, response);
                return;
            }

            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您发的指令我们还在开发中。。。请耐心等待！");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 获取并返回多图片消息
        if (msgType.equals(MsgType.Image.toString())) {
            System.out.println("获取多媒体信息");
            System.out.println("多媒体文件id：" + inputMsg.getMediaId());
            System.out.println("图片链接：" + inputMsg.getPicUrl());
            System.out.println("消息id，64位整型：" + inputMsg.getMsgId());

            OutputMessage outputMsg = new OutputMessage();
            outputMsg.setFromUserName(serverName);
            outputMsg.setToUserName(custermName);
            outputMsg.setCreateTime(returnTime);
            outputMsg.setMsgType(msgType);
            ImageMessage images = new ImageMessage();
            images.setMediaId(inputMsg.getMediaId());
            outputMsg.setImage(images);
            System.out.println("xml转换：/n" + xs.toXML(outputMsg));
            response.getWriter().write(xs.toXML(outputMsg));

        }

    }

}
