package com.rzsd.wechat.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.InvoiceData;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.dto.TInvoice;
import com.rzsd.wechat.common.dto.TInvoiceDetail;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.common.mapper.TInvoiceDetailMapper;
import com.rzsd.wechat.common.mapper.TInvoiceMapper;
import com.rzsd.wechat.context.PriceContext;
import com.rzsd.wechat.enmu.InvoiceDetailStatus;
import com.rzsd.wechat.enmu.InvoiceStatus;
import com.rzsd.wechat.entity.InvoiceDeliver;
import com.rzsd.wechat.entity.LoginUser;
import com.rzsd.wechat.exception.BussinessException;
import com.rzsd.wechat.util.CheckUtil;
import com.rzsd.wechat.util.DateUtil;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceServiceImpl.class.getName());

    @Autowired
    private TInvoiceMapper tInvoiceMappper;
    @Autowired
    private TInvoiceDetailMapper tInvoiceDetailMapper;
    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private SystemService systemServiceImpl;
    @Autowired
    private PriceContext priceContext;

    @Override
    public List<TInvoice> getConfirmLst() {
        TInvoice selectCond = new TInvoice();
        selectCond.setInvoiceStatus("1");
        selectCond.setDelFlg("0");
        selectCond.setOrderByStr(" invoice_date ");
        List<TInvoice> result = tInvoiceMappper.select(selectCond);
        for (TInvoice tInvoice : result) {
            if (StringUtils.isEmpty(tInvoice.getName())) {
                tInvoice.setName("未设置");
            }
            if (StringUtils.isEmpty(tInvoice.getTelNo())) {
                tInvoice.setTelNo("未设置");
            }
            if (StringUtils.isEmpty(tInvoice.getAddress())) {
                tInvoice.setAddress("未设置");
            }
        }
        return result;
    }

    @Override
    @Transactional
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
    @Transactional
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
        BigDecimal price = null;
        int rowNo = 0;
        for (InvoiceDeliver invoiceDeliver : invoiceDeliverLst) {
            // invoiceId发生变化时，对流水表进行更新
            if (invoiceId != null && !invoiceId.equals(invoiceDeliver.getInvoiceId())) {

                TInvoice tInvoice = new TInvoice();
                tInvoice.setInvoiceId(invoiceId);
                List<TInvoice> invoiceLst = tInvoiceMappper.select(tInvoice);
                tInvoice = invoiceLst.get(0);
                BigDecimal oldTotalWeight = tInvoice.getTotalWeight();
                tInvoice.setInvoiceStatus(InvoiceStatus.CHUKU.getCode());
                tInvoice.setTotalWeight(totalWeight);
                tInvoice.setInvoiceAmountJpy(
                        priceContext.calcPrice(tInvoice.getCreateId(), totalWeight, price, loginUser.getId()));
                tInvoice.setInvoiceAmountCny(tInvoice.getInvoiceAmountJpy().multiply(rate));
                tInvoice.setUpdateId(loginUser.getId());
                tInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
                tInvoiceMappper.update(tInvoice);
                // 如果重量发生变化，变化的重量差额更新到个人发货总重量
                if (!BigDecimal.ZERO.equals(totalWeight.subtract(oldTotalWeight))) {
                    MUser mUser = new MUser();
                    mUser.setId(tInvoice.getCreateId());
                    // mUser.setCustomId(tInvoice.getCustomCd().substring(0, 5));
                    List<MUser> usrLst = mUserMapper.select(mUser);
                    if (!usrLst.isEmpty()) {
                        mUser = usrLst.get(0);
                        mUser.setTotalWeight(mUser.getTotalWeight().add(totalWeight).subtract(oldTotalWeight));
                        mUser.setUpdateId(loginUser.getId());
                        mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
                        mUserMapper.update(mUser);
                    }
                }
                // 更新完之后对合计值清零
                totalWeight = BigDecimal.ZERO;
                price = null;
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
            if (invoiceDeliver.getPrice() != null) {
                price = invoiceDeliver.getPrice();
            }
            totalWeight = totalWeight.add(invoiceDeliver.getWeight());

            invoiceDeliver.setImportResult("导入成功");
        }
        // 对最后一组invoice数据进行更新
        if (invoiceId != null) {
            TInvoice tInvoice = new TInvoice();
            tInvoice.setInvoiceId(invoiceId);
            List<TInvoice> invoiceLst = tInvoiceMappper.select(tInvoice);
            tInvoice = invoiceLst.get(0);
            BigDecimal oldTotalWeight = tInvoice.getTotalWeight();
            tInvoice.setInvoiceStatus(InvoiceStatus.CHUKU.getCode());
            tInvoice.setTotalWeight(totalWeight);
            tInvoice.setInvoiceAmountJpy(
                    priceContext.calcPrice(tInvoice.getCreateId(), totalWeight, price, loginUser.getId()));
            tInvoice.setInvoiceAmountCny(tInvoice.getInvoiceAmountJpy().multiply(rate));
            tInvoice.setUpdateId(loginUser.getId());
            tInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
            tInvoiceMappper.update(tInvoice);
            // 如果重量发生变化，变化的重量差额更新到个人发货总重量
            if (!BigDecimal.ZERO.equals(totalWeight.subtract(oldTotalWeight))) {
                MUser mUser = new MUser();
                mUser.setId(tInvoice.getCreateId());
                // mUser.setCustomId(tInvoice.getCustomCd().substring(0, 5));
                List<MUser> usrLst = mUserMapper.select(mUser);
                if (!usrLst.isEmpty()) {
                    mUser = usrLst.get(0);
                    mUser.setTotalWeight(mUser.getTotalWeight().add(totalWeight).subtract(oldTotalWeight));
                    mUser.setUpdateId(loginUser.getId());
                    mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
                    mUserMapper.update(mUser);
                }
            }
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
    @Transactional
    public int editInvoiceData(InvoiceData invoiceDataCond, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");

        BigDecimal rate = new BigDecimal(systemServiceImpl.getSysParamByKey("JPN_CNY_RATE").getParamValue())
                .divide(new BigDecimal("100"));

        TInvoice tInvoice = new TInvoice();
        tInvoice.setInvoiceId(new BigInteger(invoiceDataCond.getInvoiceId()));
        List<TInvoice> invoiceLst = tInvoiceMappper.select(tInvoice);
        tInvoice = invoiceLst.get(0);
        BigDecimal oldTotalWeight = tInvoice.getTotalWeight();
        tInvoice.setTotalWeight(invoiceDataCond.getTotalWeight());
        tInvoice.setInvoiceAmountJpy(new BigDecimal(invoiceDataCond.getInvoiceAmountJpy()));
        tInvoice.setInvoiceAmountCny(tInvoice.getInvoiceAmountJpy().multiply(rate));
        tInvoice.setName(invoiceDataCond.getName());
        tInvoice.setAddress(invoiceDataCond.getAddress());
        tInvoice.setInvoiceStatus(invoiceDataCond.getInvoiceStatus());
        tInvoice.setUpdateId(loginUser.getId());
        tInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
        tInvoiceMappper.update(tInvoice);

        // 如果重量发生变化，变化的重量差额更新到个人发货总重量
        if (!BigDecimal.ZERO.equals(invoiceDataCond.getTotalWeight().subtract(oldTotalWeight))) {
            MUser mUser = new MUser();
            mUser.setId(tInvoice.getCreateId());
            // mUser.setCustomId(tInvoice.getCustomCd().substring(0, 5));
            List<MUser> usrLst = mUserMapper.select(mUser);
            if (!usrLst.isEmpty()) {
                mUser = usrLst.get(0);
                mUser.setTotalWeight(
                        mUser.getTotalWeight().add(invoiceDataCond.getTotalWeight()).subtract(oldTotalWeight));
                mUser.setUpdateId(loginUser.getId());
                mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
                mUserMapper.update(mUser);
            }
        }

        if (StringUtils.isEmpty(invoiceDataCond.getDispLotNo())) {
            return 0;
        }
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
    @Transactional
    public int doAppointment(TInvoice tInvoice, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        tInvoice.setInvoiceStatus(InvoiceStatus.YUYUE.getCode());
        tInvoice.setCreateId(loginUser.getId());
        tInvoice.setUpdateId(loginUser.getId());
        tInvoiceMappper.insert(tInvoice);
        return 0;
    }

    @Override
    @Transactional
    public int doOnekeyUpdate(String lotNo, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        TInvoiceDetail tInvoiceDetail = new TInvoiceDetail();
        tInvoiceDetail.setStatus(InvoiceDetailStatus.GNHGQGWC.getCode());
        tInvoiceDetail.setLotNo(lotNo);
        tInvoiceDetail.setUpdateTime(DateUtil.getCurrentTimestamp());
        tInvoiceDetail.setUpdateId(loginUser.getId());
        tInvoiceDetailMapper.updateInvoiceDetailStatus(tInvoiceDetail);
        return 0;
    }

    @Override
    @Transactional
    public int doUpdateDetailStatus(String oldStatus, String newStatus) {
        LOGGER.info("[包裹状态自动更新开始]更新前状态：" + oldStatus + "(" + InvoiceDetailStatus.getCodeAsName(oldStatus) + ")，更新后状态："
                + newStatus + "(" + InvoiceDetailStatus.getCodeAsName(newStatus) + ")");
        TInvoiceDetail selectCond = new TInvoiceDetail();
        selectCond.setStatus(oldStatus);
        selectCond.setDelFlg("0");
        List<TInvoiceDetail> tInvoiceDetailLst = tInvoiceDetailMapper.select(selectCond);
        for (TInvoiceDetail tInvoiceDetail : tInvoiceDetailLst) {
            LOGGER.info(MessageFormat.format("数据更新，流水号：{0}，行号：{1}", tInvoiceDetail.getInvoiceId(),
                    tInvoiceDetail.getRowNo()));
            tInvoiceDetail.setStatus(newStatus);
            tInvoiceDetail.setUpdateTime(DateUtil.getCurrentTimestamp());
            tInvoiceDetail.setUpdateId(RzConst.SYS_ADMIN_ID);
            tInvoiceDetailMapper.update(tInvoiceDetail);
        }
        LOGGER.info("[包裹状态自动更新结束]更新前状态：" + oldStatus + "(" + InvoiceDetailStatus.getCodeAsName(oldStatus) + ")，更新后状态："
                + newStatus + "(" + InvoiceDetailStatus.getCodeAsName(newStatus) + ")");
        return 0;
    }

    @Override
    public List<InvoiceDeliver> checkImportInvoiceData(List<InvoiceDeliver> invoiceDeliverLst,
            HttpServletRequest request) {
        // 按照客户编码排序
        invoiceDeliverLst.sort((o1, o2) -> {
            return o1.getCustomCd().compareTo(o2.getCustomCd());
        });
        // 客户编码单价map
        Map<String, BigDecimal> priceMap = new HashMap<>();

        BigInteger invoiceId = null;
        String customId = null;
        TInvoice tInvoiceCond = new TInvoice();
        tInvoiceCond.setDelFlg("0");
        tInvoiceCond.setInvoiceStatus(InvoiceStatus.QUHUO.getCode());
        // 判断是单号变更，还是单号导入
        boolean hasNewTrackingNo = false;
        for (InvoiceDeliver invoiceDeliver : invoiceDeliverLst) {
            if (!StringUtils.isEmpty(invoiceDeliver.getNewTrackingNo())) {
                hasNewTrackingNo = true;
                break;
            }
        }
        boolean hasEmptyNewTrackingNo = false;
        for (InvoiceDeliver invoiceDeliver : invoiceDeliverLst) {
            if (StringUtils.isEmpty(invoiceDeliver.getNewTrackingNo())) {
                hasEmptyNewTrackingNo = true;
                break;
            }
        }
        if (hasEmptyNewTrackingNo && hasNewTrackingNo) {
            throw new BussinessException("系统无法判断单号导入还是变更单号。");
        }
        for (InvoiceDeliver invoiceDeliver : invoiceDeliverLst) {
            // 变更快递单号check，填写时必须要有国内单号
            if (!StringUtils.isEmpty(invoiceDeliver.getNewTrackingNo())) {
                if (StringUtils.isEmpty(invoiceDeliver.getNewTrackingNo())) {
                    invoiceDeliver.setImportResult("快递单号变更时，旧快递单号必须填写");
                }
                // 变更快递单号时，仅对单号做check
                TInvoiceDetail selectCond = new TInvoiceDetail();
                selectCond.setDelFlg("0");
                selectCond.setTrackingNo(invoiceDeliver.getNewTrackingNo());
                List<TInvoiceDetail> lst = tInvoiceDetailMapper.select(selectCond);
                if (lst.isEmpty()) {
                    invoiceDeliver.setImportResult("旧快递单号未导入系统");
                }
                if (lst.size() > 1) {
                    invoiceDeliver.setImportResult("旧快递单号有多条记录存在");
                }
                continue;
            }
            // 客户编码
            if (StringUtils.isEmpty(invoiceDeliver.getCustomCd())) {
                invoiceDeliver.setImportResult("客户编码不能为空。");
                continue;
            }
            if (invoiceDeliver.getCustomCd().length() == RzConst.CUSTOM_ID_LENGTH) {
                // 默认客户编码
                invoiceDeliver.setCustomCd(invoiceDeliver.getCustomCd() + "1");
            }

            // 客户编码发生变化时
            if (!invoiceDeliver.getCustomCd().equals(customId)) {
                invoiceId = null;
                customId = invoiceDeliver.getCustomCd();
                // 客户编码是否有发货申请
                tInvoiceCond.setCustomCd(customId);
                List<TInvoice> tInvoiceLst = tInvoiceMappper.select(tInvoiceCond);
                if (tInvoiceLst.isEmpty()) {
                    invoiceDeliver.setImportResult("发货申请不存在");
                    continue;
                }
                // 客户编码是否存在多次发货记录
                if (tInvoiceLst.size() > 1) {
                    invoiceDeliver.setImportResult("存在多条发货申请记录");
                    continue;
                }
                invoiceId = tInvoiceLst.get(0).getInvoiceId();
                invoiceDeliver.setInvoiceId(invoiceId);
            } else {
                if (invoiceId == null) {
                    invoiceDeliver.setImportResult("同上");
                    continue;
                }
                invoiceDeliver.setInvoiceId(invoiceId);
            }

            // 内部编码 必须check，长度check
            if (StringUtils.isEmpty(invoiceDeliver.getLogNo())) {
                invoiceDeliver.setImportResult("内部编号不能为空");
                continue;
            }
            if (!CheckUtil.isLengthValid(invoiceDeliver.getLogNo(), 20)) {
                invoiceDeliver.setImportResult("内部编号长度不要超过20");
                continue;
            }

            // 国内快递编号 必须check，长度check
            if (StringUtils.isEmpty(invoiceDeliver.getTrackingNo())) {
                invoiceDeliver.setImportResult("快递单号不能为空");
                continue;
            }
            if (!CheckUtil.isLengthValid(invoiceDeliver.getTrackingNo(), 20)) {
                invoiceDeliver.setImportResult("快递单号长度不要超过20");
                continue;
            }

            // 重量必须check，长度check，数字check
            if (StringUtils.isEmpty(invoiceDeliver.getStrWeight())) {
                invoiceDeliver.setImportResult("重量不能为空");
                continue;
            }
            if (!CheckUtil.isValidString(invoiceDeliver.getStrWeight(), CheckUtil.REGEX_NUM)) {
                invoiceDeliver.setImportResult("重量只能是数字");
                continue;
            }
            if (!CheckUtil.isLengthValid(invoiceDeliver.getStrWeight(), 2)) {
                invoiceDeliver.setImportResult("重量不要超过20");
                continue;
            }
            invoiceDeliver.setWeight(new BigDecimal(invoiceDeliver.getStrWeight()));

            // 单价必须check，长度check，数字check
            // if (StringUtils.isEmpty(invoiceDeliver.getStrPrice())) {
            // invoiceDeliver.setImportResult("单价不能为空");
            // continue;
            // }
            if (!CheckUtil.isValidString(invoiceDeliver.getStrPrice(), CheckUtil.REGEX_NUM)) {
                invoiceDeliver.setImportResult("单价只能是数字");
                continue;
            }
            if (!CheckUtil.isLengthValid(invoiceDeliver.getStrPrice(), 5)) {
                invoiceDeliver.setImportResult("单价不要超过99999");
                continue;
            }

            if (!StringUtils.isEmpty(invoiceDeliver.getStrPrice())) {
                if (!priceMap.containsKey(customId)) {
                    priceMap.put(customId, new BigDecimal(invoiceDeliver.getStrPrice()));
                } else if (new BigDecimal(invoiceDeliver.getStrPrice()).compareTo(priceMap.get(customId)) != 0) {
                    LOGGER.warn("同一个客户编码的有多个单价。客户编码：" + customId);
                    invoiceDeliver.setImportResult("相同客户编码存在不同单价");
                }
            }
        }

        // 设置单价
        for (InvoiceDeliver invoiceDeliver : invoiceDeliverLst) {
            if (!StringUtils.isEmpty(invoiceDeliver.getImportResult())) {
                continue;
            }
            if (!StringUtils.isEmpty(invoiceDeliver.getNewTrackingNo())) {
                continue;
            }
            if (!priceMap.containsKey(invoiceDeliver.getCustomCd())) {
                invoiceDeliver.setImportResult("未设置单价");
                continue;
            }
            invoiceDeliver.setPrice(priceMap.get(invoiceDeliver.getCustomCd()));
            invoiceDeliver.setStrPrice(String.valueOf(invoiceDeliver.getPrice()));
        }

        return invoiceDeliverLst;
    }

}
