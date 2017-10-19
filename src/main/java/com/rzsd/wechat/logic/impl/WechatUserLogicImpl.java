package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.logic.WechatUserLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;
import com.rzsd.wechat.util.MD5Util;

@Component
public class WechatUserLogicImpl implements WechatUserLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatUserLogicImpl.class.getName());

    @Autowired
    private MUserMapper mUserMapper;

    @Override
    public void createUser(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;
        String[] cmdLst = inputMsg.getContent().split(" ");
        if (cmdLst.length != 4) {
            // TODO:长度不对
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您的指令格式有误，请按照下面格式发送指令！\n创建账号 zhangsan 张三 passW0rd");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 获取用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        if (userList.isEmpty()) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您的微信信息暂未在本平台登录，请取消关注后重新关注。");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        MUser mUser = userList.get(0);
        if (!StringUtils.isEmpty(userList.get(0).getUserName())) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", MessageFormat.format(
                            "您已经创建过账号，可以用{0}和密码直接登录。如果需要重置密码，请回复指令【重置密码 新密码】重置！", userList.get(0).getUserName()));
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        selectCond = new MUser();
        selectCond.setUserName(cmdLst[1]);
        if (!mUserMapper.select(selectCond).isEmpty()) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您输入的登陆账号已经被注册，请选用其他账号进行注册。");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        mUser.setUserName(cmdLst[1]);
        mUser.setNickName(cmdLst[2]);
        mUser.setPassword(MD5Util.toMd5(cmdLst[3]));
        mUser.setUpdateId(mUser.getId());
        mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
        mUserMapper.update(mUser);

        String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(), inputMsg.getToUserName(),
                returnTime, "text", MessageFormat.format("账号创建成功，您可以使用{0}及密码登陆网页系统查看更丰富的服务信息。", mUser.getUserName()));
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }

    @Override
    public void updatePassword(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;
        String[] cmdLst = inputMsg.getContent().split(" ");
        if (cmdLst.length != 2) {
            // TODO:长度不对
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您的指令格式有误，请按照下面格式发送指令！\n修改密码 passW0rd");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 获取用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        if (userList.isEmpty()) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您的微信信息暂未在本平台登录，请取消关注后重新关注。");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        MUser mUser = userList.get(0);
        if (StringUtils.isEmpty(userList.get(0).getUserName())) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您尚未创建过账号，请回复指令【创建账号 zhangsan 张三 passW0rd】创建账号！");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        mUser.setPassword(MD5Util.toMd5(cmdLst[1]));
        mUser.setUpdateId(mUser.getId());
        mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
        mUserMapper.update(mUser);

        String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(), inputMsg.getToUserName(),
                returnTime, "text", MessageFormat.format("密码更新成功，您可以使用{0}及新密码登陆网页系统体验更丰富的服务信息。", mUser.getUserName()));
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }

    // private static String createMd5String(String str) {
    // //确定计算方法
    // MessageDigest md5=MessageDigest.getInstance("MD5");
    // BASE64Encoder base64en = new BASE64Encoder();
    // //加密后的字符串
    // String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
    // return newstr;
    // }

}
