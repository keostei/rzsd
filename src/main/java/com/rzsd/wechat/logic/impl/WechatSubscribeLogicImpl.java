package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatSubscribeLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;

@Component
public class WechatSubscribeLogicImpl implements WechatSubscribeLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatSubscribeLogicImpl.class.getName());

    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private WechatCustomIdLogic wechatCustomIdLogicLogic;

    @Override
    public void execute(InputMessage inputMsg, HttpServletResponse response) throws IOException {
        Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;
        StringBuffer str = new StringBuffer();
        str.append("<xml>");
        str.append("<ToUserName><![CDATA[" + inputMsg.getFromUserName() + "]]></ToUserName>");
        str.append("<FromUserName><![CDATA[" + inputMsg.getToUserName() + "]]></FromUserName>");
        str.append("<CreateTime>" + returnTime + "</CreateTime>");
        str.append("<MsgType><![CDATA[text]]></MsgType>");
        str.append(
                "<Content><![CDATA[那一年，风华正茂；\n那一年，我们一起端起相机记录青春！\n<a href='http://122.112.229.36'>KeosImage团队</a>，欢迎您回来！]]></Content>");
        str.append("</xml>");
        LOGGER.info(str.toString());
        try {
            MUser mUser = new MUser();
            // 先跟根据OpenId检索该用户之前是否关注过，如果关注过则清空客户代码
            mUser.setWechatOpenId(inputMsg.getFromUserName());
            List<MUser> mUserOldLst = mUserMapper.select(mUser);
            if (mUserOldLst.isEmpty()) {
                mUser.setCustomId(wechatCustomIdLogicLogic.generateId(false, 'a'));
                mUser.setUserType("0");
                mUser.setCreateId(new BigInteger("1"));
                mUser.setUpdateId(new BigInteger("1"));
                mUserMapper.insert(mUser);
                LOGGER.info("创建新用户：" + mUser.getCustomId());
            } else {
                mUser = mUserOldLst.get(0);
                mUser.setUserName(null);
                mUser.setPassword(null);
                mUser.setNickName(null);
                mUser.setCustomId(wechatCustomIdLogicLogic.generateId(false, 'a'));
                mUser.setUserType("0");
                mUser.setDelFlg("0");
                mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
                mUser.setUpdateId(new BigInteger("1"));
                mUserMapper.update(mUser);
                LOGGER.info("取消关注用户重新关注：" + mUser.getCustomId());
            }
            response.getOutputStream().write(str.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.info("post exception.");
            throw e;
        }
    }

}
