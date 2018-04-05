package com.rzsd.wechat.service;

import java.util.List;

import com.rzsd.wechat.common.dto.TShopInvoice;
import com.rzsd.wechat.common.dto.TShopItem;

public interface ShopService {

    int bulkInsertShopInvoice(List<TShopInvoice> tShopInvoiceLst);

    List<TShopItem> selectShopItemWithItemInfo(TShopItem tShopItemCond);

    List<TShopInvoice> selectShopInvoiceByBarcode(String shopId, String barcode);
}
