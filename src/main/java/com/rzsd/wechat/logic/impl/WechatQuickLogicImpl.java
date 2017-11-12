package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.MCustomInfo;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MCustomInfoMapper;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.configuration.PropertiesListenerConfig;
import com.rzsd.wechat.context.ChatContextDto;
import com.rzsd.wechat.context.ChatContextInstance;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatInvoiceLogic;
import com.rzsd.wechat.logic.WechatQuickLogic;
import com.rzsd.wechat.util.CheckUtil;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;
import com.rzsd.wechat.util.WechatMessageUtil;

@Component
public class WechatQuickLogicImpl implements WechatQuickLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatQuickLogicImpl.class.getName());
    private static final String TYPE_ADD_ADDRESS = "1";
    private static final String TYPE_APPLY = "2";
    private static final String TYPE_QUERY = "3";
    private static final String TYPE_QUERY_ADDRESS = "4";
    private static final String TYPE_EDIT_ADDRESS = "5";

    @Autowired
    private WechatCustomIdLogic wechatCustomIdLogicImpl;
    @Autowired
    private WechatInvoiceLogic wechatInvoiceLogicImpl;

    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private MCustomInfoMapper mCustomInfoMapper;

    @Override
    public boolean execute(InputMessage inputMsg, HttpServletResponse response) throws IOException {
        String openId = inputMsg.getFromUserName();

        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;
        if (TYPE_ADD_ADDRESS.equals(inputMsg.getContent())) {
            ChatContextInstance.newInstance(openId);
            ChatContextInstance.setType(openId, inputMsg.getContent());
            String msg = WechatMessageUtil.getTextMessage("text.quick.hint.name", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return true;
        }
        if (TYPE_APPLY.equals(inputMsg.getContent())) {
            ChatContextInstance.newInstance(openId);
            ChatContextInstance.setType(openId, inputMsg.getContent());

            // 获取用户信息
            MUser selectCond = new MUser();
            selectCond.setWechatOpenId(inputMsg.getFromUserName());
            List<MUser> userList = mUserMapper.select(selectCond);
            if (userList.isEmpty()) {
                MUser mUser = new MUser();
                mUser.setWechatOpenId(openId);
                mUser.setCustomId(wechatCustomIdLogicImpl.generateId(false, 'a'));
                mUser.setUserType("0");
                mUser.setCreateId(RzConst.SYS_ADMIN_ID);
                mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
                mUserMapper.insert(mUser);
                String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), returnTime, "text", "您还没有设置收件信息，请输入收件人姓名：（例如：张三）");
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                ChatContextInstance.newInstance(openId);
                ChatContextInstance.setType(openId, TYPE_ADD_ADDRESS);
                return true;
            }

            MCustomInfo mCustomInfoCond = new MCustomInfo();
            mCustomInfoCond.setCustomId(userList.get(0).getCustomId());
            mCustomInfoCond.setOrderByStr(" row_no ASC ");
            List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
            // 用户尚未设置地址
            if (mCustomInfoLst.isEmpty()) {
                inputMsg.setContent("发货");
                wechatInvoiceLogicImpl.createInvoice(inputMsg, response);
                return true;
            }

            // 用户已经设置地址，并且只有一个地址
            if (mCustomInfoLst.size() == 1) {
                inputMsg.setContent("发货");
                wechatInvoiceLogicImpl.createInvoice(inputMsg, response);
                return true;
            }

            // 用户有多个地址时，让用户选择地址
            StringBuilder sb = new StringBuilder();
            String customInfoMsg = PropertiesListenerConfig.getProperty("text.quick.hint.custominfo");
            for (MCustomInfo info : mCustomInfoLst) {
                sb.append(MessageFormat.format(customInfoMsg,
                        (info.getRowNo().equals("1") ? "默认-" : "") + info.getCustomId() + info.getRowNo(),
                        info.getName(), info.getTelNo(), info.getAddress()));
            }
            String msg = WechatMessageUtil.getTextMessage("text.quick.hint.customcd", inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), sb.toString());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return true;
        }
        if (TYPE_QUERY.equals(inputMsg.getContent())) {
            ChatContextInstance.newInstance(openId);
            ChatContextInstance.setType(openId, inputMsg.getContent());
            String cmd = "查询";
            inputMsg.setContent(cmd);
            wechatInvoiceLogicImpl.queryInvoice(inputMsg, response);
            ChatContextInstance.removeInstance(openId);
            return true;
        }
        if (TYPE_QUERY_ADDRESS.equals(inputMsg.getContent())) {
            ChatContextInstance.newInstance(openId);
            ChatContextInstance.setType(openId, inputMsg.getContent());
            String cmd = "查询地址";
            inputMsg.setContent(cmd);
            wechatCustomIdLogicImpl.queryCustomInfo(inputMsg, response);
            ChatContextInstance.removeInstance(openId);
            return true;
        }
        if (TYPE_EDIT_ADDRESS.equals(inputMsg.getContent())) {
            ChatContextInstance.newInstance(openId);
            ChatContextInstance.setType(openId, inputMsg.getContent());

            // 获取用户信息
            MUser selectCond = new MUser();
            selectCond.setWechatOpenId(inputMsg.getFromUserName());
            List<MUser> userList = mUserMapper.select(selectCond);
            if (userList.isEmpty()) {
                MUser mUser = new MUser();
                mUser.setWechatOpenId(openId);
                mUser.setCustomId(wechatCustomIdLogicImpl.generateId(false, 'a'));
                mUser.setUserType("0");
                mUser.setCreateId(RzConst.SYS_ADMIN_ID);
                mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
                mUserMapper.insert(mUser);
                String msg = WechatMessageUtil.getTextMessage("text.quick.hint.custominfo.notexists",
                        inputMsg.getFromUserName(), inputMsg.getToUserName());
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                ChatContextInstance.newInstance(openId);
                ChatContextInstance.setType(openId, TYPE_ADD_ADDRESS);
                return true;
            }

            MCustomInfo mCustomInfoCond = new MCustomInfo();
            mCustomInfoCond.setCustomId(userList.get(0).getCustomId());
            mCustomInfoCond.setOrderByStr(" row_no ASC ");
            List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
            if (mCustomInfoLst.isEmpty()) {
                String msg = WechatMessageUtil.getTextMessage("text.quick.hint.custominfo.notexists",
                        inputMsg.getFromUserName(), inputMsg.getToUserName());
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                ChatContextInstance.newInstance(openId);
                ChatContextInstance.setType(openId, TYPE_ADD_ADDRESS);
                return true;
            }
            if (mCustomInfoLst.size() == 1) {
                ChatContextInstance.setWord1(openId,
                        mCustomInfoLst.get(0).getCustomId() + mCustomInfoLst.get(0).getRowNo());
                String msg = WechatMessageUtil.getTextMessage("text.quick.hint.custominfo.edit.newname",
                        inputMsg.getFromUserName(), inputMsg.getToUserName());
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                return true;
            }
            // 用户有多个地址时，让用户选择地址
            StringBuilder sb = new StringBuilder();
            String customInfoMsg = PropertiesListenerConfig.getProperty("text.quick.hint.custominfo");
            for (MCustomInfo info : mCustomInfoLst) {
                sb.append(MessageFormat.format(customInfoMsg,
                        (info.getRowNo().equals("1") ? "默认-" : "") + info.getCustomId() + info.getRowNo(),
                        info.getName(), info.getTelNo(), info.getAddress()));
            }
            String msg = WechatMessageUtil.getTextMessage("text.quick.hint.custominfo.edit.selectcd",
                    inputMsg.getFromUserName(), inputMsg.getToUserName(), sb.toString());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return true;
        }

        ChatContextDto chatContextDto = ChatContextInstance.getInstance(openId);
        if (chatContextDto == null) {
            return false;
        }

        if (StringUtils.isEmpty(chatContextDto.getType())) {
            String msg = WechatMessageUtil.getTextMessage("text.quick.hint.error", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return true;
        }

        if (TYPE_ADD_ADDRESS.equals(chatContextDto.getType())) {
            if (StringUtils.isEmpty(chatContextDto.getWord1())) {
                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 10)) {
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.name.lenerr",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    return true;
                }
                ChatContextInstance.setWord1(openId, inputMsg.getContent());
                String msg = WechatMessageUtil.getTextMessage("text.quick.hint.telno", inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), inputMsg.getContent());
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                LOGGER.debug(msg);
                return true;
            }
            if (StringUtils.isEmpty(chatContextDto.getWord2())) {
                if (!CheckUtil.isValidString(inputMsg.getContent(), CheckUtil.REGEX_NUM)) {
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.telno.numerr",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    LOGGER.debug(msg);
                    return true;
                }

                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 13)) {
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.telno.lenerr",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    LOGGER.debug(msg);
                    return true;
                }
                ChatContextInstance.setWord2(openId, inputMsg.getContent());
                String msg = WechatMessageUtil.getTextMessage("text.quick.hint.address", inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), chatContextDto.getWord1());
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                LOGGER.debug(msg);
                return true;
            }
            if (StringUtils.isEmpty(chatContextDto.getWord3())) {
                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 64)) {
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.address.lenerr",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    LOGGER.debug(msg);
                    return true;
                }
                ChatContextInstance.setWord3(openId, inputMsg.getContent());
                String cmd = "添加地址 " + chatContextDto.getWord1().replace(" ", "") + " "
                        + chatContextDto.getWord2().replace(" ", "") + " " + chatContextDto.getWord3().replace(" ", "");
                inputMsg.setContent(cmd);
                wechatCustomIdLogicImpl.createCustomInfo(inputMsg, response);
                ChatContextInstance.removeInstance(openId);
                return true;
            }

            ChatContextInstance.newInstance(openId);
            String msg = WechatMessageUtil.getTextMessage("text.quick.hint.error", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return true;

        }
        if (TYPE_APPLY.equals(chatContextDto.getType())) {
            String cmd = null;
            if ("666".equals(inputMsg.getContent())) {
                cmd = "发货";
            } else if (inputMsg.getContent().startsWith("666")) {
                cmd = "发货" + inputMsg.getContent().substring(3);
            } else {
                cmd = "发货 " + inputMsg.getContent();
            }
            inputMsg.setContent(cmd);
            wechatInvoiceLogicImpl.createInvoice(inputMsg, response);
            ChatContextInstance.removeInstance(openId);
        } else {
            return false;
        }

        if (TYPE_EDIT_ADDRESS.equals(chatContextDto.getType())) {
            //
            if (StringUtils.isEmpty(chatContextDto.getWord1())) {
                if (inputMsg.getContent().length() == 5
                        && CheckUtil.isValidString(inputMsg.getContent(), CheckUtil.REGEX_ALPHABETA)) {
                    MUser selectCond = new MUser();
                    selectCond.setWechatOpenId(openId);
                    List<MUser> userList = mUserMapper.select(selectCond);

                    if (userList.isEmpty()
                            || !userList.get(0).getCustomId().equalsIgnoreCase(inputMsg.getContent().substring(0, 4))) {
                        // 编码不正确
                        String msg = WechatMessageUtil.getTextMessage("text.quick.hint.custominfo.edit.selectcd.error",
                                inputMsg.getFromUserName(), inputMsg.getToUserName());
                        response.getOutputStream().write(msg.getBytes("UTF-8"));
                        return true;
                    }

                    MCustomInfo mCustomInfoCond = new MCustomInfo();
                    mCustomInfoCond.setCustomId(userList.get(0).getCustomId());
                    mCustomInfoCond.setRowNo(inputMsg.getContent().substring(4).toUpperCase());
                    List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
                    if (mCustomInfoLst.isEmpty()) {
                        // 编码不正确
                        String msg = WechatMessageUtil.getTextMessage("text.quick.hint.custominfo.edit.selectcd.error",
                                inputMsg.getFromUserName(), inputMsg.getToUserName());
                        response.getOutputStream().write(msg.getBytes("UTF-8"));
                        return true;
                    }

                    ChatContextInstance.setWord1(openId,
                            mCustomInfoLst.get(0).getCustomId() + mCustomInfoLst.get(0).getRowNo());
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.custominfo.edit.newname",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    return true;
                }

                // 客户编码ID不正确
                String msg = WechatMessageUtil.getTextMessage("text.quick.hint.custominfo.edit.selectcd.error",
                        inputMsg.getFromUserName(), inputMsg.getToUserName());
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                return true;

            }
            if (StringUtils.isEmpty(chatContextDto.getWord2())) {
                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 10)) {
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.name.lenerr",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    return true;
                }
                ChatContextInstance.setWord2(openId, inputMsg.getContent());
                String msg = WechatMessageUtil.getTextMessage("text.quick.hint.telno", inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), inputMsg.getContent());
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                LOGGER.debug(msg);
                return true;
            }
            if (StringUtils.isEmpty(chatContextDto.getWord3())) {
                if (!CheckUtil.isValidString(inputMsg.getContent(), CheckUtil.REGEX_NUM)) {
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.telno.numerr",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    LOGGER.debug(msg);
                    return true;
                }

                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 13)) {
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.telno.lenerr",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    LOGGER.debug(msg);
                    return true;
                }
                ChatContextInstance.setWord3(openId, inputMsg.getContent());
                String msg = WechatMessageUtil.getTextMessage("text.quick.hint.address", inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), chatContextDto.getWord1());
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                LOGGER.debug(msg);
                return true;
            }
            if (StringUtils.isEmpty(chatContextDto.getWord4())) {
                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 64)) {
                    String msg = WechatMessageUtil.getTextMessage("text.quick.hint.address.lenerr",
                            inputMsg.getFromUserName(), inputMsg.getToUserName());
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    LOGGER.debug(msg);
                    return true;
                }
                ChatContextInstance.setWord4(openId, inputMsg.getContent());
                String cmd = "修改地址 " + chatContextDto.getWord1().replace(" ", "") + " "
                        + chatContextDto.getWord2().replace(" ", "") + " " + chatContextDto.getWord3().replace(" ", "")
                        + " " + chatContextDto.getWord4().replace(" ", "");
                inputMsg.setContent(cmd);
                wechatCustomIdLogicImpl.updateCustomInfo(inputMsg, response);
                ChatContextInstance.removeInstance(openId);
                return true;
            }

            ChatContextInstance.newInstance(openId);
            String msg = WechatMessageUtil.getTextMessage("text.quick.hint.error", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            LOGGER.debug(msg);
            return true;

        }

        return true;
    }

}
