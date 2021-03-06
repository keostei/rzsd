package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.logic.WechatUnSubscribeLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;

@Component
public class WechatUnSubscribeLogicImpl implements WechatUnSubscribeLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatUnSubscribeLogicImpl.class.getName());

    @Autowired
    private MUserMapper mUserMapper;

    @Override
    @Transactional
    public void execute(InputMessage inputMsg, HttpServletResponse response) throws IOException {
        try {
            MUser mUser = new MUser();
            mUser.setWechatOpenId(inputMsg.getFromUserName());
            List<MUser> mUserOldLst = mUserMapper.select(mUser);
            if (mUserOldLst.isEmpty()) {
                LOGGER.warn("未在数据库中找到该用户信息,微信openId：" + inputMsg.getFromUserName());
            } else {
                mUser = mUserOldLst.get(0);
                mUser.setUserName("");
                mUser.setPassword("");
                mUser.setNickName("");
                mUser.setUserType("0");
                mUser.setDelFlg("1");
                mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
                mUser.setUpdateId(mUser.getId());
                mUserMapper.update(mUser);
                LOGGER.info("用户取消关注：" + mUser.getCustomId());
            }
            response.getOutputStream().write("success".toString().getBytes("UTF-8"));
        } catch (IOException e) {
            LOGGER.error("用户取消关注公众号发生异常。", e);
            throw e;
        }
    }

}
