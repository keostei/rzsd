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

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.MCustomInfo;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.dto.TInvoice;
import com.rzsd.wechat.common.mapper.MCustomInfoMapper;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.common.mapper.TInvoiceMapper;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatInvoiceLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;

@Component
public class WechatInvoiceLogicImpl implements WechatInvoiceLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatInvoiceLogicImpl.class.getName());

    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private MCustomInfoMapper mCustomInfoMapper;
    @Autowired
    private TInvoiceMapper tInvoiceMapper;
    @Autowired
    private WechatCustomIdLogic wechatCustomIdLogicImpl;

    @Override
    public void createInvoice(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;
        String[] cmdLst = inputMsg.getContent().split(" ");
        if (cmdLst.length != 3 && cmdLst.length != 2) {
            // TODO:长度不对
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您的指令格式有误，请按照下面格式发送指令！\n发货 AJKXS1");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        String customCd = null;
        MUser mUser = null;
        // 通过代理发货的时候，变更客户代码
        if (cmdLst.length == 3) {
            // 检索用户信息
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
            mUser = userList.get(0);
            if (!mUser.getCustomId().substring(2).equalsIgnoreCase(cmdLst[1].substring(2, 5))) {
                String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), returnTime, "text", "您输入的地址编码有误，请重新输入。您可以通过【查询地址】指令查询地址编码。");
                LOGGER.info(msg);
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                return;
            }

            String oldCustomId = mUser.getCustomId();
            String customId = wechatCustomIdLogicImpl.generateId(true, cmdLst[2].charAt(0));
            mUser.setCustomId(customId);
            mUser.setUpdateId(mUser.getId());
            mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
            mUserMapper.update(mUser);

            MCustomInfo mCustomInfo = new MCustomInfo();
            mCustomInfo.setCustomId(customId);
            mCustomInfo.setOldCustomId(oldCustomId);
            mCustomInfo.setUpdateId(mUser.getId());
            mCustomInfo.setUpdateTime(DateUtil.getCurrentTimestamp());
            mCustomInfoMapper.updateCustomId(mCustomInfo);
            if (cmdLst[1].length() > 5) {
                customCd = customId + cmdLst[1].substring(5, 6);
            } else {
                customCd = customId + "1";
            }
            // TODO:流水记录表的客户编码也需要批量更新
        } else {
            // 检索用户信息
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
            mUser = userList.get(0);
            if (!mUser.getCustomId().equalsIgnoreCase(cmdLst[1].substring(0, 5))) {
                String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), returnTime, "text", "您输入的地址编码有误，请重新输入。您可以通过【查询地址】指令查询地址编码。");
                LOGGER.info(msg);
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                return;
            }
            if (cmdLst[1].length() > 5) {
                customCd = cmdLst[1];
            } else {
                customCd = cmdLst[1] + "1";
            }
        }
        MCustomInfo mCustomInfoCond = new MCustomInfo();
        mCustomInfoCond.setCustomId(customCd.substring(0, 5));
        mCustomInfoCond.setRowNo(customCd.substring(5, 6));
        mCustomInfoCond.setOrderByStr(" row_no DESC ");
        List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
        if (mCustomInfoLst.isEmpty()) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您输入的地址编码有误，请重新输入。您可以通过【查询地址】指令查询地址编码。");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        TInvoice tInvoice = new TInvoice();
        tInvoice.setInvoiceDate(DateUtil.getCurrentTimestamp());
        tInvoice.setCustomCd(customCd);
        tInvoice.setName(mCustomInfoLst.get(0).getName());
        tInvoice.setTelNo(mCustomInfoLst.get(0).getTelNo());
        tInvoice.setAddress(mCustomInfoLst.get(0).getAddress());
        tInvoice.setInvoiceStatus("1");
        tInvoice.setCreateId(mUser.getId());
        tInvoice.setUpdateId(mUser.getId());
        tInvoiceMapper.insert(tInvoice);

        String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(), inputMsg.getToUserName(),
                returnTime, "text",
                MessageFormat.format("发货申请成功！\n日期：{0}\n客户编号：{1}\n收件人：{2}\n电话：{3}\n地址：{4}", tInvoice.getInvoiceDate(),
                        tInvoice.getCustomCd(), tInvoice.getName(), tInvoice.getTelNo(), tInvoice.getAddress()));
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }

}
