package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.context.ChatContextInstance;
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
                "<Content><![CDATA[欢迎关注XXXX；您的客户编码是{0}，请牢记！\n现在您可以回复下面数字快速操作。\n1.添加地址\n2.发货\n3.查询发货记录\n您也可以回复\"更多\"查看更多指令。]]></Content>");
        str.append("</xml>");
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
                msg = MessageFormat.format(str.toString(), mUser.getCustomId());
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
                msg = MessageFormat.format(str.toString(), mUser.getCustomId());
                LOGGER.info("取消关注用户重新关注：" + mUser.getCustomId());
            }
            ChatContextInstance.newInstance(inputMsg.getFromUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("post exception.");
            throw e;
        }
    }

}
