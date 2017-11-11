package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.context.ChatContextInstance;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatSubscribeLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;
import com.rzsd.wechat.util.WechatMessageUtil;

@Component
public class WechatSubscribeLogicImpl implements WechatSubscribeLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatSubscribeLogicImpl.class.getName());

    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private WechatCustomIdLogic wechatCustomIdLogicLogic;

    @Override
    @Transactional
    public void execute(InputMessage inputMsg, HttpServletResponse response) throws IOException {
        String msg = null;
        try {
            MUser mUser = new MUser();
            // 先跟根据OpenId检索该用户之前是否关注过，如果关注过恢复用户删除flg
            mUser.setWechatOpenId(inputMsg.getFromUserName());
            List<MUser> mUserOldLst = mUserMapper.select(mUser);
            if (mUserOldLst.isEmpty()) {
                mUser.setCustomId(wechatCustomIdLogicLogic.generateId(false, 'a'));
                mUser.setUserType("0");
                mUser.setCreateId(RzConst.SYS_ADMIN_ID);
                mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
                mUserMapper.insert(mUser);
                msg = WechatMessageUtil.getTextMessage("text.subscribe.new", inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), mUser.getCustomId());
                LOGGER.info("创建新用户：" + mUser.getCustomId());
            } else {
                mUser = mUserOldLst.get(0);
                // mUser.setUserName(null);
                // mUser.setPassword(null);
                // mUser.setNickName(null);
                mUser.setCustomId(wechatCustomIdLogicLogic.generateId(false, 'a'));
                // mUser.setUserType("0");
                mUser.setDelFlg("0");
                mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
                mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
                mUserMapper.update(mUser);
                msg = WechatMessageUtil.getTextMessage("text.subscribe.resubscribe", inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), mUser.getCustomId());
                LOGGER.info("取消关注用户重新关注：" + mUser.getCustomId());
            }
            ChatContextInstance.newInstance(inputMsg.getFromUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
        } catch (IOException e) {
            LOGGER.error("用户关注公众号发生异常。", e);
            throw e;
        }
    }

}
