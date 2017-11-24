package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.TInvoiceDetail;

public interface TInvoiceDetailMapper {

    List<TInvoiceDetail> select(TInvoiceDetail selectCond);

    int insert(TInvoiceDetail tInvoiceDetail);

    int update(TInvoiceDetail tInvoiceDetail);

    int updateInvoiceDetailStatus(TInvoiceDetail tInvoiceDetail);

    List<TInvoiceDetail> selectInvoiceDetailByLotNo(TInvoiceDetail selectCond);
}
