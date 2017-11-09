package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.MCustomInfo;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MCustomInfoMapper;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.context.ChatContextDto;
import com.rzsd.wechat.context.ChatContextInstance;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatInvoiceLogic;
import com.rzsd.wechat.logic.WechatQuickLogic;
import com.rzsd.wechat.util.CheckUtil;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;

@Component
public class WechatQuickLogicImpl implements WechatQuickLogic {

    private static final String TYPE_ADD_ADDRESS = "1";
    private static final String TYPE_APPLY = "2";
    private static final String TYPE_QUERY = "3";
    private static final String TYPE_QUERY_ADDRESS = "4";

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
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "请输入收件人姓名：（例如：张三）");
            response.getOutputStream().write(msg.getBytes("UTF-8"));
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
                        inputMsg.getToUserName(), returnTime, "text", "您还没有设置收件信息，请输入收件人姓名：（例如：张三）！");
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
                String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), returnTime, "text", "您还没有设置收件信息，请输入收件人姓名：（例如：张三）！");
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                ChatContextInstance.newInstance(openId);
                ChatContextInstance.setType(openId, TYPE_ADD_ADDRESS);
                return true;
            }

            StringBuilder sb = new StringBuilder();
            String customInfoMsg = "【{0}】\n姓名：{1}\n电话号码：{2}\n收件地址：{3}\n";
            for (MCustomInfo info : mCustomInfoLst) {
                sb.append(MessageFormat.format(customInfoMsg,
                        info.getRowNo().equals("1") ? "【默认】" : "" + info.getCustomId() + info.getRowNo(),
                        info.getName(), info.getTelNo(), info.getAddress()));
            }

            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "请回复发货地址编号，默认地址发货直接回复666。");
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

        ChatContextDto chatContextDto = ChatContextInstance.getInstance(openId);
        if (chatContextDto == null) {
            return false;
        }

        if (StringUtils.isEmpty(chatContextDto.getType())) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text",
                    "您输入的指令有误，请重新输入：\n1.添加地址\n2.发货\n3.查询发货记录\n4.查询已设置收件地址\n您也可以回复\"更多\"查看更多指令。");
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return true;
        }

        if (TYPE_ADD_ADDRESS.equals(inputMsg.getContent())) {
            if (StringUtils.isEmpty(chatContextDto.getWord1())) {
                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 10)) {
                    String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                            inputMsg.getToUserName(), returnTime, "text", "您的姓名太长了吧？请重新输入哦！");
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    return true;
                }
                ChatContextInstance.setWord1(openId, inputMsg.getContent());
                String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), returnTime, "text",
                        "请输入" + inputMsg.getContent() + "的电话号码：（例如：139142345678");
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                return true;
            }
            if (StringUtils.isEmpty(chatContextDto.getWord2())) {
                if (!CheckUtil.isValidString(inputMsg.getContent(), CheckUtil.REGEX_NUM)) {
                    String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                            inputMsg.getToUserName(), returnTime, "text", "听说您的电话号码不是数字？别逗我啦，再给你一次重新输入的机会！");
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    return true;
                }

                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 13)) {
                    String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                            inputMsg.getToUserName(), returnTime, "text", "您的电话号码太长了吧？请重新输入哦！");
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    return true;
                }
                ChatContextInstance.setWord2(openId, inputMsg.getContent());
                String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                        inputMsg.getToUserName(), returnTime, "text",
                        "请输入" + inputMsg.getContent() + "的地址，注意不要包含空格：（例如：江苏省南京市建邺区奥体大街00号新城家园1栋502");
                response.getOutputStream().write(msg.getBytes("UTF-8"));
                return true;
            }
            if (StringUtils.isEmpty(chatContextDto.getWord3())) {
                if (!CheckUtil.isLengthValid(inputMsg.getContent(), 64)) {
                    String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                            inputMsg.getToUserName(), returnTime, "text", "您的地址太长了？记不住哦，请重新输入！");
                    response.getOutputStream().write(msg.getBytes("UTF-8"));
                    return true;
                }

                String cmd = "添加地址 " + chatContextDto.getWord1().replace(" ", "") + " "
                        + chatContextDto.getWord2().replace(" ", "") + " " + chatContextDto.getWord3().replace(" ", "");
                inputMsg.setContent(cmd);
                wechatCustomIdLogicImpl.createCustomInfo(inputMsg, response);
                ChatContextInstance.removeInstance(openId);
                return true;
            }

            ChatContextInstance.newInstance(openId);
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text",
                    "公众号也懵了，请重新输入：\n1.添加地址\n2.发货\n3.查询发货记录\n4.查询已设置收件地址\n您也可以回复\"更多\"查看更多指令。");
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return true;

        }
        if (TYPE_APPLY.equals(inputMsg.getContent())) {
            String cmd = null;
            if ("666".equals(inputMsg.getContent())) {
                cmd = "发货";
            } else {
                cmd = "发货 " + inputMsg.getContent();
            }
            inputMsg.setContent(cmd);
            wechatInvoiceLogicImpl.createInvoice(inputMsg, response);
            ChatContextInstance.removeInstance(openId);
        } else {
            return false;
        }

        return true;
    }

}
