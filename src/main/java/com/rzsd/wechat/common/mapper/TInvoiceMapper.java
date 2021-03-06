package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.InvoiceData;
import com.rzsd.wechat.common.dto.TInvoice;

public interface TInvoiceMapper {

    List<TInvoice> select(TInvoice selectCond);

    int insert(TInvoice tInvoice);

    int update(TInvoice tInvoice);

    List<TInvoice> getPersonalInvoice(TInvoice selectCond);

    List<TInvoice> getScheduledData(TInvoice selectCond);

    List<InvoiceData> getInvoiceData(InvoiceData invoiceData);
}
