package com.rzsd.wechat.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.MShopItem;
import com.rzsd.wechat.common.dto.TShopInvoice;
import com.rzsd.wechat.common.dto.TShopItem;
import com.rzsd.wechat.common.dto.TShopSum;
import com.rzsd.wechat.common.mapper.MShopItemMapper;
import com.rzsd.wechat.common.mapper.TShopInvoiceMapper;
import com.rzsd.wechat.common.mapper.TShopItemMapper;
import com.rzsd.wechat.util.DateUtil;

@Service
public class ShopServiceImpl implements ShopService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopServiceImpl.class.getName());
    @Autowired
    private TShopInvoiceMapper tShopInvoiceMapper;
    @Autowired
    private TShopItemMapper tShopItemMapper;
    @Autowired
    private MShopItemMapper mShopItemMapper;

    @Transactional
    @Override
    public int bulkInsertShopInvoice(List<TShopInvoice> tShopInvoiceLst) {
        int cnt = 0;

        for (TShopInvoice t : tShopInvoiceLst) {
            LOGGER.info("[入出库批量登录]条码：" + t.getBarcode() + ", 种类：" + t.getType() + ", 理由：" + t.getReason());
            cnt += tShopInvoiceMapper.insert(t);
            updateShopItem(t.getShopId(), t.getBarcode(), t.getType(), t.getCount());
            LOGGER.info("[入出库批量登录]登录成功");
        }
        return cnt;
    }

    private int updateShopItem(BigInteger shopId, String barCode, String type, BigInteger updAmount) {
        TShopItem tShopItemCond = new TShopItem();
        tShopItemCond.setShopId(shopId);
        tShopItemCond.setBarcode(barCode);
        List<TShopItem> lst = tShopItemMapper.select(tShopItemCond);
        ;
        // 批量入库
        if (!"1".equals(type)) {
            updAmount = updAmount.multiply(new BigInteger("-1"));
        }
        TShopItem tShopItem;
        if (lst.isEmpty()) {
            tShopItem = new TShopItem();
            tShopItem.setShopId(shopId);
            tShopItem.setBarcode(barCode);
            tShopItem.setTotalAmount(updAmount);
            tShopItem.setCreateId(RzConst.SYS_ADMIN_ID);
            tShopItem.setUpdateId(RzConst.SYS_ADMIN_ID);
            tShopItemMapper.insert(tShopItem);
        } else {
            tShopItem = lst.get(0);
            tShopItem.setTotalAmount(tShopItem.getTotalAmount().add(updAmount));
            tShopItem.setUpdateId(RzConst.SYS_ADMIN_ID);
            tShopItem.setUpdateTime(DateUtil.getCurrentTimestamp());
            tShopItemMapper.update(tShopItem);
        }
        return 1;
    }

    @Override
    public List<TShopItem> selectShopItemWithItemInfo(TShopItem tShopItemCond) {
        return tShopItemMapper.selectShopItemWithItemInfo(tShopItemCond);
    }

    @Override
    public List<TShopInvoice> selectShopInvoiceByBarcode(String shopId, String barcode) {
        TShopInvoice tShopInvoiceCond = new TShopInvoice();
        tShopInvoiceCond.setShopId(new BigInteger(shopId));
        tShopInvoiceCond.setBarcode(barcode);
        tShopInvoiceCond.setDelFlg("0");
        tShopInvoiceCond.setOrderByStr("shop_invoice_id DESC ");
        return tShopInvoiceMapper.select(tShopInvoiceCond);
    }

    @Override
    public List<TShopSum> selectShopSum(TShopSum tShopSumCond) {
        List<TShopSum> tShopSumLst = tShopInvoiceMapper.selectShopInvoiceSum(tShopSumCond);
        for (TShopSum tss : tShopSumLst) {
            if (tss.getItemName() == null) {
                tss.setItemName("商品名未设置");
            }
            if (tss.getPriceRmb() == null) {
                tss.setPriceRmb(BigDecimal.ZERO);
            }
            if (tss.getPriceShow() == null) {
                tss.setPriceShow(BigDecimal.ZERO);
            }
            tss.setPriceRmbSum(tss.getPriceRmb().multiply(new BigDecimal(tss.getSumCnt())));
            tss.setPriceShowSum(tss.getPriceShow().multiply(new BigDecimal(tss.getSumCnt())));
        }
        return tShopSumLst;
    }

    @Override
    public int updateItemName(BigInteger shopId, String barCode, String itemName) {
        MShopItem selectCond = new MShopItem();
        selectCond.setShopId(shopId);
        selectCond.setBarcode(barCode);
        List<MShopItem> shopItemLst = mShopItemMapper.select(selectCond);
        if (shopItemLst.isEmpty()) {
            MShopItem mShopItem = new MShopItem();
            mShopItem.setShopId(shopId);
            mShopItem.setBarcode(barCode);
            mShopItem.setItemName(itemName);
            mShopItem.setCreateId(RzConst.SYS_ADMIN_ID);
            mShopItem.setUpdateId(RzConst.SYS_ADMIN_ID);
            mShopItemMapper.insert(mShopItem);
        } else {
            MShopItem mShopItem = shopItemLst.get(0);
            mShopItem.setItemName(itemName);
            mShopItem.setDelFlg("0");
            mShopItem.setUpdateId(RzConst.SYS_ADMIN_ID);
            mShopItem.setUpdateTime(DateUtil.getCurrentTimestamp());
            mShopItemMapper.update(mShopItem);
        }
        return 1;
    }

    @Override
    public int delShopInvoice(BigInteger shopInvoiceId) {
        TShopInvoice tShopInvoiceCond = new TShopInvoice();
        tShopInvoiceCond.setShopInvoiceId(shopInvoiceId);
        List<TShopInvoice> shopInvoiceLst = tShopInvoiceMapper.select(tShopInvoiceCond);
        if (shopInvoiceLst.isEmpty()) {
            return 0;
        }

        TShopInvoice tShopInvoice = shopInvoiceLst.get(0);
        tShopInvoice.setDelFlg("1");
        tShopInvoice.setUpdateId(RzConst.SYS_ADMIN_ID);
        tShopInvoice.setUpdateTime(DateUtil.getCurrentTimestamp());
        tShopInvoiceMapper.update(tShopInvoice);

        updateShopItem(tShopInvoice.getShopId(), tShopInvoice.getBarcode(),
                "1".equals(tShopInvoice.getType()) ? "2" : "1", tShopInvoice.getCount());
        return 1;
    }

}
