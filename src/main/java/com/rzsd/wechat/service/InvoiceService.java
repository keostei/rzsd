package com.rzsd.wechat.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rzsd.wechat.common.dto.TInvoice;
import com.rzsd.wechat.common.dto.TInvoiceDetail;
import com.rzsd.wechat.entity.InvoiceDeliver;

public interface InvoiceService {

    List<TInvoice> getConfirmLst();

    int doConfirm(String invoiceId, HttpServletRequest request);

    int doReject(String invoiceId, HttpServletRequest request);

    List<TInvoice> getPersonalInvoice(HttpServletRequest request);

    TInvoice getInvoiceById(String invoiceId, HttpServletRequest request);

    List<TInvoiceDetail> getInvoiceDetailById(String invoiceId, HttpServletRequest request);

    List<TInvoice> getInvoiceOutputInfo(HttpServletRequest request);

    List<InvoiceDeliver> importInvoiceData(final List<InvoiceDeliver> invoiceDeliverLst, HttpServletRequest request);
}
