package com.rzsd.wechat.service;

import java.math.BigInteger;
import java.util.List;

import com.rzsd.wechat.common.dto.TShopInvoice;
import com.rzsd.wechat.common.dto.TShopItem;
import com.rzsd.wechat.common.dto.TShopSum;

public interface ShopService {

    int bulkInsertShopInvoice(List<TShopInvoice> tShopInvoiceLst);

    List<TShopItem> selectShopItemWithItemInfo(TShopItem tShopItemCond);

    List<TShopInvoice> selectShopInvoiceByBarcode(String shopId, String barcode);

    List<TShopSum> selectShopSum(TShopSum tShopSumCond);

    int updateItemName(BigInteger shopId, String barCode, String itemName);

    int delShopInvoice(BigInteger shopInvoiceId);
}
