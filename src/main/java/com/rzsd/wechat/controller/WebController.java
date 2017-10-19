package com.rzsd.wechat.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rzsd.wechat.annotation.WebAuth;
import com.rzsd.wechat.common.dto.TInvoice;
import com.rzsd.wechat.service.InvoiceService;
import com.rzsd.wechat.service.SystemService;
import com.rzsd.wechat.service.UserService;

@Controller
@RequestMapping("/")
public class WebController {

    @Autowired
    private InvoiceService invoiceServiceImpl;
    @Autowired
    private UserService userServiceImpl;
    @Autowired
    private SystemService systemServiceImpl;

    @RequestMapping
    @WebAuth
    public String index(Model model, HttpServletRequest request) {
        System.out.println(request.getHeader("USER-AGENT"));
        prepareIndex(model, request);
        return "index";
    }

    @RequestMapping(value = "{pageurl}")
    @WebAuth
    public String showPage(@PathVariable("pageurl") String pageurl, Model model, HttpServletRequest request) {
        System.out.println(request.getHeader("USER-AGENT"));
        return "" + pageurl;
    }

    @RequestMapping(value = "login")
    public String login(Model model, HttpServletRequest request) {
        System.out.println(request.getHeader("USER-AGENT"));
        return "login";
    }

    @RequestMapping(value = "index")
    @WebAuth
    public String indexPage(Model model, HttpServletRequest request) {
        System.out.println(request.getHeader("USER-AGENT"));
        prepareIndex(model, request);
        return "index";
    }

    @RequestMapping(value = "detail")
    @WebAuth
    public String showDetail(@RequestParam(name = "invoiceId") String invoiceId, Model model,
            HttpServletRequest request) {
        System.out.println(request.getHeader("USER-AGENT"));

        TInvoice invoice = invoiceServiceImpl.getInvoiceById(invoiceId, request);
        if (invoice != null) {
            model.addAttribute("invoice", invoice);
            model.addAttribute("invoiceDetailList", invoiceServiceImpl.getInvoiceDetailById(invoiceId, request));
        }

        return "detail";
    }

    private void prepareIndex(Model model, HttpServletRequest request) {
        model.addAttribute("invoiceLst", invoiceServiceImpl.getPersonalInvoice(request));
        model.addAttribute("customInfoLst", userServiceImpl.getPersonCustomInfo(request));
    }

    @RequestMapping(value = "confirm")
    @WebAuth(lever = { "2", "3" })
    public String initConfirm(Model model, HttpServletRequest request) {
        model.addAttribute("invoiceLst", invoiceServiceImpl.getConfirmLst());
        return "confirm";
    }

    @RequestMapping(value = "system")
    @WebAuth(lever = { "3" })
    public String initSystemSetting(Model model, HttpServletRequest request) {
        model.addAttribute("currentRate", systemServiceImpl.getSysParamByKey("JPN_CNY_RATE").getParamValue());
        return "system";
    }

    @RequestMapping(value = "import")
    @WebAuth(lever = { "3" })
    public String initBackImport(Model model, HttpServletRequest request) {
        return "import";
    }

    @RequestMapping(value = "regist.html")
    public String regist(Model model, HttpServletRequest request) {
        System.out.println(request.getHeader("USER-AGENT"));
        return "regist";
    }

}
