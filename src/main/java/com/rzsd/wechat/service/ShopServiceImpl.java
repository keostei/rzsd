package com.rzsd.wechat.service;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.TShopInvoice;
import com.rzsd.wechat.common.dto.TShopItem;
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

    @Transactional
    @Override
    public int bulkInsertShopInvoice(List<TShopInvoice> tShopInvoiceLst) {
        int cnt = 0;
        TShopItem tShopItemCond = new TShopItem();
        for (TShopInvoice t : tShopInvoiceLst) {
            LOGGER.info("[入出库批量登录]条码：" + t.getBarcode() + ", 种类：" + t.getType() + ", 理由：" + t.getReason());
            cnt += tShopInvoiceMapper.insert(t);
            tShopItemCond.setShopId(t.getShopId());
            tShopItemCond.setBarcode(t.getBarcode());
            List<TShopItem> lst = tShopItemMapper.select(tShopItemCond);
            BigInteger updAmount;
            if ("1".equals(t.getType())) {
                updAmount = t.getCount();
            } else {
                updAmount = t.getCount().multiply(new BigInteger("-1"));
            }
            if (lst.isEmpty()) {
                TShopItem tShopItem = new TShopItem();
                tShopItem.setShopId(t.getShopId());
                tShopItem.setBarcode(t.getBarcode());
                tShopItem.setTotalAmount(updAmount);
                tShopItem.setCreateId(RzConst.SYS_ADMIN_ID);
                tShopItem.setUpdateId(RzConst.SYS_ADMIN_ID);
                tShopItemMapper.insert(tShopItem);
            } else {
                TShopItem tShopItem = lst.get(0);
                tShopItem.setTotalAmount(tShopItem.getTotalAmount().add(updAmount));
                tShopItem.setUpdateId(RzConst.SYS_ADMIN_ID);
                tShopItem.setUpdateTime(DateUtil.getCurrentTimestamp());
                tShopItemMapper.update(tShopItem);
            }
            LOGGER.info("[入出库批量登录]登录成功");
        }
        return cnt;
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

}
