package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.TInvoice;

public interface TInvoiceMapper {

    List<TInvoice> select(TInvoice selectCond);

    int insert(TInvoice tInvoice);

    int update(TInvoice tInvoice);

    List<TInvoice> getPersonalInvoice(TInvoice selectCond);
}
