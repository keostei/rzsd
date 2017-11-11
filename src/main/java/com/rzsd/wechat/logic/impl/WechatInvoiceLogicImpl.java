package com.rzsd.wechat.logic.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import com.rzsd.wechat.common.dto.TInvoiceDetail;
import com.rzsd.wechat.common.mapper.MCustomInfoMapper;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.common.mapper.TInvoiceDetailMapper;
import com.rzsd.wechat.common.mapper.TInvoiceMapper;
import com.rzsd.wechat.context.ChatContextInstance;
import com.rzsd.wechat.enmu.InvoiceDetailStatus;
import com.rzsd.wechat.enmu.InvoiceStatus;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatInvoiceLogic;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.InputMessage;
import com.rzsd.wechat.util.WechatMessageUtil;

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
    private TInvoiceDetailMapper tInvoiceDetailMapper;
    @Autowired
    private WechatCustomIdLogic wechatCustomIdLogicImpl;

    @Override
    public void createInvoice(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;
        String[] cmdLst = inputMsg.getContent().split(" ");
        if (cmdLst.length != 3 && cmdLst.length != 2 && cmdLst.length != 1) {
            String msg = WechatMessageUtil.getTextMessage("text.tinvoice.add.format.error", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }
        // 发货
        // 发货 ANMSX
        // 发货 ANMSX1
        // 发货 ANMSX1 A
        // 发货 ANMSX A
        // 发货 A

        String customCd = null;
        String proxyCd = null;
        if (cmdLst.length == 1) {
            // 默认值，不需要特别设置
        } else if (cmdLst.length == 2) {
            if (cmdLst[1].length() == 1) {
                customCd = null;
                proxyCd = cmdLst[1].toUpperCase();
            } else {
                customCd = cmdLst[1].toUpperCase();
                proxyCd = null;
            }
        } else if (cmdLst.length == 3) {
            customCd = cmdLst[1].toUpperCase();
            proxyCd = cmdLst[2].toUpperCase();
        }
        if (customCd != null && customCd.length() == 4) {
            customCd = customCd + "1";
        }
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        MUser mUser = null;
        // 用户信息没创建的时候，先创建用户，再提示用户设置地址
        if (userList.isEmpty()) {
            // 通过代理时，客户编码生成
            mUser = new MUser();
            mUser.setWechatOpenId(inputMsg.getFromUserName());
            if (proxyCd != null) {
                mUser.setCustomId(wechatCustomIdLogicImpl.generateId(true, proxyCd.charAt(0)));
            } else {
                mUser.setCustomId(wechatCustomIdLogicImpl.generateId(false, 'a'));
            }
            mUser.setUserType("0");
            mUser.setCreateId(RzConst.SYS_ADMIN_ID);
            mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
            mUserMapper.insert(mUser);

            String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), returnTime, "text", "您尚未设置收件地址，请先设置收件地址。");
            LOGGER.info(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        mUser = userList.get(0);
        // 通过代理发货的时候，变更客户代码
        if (proxyCd != null) {
            // 客户代码跟代理编码不一致时，重新生成客户代码
            if (mUser.getCustomId().substring(0, 2).equalsIgnoreCase("Z" + proxyCd)) {
                String oldCustomId = mUser.getCustomId();
                // 用户表客户代码更新
                String customId = wechatCustomIdLogicImpl.generateId(true, proxyCd.charAt(0));
                mUser.setCustomId(customId);
                mUser.setUpdateId(mUser.getId());
                mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
                mUserMapper.update(mUser);

                // 客户代码表地址统一更新
                MCustomInfo mCustomInfo = new MCustomInfo();
                mCustomInfo.setCustomId(customId);
                mCustomInfo.setOldCustomId(oldCustomId);
                mCustomInfo.setUpdateId(mUser.getId());
                mCustomInfo.setUpdateTime(DateUtil.getCurrentTimestamp());
                mCustomInfoMapper.updateCustomId(mCustomInfo);
                if (customCd != null && customCd.length() > 4) {
                    customCd = customId + customCd.substring(4);
                } else {
                    customCd = customId + "1";
                }
            }
        }
        // 检查后三位跟登陆的编码是否一致，不一致则报错
        if (customCd != null && !mUser.getCustomId().equalsIgnoreCase(customCd.substring(0, 4))) {
            String msg = WechatMessageUtil.getTextMessage("text.tinvoice.add.customcd.error",
                    inputMsg.getFromUserName(), inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        if (customCd == null) {
            customCd = mUser.getCustomId() + "1";
        }

        MCustomInfo mCustomInfoCond = new MCustomInfo();
        mCustomInfoCond.setCustomId(customCd.substring(0, 4));
        mCustomInfoCond.setRowNo(customCd.substring(4));
        mCustomInfoCond.setOrderByStr(" row_no DESC ");
        List<MCustomInfo> mCustomInfoLst = mCustomInfoMapper.select(mCustomInfoCond);
        boolean hasAddress = true;
        if (mCustomInfoLst.isEmpty()) {
            hasAddress = false;
        }

        TInvoice tInvoice = new TInvoice();
        tInvoice.setInvoiceDate(DateUtil.getCurrentTimestamp());
        tInvoice.setCustomCd(customCd);
        if (hasAddress) {
            tInvoice.setName(mCustomInfoLst.get(0).getName());
            tInvoice.setTelNo(mCustomInfoLst.get(0).getTelNo());
            tInvoice.setAddress(mCustomInfoLst.get(0).getAddress());
        }
        tInvoice.setInvoiceStatus(InvoiceStatus.YUYUE.getCode());
        tInvoice.setCreateId(mUser.getId());
        tInvoice.setUpdateId(mUser.getId());
        tInvoiceMapper.insert(tInvoice);

        String msg = null;
        if (hasAddress) {
            msg = WechatMessageUtil.getTextMessage("text.tinvoice.add.success", inputMsg.getFromUserName(),
                    inputMsg.getToUserName(), tInvoice.getInvoiceDate(), tInvoice.getCustomCd(), tInvoice.getName(),
                    tInvoice.getTelNo(), tInvoice.getAddress());
        } else {
            msg = WechatMessageUtil.getTextMessage("text.tinvoice.add.success.noaddress", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            ChatContextInstance.newInstance(inputMsg.getFromUserName());
            ChatContextInstance.setType(inputMsg.getFromUserName(), "1");
        }
        LOGGER.debug(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }

    @Override
    public void queryInvoice(InputMessage inputMsg, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        Long returnTime = DateUtil.getCurrentTimestamp().getTime() / 1000;

        // 检索用户信息
        MUser selectCond = new MUser();
        selectCond.setWechatOpenId(inputMsg.getFromUserName());
        List<MUser> userList = mUserMapper.select(selectCond);
        // 用户信息不存在时，提示没有发货记录
        if (userList.isEmpty()) {
            MUser mUser = new MUser();
            mUser.setWechatOpenId(inputMsg.getFromUserName());
            mUser.setCustomId(wechatCustomIdLogicImpl.generateId(false, 'a'));
            mUser.setUserType("0");
            mUser.setCreateId(RzConst.SYS_ADMIN_ID);
            mUser.setUpdateId(RzConst.SYS_ADMIN_ID);
            mUserMapper.insert(mUser);
            String msg = WechatMessageUtil.getTextMessage("text.tinvoice.query.nodata", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        MUser mUser = userList.get(0);
        TInvoice tInvoiceCond = new TInvoice();
        tInvoiceCond.setDelFlg("0");
        tInvoiceCond.setCreateId(mUser.getId());
        tInvoiceCond.setOrderByStr(" invoice_date DESC ");
        tInvoiceCond.setLimitCnt(3L);
        List<TInvoice> invoiceLst = tInvoiceMapper.select(tInvoiceCond);
        if (invoiceLst.isEmpty()) {
            String msg = WechatMessageUtil.getTextMessage("text.tinvoice.query.nodata", inputMsg.getFromUserName(),
                    inputMsg.getToUserName());
            LOGGER.debug(msg);
            response.getOutputStream().write(msg.getBytes("UTF-8"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("系统中查询到您最近三次发货记录是：");
        for (TInvoice tInvoice : invoiceLst) {
            sb.append("\n==========");
            sb.append("\n取件日期：").append(DateUtil.format(tInvoice.getInvoiceDate()));
            sb.append("\n").append("客户编码：").append(tInvoice.getCustomCd());
            sb.append("\n").append("收件人：").append(tInvoice.getName());
            sb.append("\n").append("当前状态：").append(InvoiceStatus.getCodeAsName(tInvoice.getInvoiceStatus()));
            if (!BigDecimal.ZERO.equals(tInvoice.getTotalWeight())) {
                sb.append("\n").append("包裹重量：").append(tInvoice.getTotalWeight());
            }
            if (!BigDecimal.ZERO.equals(tInvoice.getInvoiceAmountJpy())) {
                sb.append("\n").append("运费合计：").append(tInvoice.getInvoiceAmountJpy()).append("日元").append(" ")
                        .append(tInvoice.getInvoiceAmountCny()).append("人民币");
                sb.append("\n请尽快完成支付，否则可能会影响正常清关，如已完成支付，请无视。");
            }

            TInvoiceDetail tInvoiceDetailCond = new TInvoiceDetail();
            tInvoiceDetailCond.setDelFlg("0");
            tInvoiceDetailCond.setInvoiceId(tInvoice.getInvoiceId());
            tInvoiceDetailCond.setOrderByStr(" row_no ");
            List<TInvoiceDetail> tInvoiceDetailLst = tInvoiceDetailMapper.select(tInvoiceDetailCond);
            if (tInvoiceDetailLst.isEmpty()) {
                continue;
            }

            sb.append("\n----------\n包裹详细");
            for (TInvoiceDetail tInvoiceDetail : tInvoiceDetailLst) {
                sb.append("\n包裹状态：").append(InvoiceDetailStatus.getCodeAsName(tInvoiceDetail.getStatus()));
                sb.append("\n").append("重量：").append(tInvoiceDetail.getWeight());
                sb.append("\n").append("快递单号：").append(tInvoiceDetail.getTrackingNo());
            }
        }
        String msg = MessageFormat.format(RzConst.WECHAT_MESSAGE, inputMsg.getFromUserName(), inputMsg.getToUserName(),
                returnTime, "text", sb.toString());
        LOGGER.info(msg);
        response.getOutputStream().write(msg.getBytes("UTF-8"));
        return;
    }

}
