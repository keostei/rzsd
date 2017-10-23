package com.rzsd.wechat.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rzsd.wechat.common.dto.InvoiceData;
import com.rzsd.wechat.common.dto.TInvoice;
import com.rzsd.wechat.common.dto.TInvoiceDetail;
import com.rzsd.wechat.common.mapper.TInvoiceDetailMapper;
import com.rzsd.wechat.common.mapper.TInvoiceMapper;
import com.rzsd.wechat.enmu.InvoiceDetailStatus;
import com.rzsd.wechat.enmu.InvoiceStatus;
import com.rzsd.wechat.entity.InvoiceDeliver;
import com.rzsd.wechat.entity.LoginUser;
import com.rzsd.wechat.util.DateUtil;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private TInvoiceMapper tInvoiceMappper;
    @Autowired
    private TInvoiceDetailMapper tInvoiceDetailMapper;
    @Autowired
    private SystemService systemServiceImpl;

    @Override
    public List<TInvoice> getConfirmLst() {
        TInvoice selectCond = new TInvoice();
        selectCond.setInvoiceStatus("1");
        selectCond.setDelFlg("0");
        selectCond.setOrderByStr(" invoice_date ");
        List<TInvoice> result = tInvoiceMappper.select(selectCond);
        return result;
    }

    @Override
    public int doConfirm(String invoiceId, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        TInvoice tInvoice = new TInvoice();
        tInvoice.setInvoiceId(new BigInteger(invoiceId));
        tInvoice.setInvoiceStatus("2");
        tInvoice.setUpdateId(loginUser.getId());
        tInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
        return tInvoiceMappper.update(tInvoice);
    }

    @Override
    public int doReject(String invoiceId, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        TInvoice tInvoice = new TInvoice();
        tInvoice.setInvoiceId(new BigInteger(invoiceId));
        tInvoice.setDelFlg("1");
        tInvoice.setUpdateId(loginUser.getId());
        tInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
        return tInvoiceMappper.update(tInvoice);
    }

    @Override
    public List<TInvoice> getPersonalInvoice(HttpServletRequest request) {
        TInvoice selectCond = new TInvoice();
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        selectCond.setCreateId(loginUser.getId());
        List<TInvoice> invoiceLst = tInvoiceMappper.getPersonalInvoice(selectCond);
        for (TInvoice ti : invoiceLst) {
            ti.setInvoiceStatusName(InvoiceStatus.getCodeAsName(ti.getInvoiceStatus()));
            if (StringUtils.isEmpty(ti.getInvoiceRequirement())) {
                ti.setInvoiceRequirement("");
            }
        }
        return invoiceLst;
    }

    @Override
    public List<TInvoice> getInvoiceOutputInfo(HttpServletRequest request) {
        TInvoice selectCond = new TInvoice();
        selectCond.setDelFlg("0");
        selectCond.setInvoiceStatus(InvoiceStatus.QUHUO.getCode());
        List<TInvoice> invoiceLst = tInvoiceMappper.select(selectCond);
        return invoiceLst;
    }

    @Override
    @Transactional
    public List<InvoiceDeliver> importInvoiceData(List<InvoiceDeliver> invoiceDeliverLst, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        invoiceDeliverLst.sort((o1, o2) -> {
            if (!o1.getInvoiceId().equals(o2.getInvoiceId())) {
                return o1.getInvoiceId().compareTo(o2.getInvoiceId());
            }
            return o1.getLogNo().compareTo(o2.getLogNo());
        });

        BigDecimal rate = new BigDecimal(systemServiceImpl.getSysParamByKey("JPN_CNY_RATE").getParamValue())
                .divide(new BigDecimal("100"));
        BigInteger invoiceId = null;
        BigDecimal totalWeight = BigDecimal.ZERO;
        int rowNo = 0;
        for (InvoiceDeliver invoiceDeliver : invoiceDeliverLst) {
            // invoiceId发生变化时，对流水表进行更新
            if (invoiceId != null && !invoiceId.equals(invoiceDeliver.getInvoiceId())) {

                TInvoice tInvoice = new TInvoice();
                tInvoice.setInvoiceId(invoiceId);
                tInvoice.setInvoiceStatus("4");
                tInvoice.setTotalWeight(totalWeight);
                tInvoice.setInvoiceAmountJpy(totalWeight.multiply(new BigDecimal(960)));
                tInvoice.setInvoiceAmountCny(tInvoice.getInvoiceAmountJpy().multiply(rate));
                tInvoice.setUpdateId(loginUser.getId());
                tInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
                tInvoiceMappper.update(tInvoice);
                // 更新完之后对合计值清零
                totalWeight = BigDecimal.ZERO;
                rowNo = 0;
            }

            TInvoiceDetail tInvoiceDetail = new TInvoiceDetail();
            tInvoiceDetail.setInvoiceId(invoiceDeliver.getInvoiceId());
            tInvoiceDetail.setRowNo(String.format("%03d", ++rowNo));
            tInvoiceDetail.setLotNo(invoiceDeliver.getLogNo());
            tInvoiceDetail.setTrackingNo(invoiceDeliver.getTrackingNo());
            tInvoiceDetail.setWeight(invoiceDeliver.getWeight());
            tInvoiceDetail.setUpdateId(loginUser.getId());
            tInvoiceDetail.setUpdateTime(DateUtil.getCurrentTimestamp());
            if (tInvoiceDetailMapper.update(tInvoiceDetail) == 0) {
                tInvoiceDetail.setCreateId(loginUser.getId());
                tInvoiceDetailMapper.insert(tInvoiceDetail);
            }
            invoiceId = invoiceDeliver.getInvoiceId();
            totalWeight = totalWeight.add(invoiceDeliver.getWeight());
        }
        // 对最后一组invoice数据进行更新
        if (invoiceId != null) {
            TInvoice tInvoice = new TInvoice();
            tInvoice.setInvoiceId(invoiceId);
            tInvoice.setInvoiceAmountJpy(totalWeight.multiply(new BigDecimal(960)));
            tInvoice.setInvoiceAmountCny(tInvoice.getInvoiceAmountJpy().multiply(rate));
            tInvoiceMappper.update(tInvoice);
            // 更新完之后对合计值清零
            totalWeight = BigDecimal.ZERO;
            rowNo = 0;
        }

        return invoiceDeliverLst;
    }

    @Override
    public TInvoice getInvoiceById(String invoiceId, HttpServletRequest request) {
        TInvoice selectCond = new TInvoice();
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        selectCond.setCreateId(loginUser.getId());
        selectCond.setInvoiceId(new BigInteger(invoiceId));
        List<TInvoice> invoiceLst = tInvoiceMappper.getPersonalInvoice(selectCond);
        if (invoiceLst.isEmpty()) {
            return null;
        }
        for (TInvoice ti : invoiceLst) {
            ti.setInvoiceStatusName(InvoiceStatus.getCodeAsName(ti.getInvoiceStatus()));
            if (StringUtils.isEmpty(ti.getInvoiceRequirement())) {
                ti.setInvoiceRequirement("");
            }
        }
        return invoiceLst.get(0);
    }

    @Override
    public List<TInvoiceDetail> getInvoiceDetailById(String invoiceId, HttpServletRequest request) {
        TInvoiceDetail selectCond = new TInvoiceDetail();
        selectCond.setInvoiceId(new BigInteger(invoiceId));
        selectCond.setOrderByStr(" row_no ");
        List<TInvoiceDetail> invoiceDetailLst = tInvoiceDetailMapper.select(selectCond);
        for (TInvoiceDetail tInvoiceDetail : invoiceDetailLst) {
            tInvoiceDetail.setStatusName(InvoiceDetailStatus.getCodeAsName(tInvoiceDetail.getStatus()));
        }
        return invoiceDetailLst;
    }

    @Override
    public List<InvoiceData> searchInvoiceData(InvoiceData invoiceDataCond) {

        List<InvoiceData> invoiceDataLst = tInvoiceMappper.getInvoiceData(invoiceDataCond);
        List<InvoiceData> result = new ArrayList<>();
        String invoiceId = "";
        String dispLotNo = "";
        String dispTrackingNo = "";
        String dispWeight = "";
        for (InvoiceData invoiceData : invoiceDataLst) {
            if (invoiceId == null || !invoiceId.equals(invoiceData.getInvoiceId())) {
                if (dispLotNo.length() > 0) {
                    result.get(result.size() - 1).setDispLotNo(dispLotNo.substring(1));
                }
                if (dispTrackingNo.length() > 0) {
                    result.get(result.size() - 1).setDispTrackingNo(dispTrackingNo.substring(1));
                }
                if (dispWeight.length() > 0) {
                    result.get(result.size() - 1).setDispWeight(dispWeight.substring(1));
                }
                result.add(invoiceData);
                invoiceData.setInvoiceStatusName(InvoiceStatus.getCodeAsName(invoiceData.getInvoiceStatus()));
                dispLotNo = "";
                dispTrackingNo = "";
                dispWeight = "";
            }
            if (!StringUtils.isEmpty(invoiceData.getLotNo())) {

                dispLotNo += "," + invoiceData.getLotNo();
            }
            if (!StringUtils.isEmpty(invoiceData.getTrackingNo())) {
                dispTrackingNo += "," + invoiceData.getTrackingNo();
            }
            if (invoiceData.getWeight() != null && !BigDecimal.ZERO.equals(invoiceData.getWeight())) {
                dispWeight += "," + invoiceData.getWeight().intValue();
            }
            invoiceId = invoiceData.getInvoiceId();
        }
        if (dispLotNo.length() > 0) {
            result.get(result.size() - 1).setDispLotNo(dispLotNo.substring(1));
        }
        if (dispTrackingNo.length() > 0) {
            result.get(result.size() - 1).setDispTrackingNo(dispTrackingNo.substring(1));
        }
        if (dispWeight.length() > 0) {
            result.get(result.size() - 1).setDispWeight(dispWeight.substring(1));
        }
        return result;
    }

    @Override
    public int editInvoiceData(InvoiceData invoiceDataCond, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");

        BigDecimal rate = new BigDecimal(systemServiceImpl.getSysParamByKey("JPN_CNY_RATE").getParamValue())
                .divide(new BigDecimal("100"));

        TInvoice tInvoice = new TInvoice();
        tInvoice.setInvoiceId(new BigInteger(invoiceDataCond.getInvoiceId()));
        tInvoice.setTotalWeight(invoiceDataCond.getTotalWeight());
        tInvoice.setInvoiceAmountJpy(new BigDecimal(invoiceDataCond.getInvoiceAmountJpy()));
        tInvoice.setInvoiceAmountCny(tInvoice.getInvoiceAmountJpy().multiply(rate));
        tInvoice.setName(invoiceDataCond.getName());
        tInvoice.setAddress(invoiceDataCond.getAddress());
        tInvoice.setInvoiceStatus(invoiceDataCond.getInvoiceStatus());
        tInvoice.setUpdateId(loginUser.getId());
        tInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
        tInvoiceMappper.update(tInvoice);

        String[] lotNoLst = invoiceDataCond.getDispLotNo().split(",");
        String[] trackingNoLst = invoiceDataCond.getDispTrackingNo().split(",");
        String[] weightLst = invoiceDataCond.getDispWeight().split(",");
        for (int i = 0; i < lotNoLst.length; i++) {
            TInvoiceDetail tInvoiceDetail = new TInvoiceDetail();
            tInvoiceDetail.setInvoiceId(new BigInteger(invoiceDataCond.getInvoiceId()));
            tInvoiceDetail.setRowNo(String.format("%03d", (i + 1)));
            tInvoiceDetail.setLotNo(lotNoLst[i]);
            tInvoiceDetail.setTrackingNo(trackingNoLst[i]);
            tInvoiceDetail.setWeight(new BigDecimal(weightLst[i]));
            tInvoiceDetail.setUpdateId(loginUser.getId());
            tInvoiceDetail.setUpdateTime(DateUtil.getCurrentTimestamp());
            if (tInvoiceDetailMapper.update(tInvoiceDetail) == 0) {
                tInvoiceDetail.setCreateId(loginUser.getId());
                tInvoiceDetail.setCreateTime(DateUtil.getCurrentTimestamp());
                tInvoiceDetailMapper.insert(tInvoiceDetail);
            }
        }
        return 0;
    }

    @Override
    public int doAppointment(TInvoice tInvoice, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        tInvoice.setInvoiceStatus(InvoiceStatus.YUYUE.getCode());
        tInvoice.setCreateId(loginUser.getId());
        tInvoice.setUpdateId(loginUser.getId());
        tInvoiceMappper.insert(tInvoice);
        return 0;
    }

}
