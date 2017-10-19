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
import com.rzsd.wechat.common.mapper.MCustomInfoMapper;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;

@Component
public class WechatCustomIdLogicImpl implements WechatCustomIdLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatCustomIdLogicImpl.class.getName());

    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private MCustomInfoMapper mCustomInfoMapper;

    private static final char[] CHAR_ARR = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    @Override
    public String generateId(boolean isProxy, char proxyCd) {
        StringBuilder sb = new StringBuilder();
        MUser selectCond = new MUser();
        while (true) {
            sb = new StringBuilder();
            if (isProxy) {
                sb.append('Z');
                sb.append(proxyCd);
            } else {
                sb.append(CHAR_ARR[(new Random()).nextInt(CHAR_ARR.length - 1)]);
                sb.append(CHAR_ARR[(new Random()).nextInt(CHAR_ARR.length)]);
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
        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;
        String[] cmdLst = inputMsg.getContent().split(" ");
        if (cmdLst.length != 4) {
            // TODO:长度不对
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text",
                    "您的指令格式有误，请按照下面格式发送指令！\n添加地址 张三 13912345678 江苏省南京市玄武区北京东路1号4栋203");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 获取用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        MCustomInfo mCustomInfoCond = new MCustomInfo();
        mCustomInfoCond.setCustomId(userList.get(0).getCustomId());
        mCustomInfoCond.setOrderByStr(" row_no DESC ");
        List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
        if (mCustomInfoLst.size() >= 3) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "最多只能设置3个收件人地址");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        MCustomInfo mCustomInfo = new MCustomInfo();
        mCustomInfo.setCustomId(userList.get(0).getCustomId());
        if (mCustomInfoLst.isEmpty()) {
            mCustomInfo.setRowNo("1");
        } else {
            mCustomInfo.setRowNo(String.valueOf(Integer.valueOf((mCustomInfoLst.get(0).getRowNo())) + 1));
        }
        mCustomInfo.setName(cmdLst[1]);
        mCustomInfo.setTelNo(cmdLst[2]);
        mCustomInfo.setAddress(cmdLst[3]);
        mCustomInfo.setCreateId(userList.get(0).getId());
        mCustomInfo.setUpdateId(userList.get(0).getId());
        mCustomInfoMapper.insert(mCustomInfo);

        String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(), inputMsg.getToUserName(),
                returnTime, "text",
                "地址添加成功，客户编号是：\n【" + mCustomInfo.getCustomId() + mCustomInfo.getRowNo() + "】\n收件人："
                        + mCustomInfo.getName() + "\n电话号码：" + mCustomInfo.getTelNo() + "\n地址："
                        + mCustomInfo.getAddress());
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }

    @Override
    public void updateCustomInfo(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {

        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;
        String[] cmdLst = inputMsg.getContent().split(" ");
        if (cmdLst.length != 5) {
            // TODO:长度不对
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text",
                    "您的指令格式有误，请按照下面格式发送指令！\n修改地址 AXKJS1 张三 13912345678 江苏省南京市玄武区北京东路1号4栋203");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 获取用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        if (!userList.get(0).getCustomId().equals(cmdLst[1].substring(0, 5))) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text",
                    MessageFormat.format("您需要修改的地址编码【{0}】不正确，请重新输入。如需查询地址编号，请输入指令【查询地址】进行查询。", cmdLst[1]));
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        MCustomInfo mCustomInfoCond = new MCustomInfo();
        mCustomInfoCond.setCustomId(userList.get(0).getCustomId());
        if (cmdLst[1].length() > 5) {
            mCustomInfoCond.setRowNo(cmdLst[1].substring(5, 6));
        } else {
            mCustomInfoCond.setRowNo("1");
        }
        mCustomInfoCond.setOrderByStr(" row_no DESC ");
        List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
        if (mCustomInfoLst.isEmpty()) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text",
                    MessageFormat.format("您需要修改的地址编码【{0}】尚未设置，请检查后再修改。如需查询地址编号，请输入指令【查询地址】进行查询。", cmdLst[1]));
            LOGGER.info(msg);
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

        String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(), inputMsg.getToUserName(),
                returnTime, "text",
                "地址修改成功，新地址是：\n【" + mCustomInfo.getCustomId() + mCustomInfo.getRowNo() + "】\n收件人："
                        + mCustomInfo.getName() + "\n电话号码：" + mCustomInfo.getTelNo() + "\n地址："
                        + mCustomInfo.getAddress());
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }

    @Override
    public void queryCustomInfo(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {

        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;
        // 获取用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);

        MCustomInfo mCustomInfoCond = new MCustomInfo();
        mCustomInfoCond.setCustomId(userList.get(0).getCustomId());
        mCustomInfoCond.setOrderByStr(" row_no ASC ");
        List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
        if (mCustomInfoLst.isEmpty()) {
            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您尚未设置地址！回复【设置地址 姓名 电话 地址】来设置地址吧！");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        String customInfoMsg = "【{0}】\n姓名：{1}\n电话号码：{2}\n收件地址：{3}\n";
        for (MCustomInfo info : mCustomInfoLst) {
            sb.append(MessageFormat.format(customInfoMsg, info.getCustomId() + info.getRowNo(), info.getName(),
                    info.getTelNo(), info.getAddress()));
        }

        String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(), inputMsg.getToUserName(),
                returnTime, "text", "您的地址信息如下\n" + sb.toString());
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }
}
