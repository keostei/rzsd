package com.rzsd.wechat.service;

import java.util.List;

import com.rzsd.wechat.common.dto.TShopInvoice;

public interface ShopService {

    int bulkInsertShopInvoice(List<TShopInvoice> tShopInvoiceLst);
}
