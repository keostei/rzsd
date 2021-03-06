package com.rzsd.wechat.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rzsd.wechat.common.dto.InvoiceData;
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

    List<InvoiceDeliver> checkImportInvoiceData(final List<InvoiceDeliver> invoiceDeliverLst,
            HttpServletRequest request);

    List<InvoiceData> searchInvoiceData(InvoiceData invoiceDataCond);

    int editInvoiceData(InvoiceData invoiceDataCond, HttpServletRequest request);

    int doAppointment(TInvoice tInvoice, HttpServletRequest request);

    int doOnekeyUpdate(String lotNo, HttpServletRequest request);

    int doUpdateDetailStatus(String oldStatus, Date dateLine, String newStatus);
}
