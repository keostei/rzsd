package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.TShopInvoice;
import com.rzsd.wechat.common.dto.TShopSum;

public interface TShopInvoiceMapper {

    List<TShopInvoice> select(TShopInvoice tShopInvoiceCond);

    int insert(TShopInvoice tShopInvoice);

    List<TShopSum> selectShopInvoiceSum(TShopSum tShopSumCond);

    int update(TShopInvoice tShopInvoice);

    // List<TInvoice> getPersonalInvoice(TInvoice selectCond);

    // List<TInvoice> getScheduledData(TInvoice selectCond);

    // List<InvoiceData> getInvoiceData(InvoiceData invoiceData);
}
