package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.TShopInvoice;

public interface TShopInvoiceMapper {

    List<TShopInvoice> select(TShopInvoice tShopInvoiceCond);

    int insert(TShopInvoice tShopInvoice);

    // int update(TInvoice tInvoice);

    // List<TInvoice> getPersonalInvoice(TInvoice selectCond);

    // List<TInvoice> getScheduledData(TInvoice selectCond);

    // List<InvoiceData> getInvoiceData(InvoiceData invoiceData);
}
