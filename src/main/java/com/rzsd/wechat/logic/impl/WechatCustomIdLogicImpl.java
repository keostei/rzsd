package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

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
import com.rzsd.wechat.configuration.PropertiesListenerConfig;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.util.CheckUtil;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;
import com.rzsd.wechat.util.WechatMessageUtil;

@Component
public class WechatCustomIdLogicImpl implements WechatCustomIdLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatCustomIdLogicImpl.class.getName());

    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private MCustomInfoMapper mCustomInfoMapper;
    @Autowired
    private TInvoiceMapper tInvoiceMapper;

    private static final char[] CHAR_ARR = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    @Override
    public String generateId(boolean isProxy, char proxyCd) {
        StringBuilder sb = new StringBuilder();
        MUser selectCond = new MUser();
        while (true) {
            sb = new StringBuilder();
            if (isProxy) {
                sb.append(proxyCd);
            } else {
                sb.append('Z');
            }
            sb.append(CHAR_ARR[(new Random()).nextInt(CHAR_ARR.length)]);
            sb.append(CHAR_ARR[(new Random()).nextInt(CHAR_ARR.length)]);
            sb.append(CHAR_ARR[(new Random()).nextInt(CHAR_ARR.length)]);
            selectCond.setCustomId(sb.toString());
            if (mUserMapper.select(selectCond).isEmpty()) {
                break;
            }
        }
        return sb.toString();
    }

    @Override
    public void createCustomInfo(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        String[] cmdLst = inputMsg.getContent().split(" ");
        if (cmdLst.length != 4) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.format.error", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 姓名check
        if (!CheckUtil.isLengthValid(cmdLst[1], 10)) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.name.lenerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 电话号码check
        if (!CheckUtil.isValidString(cmdLst[2], CheckUtil.REGEX_NUM)) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.telno.numerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return;
        }

        if (!CheckUtil.isLengthValid(cmdLst[2], 13)) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.telno.lenerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return;
        }

        // 地址check
        if (!CheckUtil.isLengthValid(cmdLst[3], 64)) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.address.lenerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return;
        }

        // 获取用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        // 用户信息为查到时，先创建用户表
        MUser mUser = new MUser();
        if (userList.isEmpty()) {
            mUser.setWechatOpenId(inputMsg.getFromUserName());
            mUser.setCustomId(generateId(false, 'a'));
            mUser.setUserType("0");
            mUser.setCreateId(RzConst.SYS_ADMIN_ID);
            mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
            mUserMapper.insert(mUser);
        } else {
            mUser = userList.get(0);
        }
        MCustomInfo mCustomInfoCond = new MCustomInfo();
        mCustomInfoCond.setCustomId(mUser.getCustomId());
        mCustomInfoCond.setOrderByStr(" row_no DESC ");
        List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
        if (mCustomInfoLst.size() >= 3) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.address.sizeerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        MCustomInfo mCustomInfo = new MCustomInfo();
        mCustomInfo.setCustomId(mUser.getCustomId());
        if (mCustomInfoLst.isEmpty()) {
            mCustomInfo.setRowNo("1");
        } else {
            mCustomInfo.setRowNo(String.valueOf(Integer.valueOf((mCustomInfoLst.get(0).getRowNo())) + 1));
        }
        mCustomInfo.setName(cmdLst[1]);
        mCustomInfo.setTelNo(cmdLst[2]);
        mCustomInfo.setAddress(cmdLst[3]);
        mCustomInfo.setCreateId(mUser.getId());
        mCustomInfo.setUpdateId(mUser.getId());
        mCustomInfoMapper.insert(mCustomInfo);

        // 未设置地址发货申请地址不足
        if ("1".equals(mCustomInfo.getRowNo())) {
            TInvoice tInvoiceSelectCond = new TInvoice();
            tInvoiceSelectCond.setCreateId(mUser.getId());
            tInvoiceSelectCond.setDelFlg("0");
            List<TInvoice> tInvoiceLst = tInvoiceMapper.select(tInvoiceSelectCond);
            for (TInvoice tInvoice : tInvoiceLst) {
                if (tInvoice.getName() != null) {
                    continue;
                }
                tInvoice.setName(mCustomInfo.getName());
                tInvoice.setTelNo(mCustomInfo.getTelNo());
                tInvoice.setAddress(mCustomInfo.getAddress());
                tInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
                tInvoice.setUpdateId(mUser.getId());
                tInvoiceMapper.update(tInvoice);
            }
        }
        String msg = WechatMessageUtil.getTextMessage("text.customid.address.add.success", inputMsg.getFromUserName(),
                inputMsg.getToUserName(), mCustomInfo.getCustomId() + mCustomInfo.getRowNo(), mCustomInfo.getName(),
                mCustomInfo.getTelNo(), mCustomInfo.getAddress());
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        LOGGER.info(msg);
        return;
    }

    @Override
    public void updateCustomInfo(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {

        String[] cmdLst = inputMsg.getContent().split(" ");
        if (cmdLst.length != 5) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.modify.format.error",
                    inputMsg.getFromUserName(), inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 姓名check
        if (!CheckUtil.isLengthValid(cmdLst[2], 10)) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.name.lenerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 电话号码check
        if (!CheckUtil.isValidString(cmdLst[3], CheckUtil.REGEX_NUM)) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.telno.numerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return;
        }

        if (!CheckUtil.isLengthValid(cmdLst[3], 13)) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.telno.lenerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return;
        }

        // 地址check
        if (!CheckUtil.isLengthValid(cmdLst[4], 64)) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.address.lenerr", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return;
        }
        // 获取用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        // 用户信息为查到时，先创建用户表
        MUser mUser = new MUser();
        if (userList.isEmpty()) {
            mUser.setWechatOpenId(inputMsg.getFromUserName());
            mUser.setCustomId(generateId(false, 'a'));
            mUser.setUserType("0");
            mUser.setCreateId(RzConst.SYS_ADMIN_ID);
            mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
            mUserMapper.insert(mUser);
        } else {
            mUser = userList.get(0);
        }

        if (!mUser.getCustomId().equalsIgnoreCase(cmdLst[1].substring(0, 4))) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.modify.customid.notexist",
                    inputMsg.getFromUserName(), inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        MCustomInfo mCustomInfoCond = new MCustomInfo();
        mCustomInfoCond.setCustomId(mUser.getCustomId());
        if (cmdLst[1].length() > 4) {
            mCustomInfoCond.setRowNo(cmdLst[1].substring(4));
        } else {
            mCustomInfoCond.setRowNo("1");
        }
        mCustomInfoCond.setOrderByStr(" row_no DESC ");
        List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
        if (mCustomInfoLst.isEmpty()) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.modify.customid.notexist",
                    inputMsg.getFromUserName(), inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        MCustomInfo mCustomInfo = mCustomInfoLst.get(0);
        mCustomInfo.setName(cmdLst[2]);
        mCustomInfo.setTelNo(cmdLst[3]);
        mCustomInfo.setAddress(cmdLst[4]);
        mCustomInfo.setUpdateId(userList.get(0).getId());
        mCustomInfo.setUpdateTime(DateUtil.getCurrentTimestamp());
        mCustomInfoMapper.update(mCustomInfo);

        String msg = WechatMessageUtil.getTextMessage("text.customid.address.modify.success",
                inputMsg.getFromUserName(), inputMsg.getToUserName(),
                mCustomInfo.getCustomId() + mCustomInfo.getRowNo(), mCustomInfo.getName(), mCustomInfo.getTelNo(),
                mCustomInfo.getAddress());
        LOGGER.debug(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }

    @Override
    public void queryCustomInfo(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {

        // 获取用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        if (userList.isEmpty()) {
            MUser mUser = new MUser();
            mUser.setWechatOpenId(inputMsg.getFromUserName());
            mUser.setCustomId(generateId(false, 'a'));
            mUser.setUserType("0");
            mUser.setCreateId(RzConst.SYS_ADMIN_ID);
            mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
            mUserMapper.insert(mUser);
            String msg = WechatMessageUtil.getTextMessage("text.customid.address.query.notexist",
                    inputMsg.getFromUserName(), inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        MCustomInfo mCustomInfoCond = new MCustomInfo();
        mCustomInfoCond.setCustomId(userList.get(0).getCustomId());
        mCustomInfoCond.setOrderByStr(" row_no ASC ");
        List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
        if (mCustomInfoLst.isEmpty()) {
            String msg = WechatMessageUtil.getTextMessage("text.customid.address.query.notexist",
                    inputMsg.getFromUserName(), inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        String customInfoMsg = PropertiesListenerConfig.getProperty("text.customid.address.query.custominfo");
        for (MCustomInfo info : mCustomInfoLst) {
            sb.append(MessageFormat.format(customInfoMsg, info.getCustomId() + info.getRowNo(), info.getName(),
                    info.getTelNo(), info.getAddress()));
        }

        String msg = WechatMessageUtil.getTextMessage("text.customid.address.query.result", inputMsg.getFromUserName(),
                inputMsg.getToUserName(), sb.toString());
        LOGGER.debug(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }
}
