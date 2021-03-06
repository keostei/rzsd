package com.rzsd.wechat.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rzsd.wechat.annotation.WebAuth;
import com.rzsd.wechat.common.dto.TInvoice;
import com.rzsd.wechat.common.dto.TShopItem;
import com.rzsd.wechat.common.dto.TShopSum;
import com.rzsd.wechat.service.InvoiceService;
import com.rzsd.wechat.service.ShopService;
import com.rzsd.wechat.service.SystemService;
import com.rzsd.wechat.service.UserService;
import com.rzsd.wechat.util.DateUtil;

@Controller
@RequestMapping("/")
public class WebController {

    @Autowired
    private InvoiceService invoiceServiceImpl;
    @Autowired
    private UserService userServiceImpl;
    @Autowired
    private ShopService shopServiceImpl;
    @Autowired
    private SystemService systemServiceImpl;
    @Value("${rzsd.output.file.path}")
    private String outputPath;
    @Value("${rzsd.output.file.name}")
    private String outputName;

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

    @RequestMapping(value = "data")
    @WebAuth
    public String initData(Model model, HttpServletRequest request) {
        System.out.println(request.getHeader("USER-AGENT"));
        model.addAttribute("condInvoiceDateFrom",
                DateUtil.format(DateUtil.addDays(DateUtil.getCurrentTimestamp(), -14)));
        model.addAttribute("condInvoiceDateTo", DateUtil.format(DateUtil.getCurrentTimestamp()));
        return "data";
    }

    @RequestMapping(value = "system")
    @WebAuth(lever = { "3" })
    public String initSystemSetting(Model model, HttpServletRequest request) {
        model.addAttribute("currentRate", systemServiceImpl.getSysParamByKey("JPN_CNY_RATE").getParamValue());
        return "system";
    }

    @RequestMapping("system/invoice_download")
    public void downloadInvoice(HttpServletResponse response,
            @RequestParam(value = "path", required = false) String path) {
        try {
            String newFilePath = outputPath
                    + MessageFormat.format(outputName, DateUtil.format(DateUtil.getCurrentTimestamp(), "yyyyMMdd"));
            if (!StringUtils.isEmpty(path)) {
                newFilePath = path;
            }
            // path是指欲下载的文件的路径。
            File file = new File(newFilePath);
            // 取得文件名。
            String filename = file.getName();
            // String strName = new String(filename.getBytes("UTF-8"), "UTF-8");
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String(filename.getBytes("UTF-8"), "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    @RequestMapping(value = "shop/item_list")
    public String shopItemList(@RequestParam(name = "shopId") String shopId,
            @RequestParam(name = "itemName", required = false) String itemName, Model model,
            HttpServletRequest request) {

        TShopItem tShopItemCond = new TShopItem();
        tShopItemCond.setShopId(new BigInteger(shopId));
        if (!StringUtils.isEmpty(itemName)) {
            tShopItemCond.setItemName(itemName);
        }
        model.addAttribute("shopItemLst", shopServiceImpl.selectShopItemWithItemInfo(tShopItemCond));
        model.addAttribute("shopId", shopId);
        model.addAttribute("itemName", itemName);
        return "item_list";
    }

    @RequestMapping(value = "shop/item_detail")
    public String shopItemDetail(@RequestParam(name = "shopId") String shopId,
            @RequestParam(name = "barcode") String barcode, Model model, HttpServletRequest request) {

        TShopItem tShopItemCond = new TShopItem();
        tShopItemCond.setShopId(new BigInteger(shopId));
        tShopItemCond.setBarcode(barcode);
        model.addAttribute("shopItem", shopServiceImpl.selectShopItemWithItemInfo(tShopItemCond).get(0));
        model.addAttribute("shopInvoiceLst", shopServiceImpl.selectShopInvoiceByBarcode(shopId, barcode));
        model.addAttribute("shopId", shopId);
        return "item_detail";
    }

    @RequestMapping(value = "shop/item_sum")
    public String shopItemSum(@RequestParam(name = "shopId") String shopId,
            @RequestParam(name = "dateFrom", required = false) String dateFrom,
            @RequestParam(name = "dateTo", required = false) String dateTo, Model model, HttpServletRequest request) {

        TShopSum tShopSumCond = new TShopSum();
        tShopSumCond.setShopId(new BigInteger(shopId));

        if (StringUtils.isEmpty(dateFrom)) {
            dateTo = DateUtil.format(DateUtil.getCurrentTimestamp());
            tShopSumCond.setDateTo(DateUtil.addDays(DateUtil.parse(dateTo), 1));
            tShopSumCond.setDateFrom(DateUtil.addDays(tShopSumCond.getDateTo(), -30));
            dateFrom = DateUtil.format(tShopSumCond.getDateFrom());
        } else {
            tShopSumCond.setDateTo(DateUtil.addDays(DateUtil.parse(dateTo), 1));
            tShopSumCond.setDateFrom(DateUtil.parse(dateFrom));
        }

        List<TShopSum> tShopSumLst = shopServiceImpl.selectShopSum(tShopSumCond);
        BigInteger totalCnt = new BigInteger("0");
        BigDecimal totalPriceRmb = BigDecimal.ZERO;
        BigDecimal totalPriceShow = BigDecimal.ZERO;
        for (TShopSum tss : tShopSumLst) {
            totalCnt = totalCnt.add(tss.getSumCnt());
            totalPriceRmb = totalPriceRmb.add(tss.getPriceRmbSum());
            totalPriceShow = totalPriceShow.add(tss.getPriceShowSum());
        }
        model.addAttribute("shopItemSum", tShopSumLst);
        model.addAttribute("shopId", shopId);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("totalPriceRmb", totalPriceRmb);
        model.addAttribute("totalPriceShow", totalPriceShow);
        return "item_sum";
    }

    @RequestMapping(value = "shop/xm")
    public String pubShopItemList(@RequestParam(name = "shopId", required = false) String shopId,
            @RequestParam(name = "itemName", required = false) String itemName, Model model,
            HttpServletRequest request) {
        shopId = "2";
        TShopItem tShopItemCond = new TShopItem();
        tShopItemCond.setShopId(new BigInteger(shopId));
        if (!StringUtils.isEmpty(itemName)) {
            tShopItemCond.setItemName(itemName);
        }
        tShopItemCond.setLimitCnt(30L);
        model.addAttribute("shopItemLst", shopServiceImpl.selectShopItemWithItemInfo(tShopItemCond));
        model.addAttribute("itemName", itemName);
        return "xmshop_list";
    }
}
