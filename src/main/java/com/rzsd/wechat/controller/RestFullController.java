package com.rzsd.wechat.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rzsd.wechat.annotation.WebAuth;
import com.rzsd.wechat.common.dto.BaseJsonDto;
import com.rzsd.wechat.common.dto.InvoiceData;
import com.rzsd.wechat.common.dto.MCustomInfo;
import com.rzsd.wechat.common.dto.TInvoice;
import com.rzsd.wechat.entity.InvoiceDeliver;
import com.rzsd.wechat.exception.BussinessException;
import com.rzsd.wechat.form.ImportDataForm;
import com.rzsd.wechat.logic.WechatCustomIdLogic;
import com.rzsd.wechat.logic.WechatHelpLogic;
import com.rzsd.wechat.logic.WechatInvoiceLogic;
import com.rzsd.wechat.logic.WechatUserLogic;
import com.rzsd.wechat.service.InvoiceService;
import com.rzsd.wechat.service.LoginService;
import com.rzsd.wechat.service.SystemService;
import com.rzsd.wechat.service.UserService;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.ExcelReaderUtil;
import com.rzsd.wechat.util.InputMessage;

@RestController
@RequestMapping("/rest")
public class RestFullController {

    @Autowired
    private WechatInvoiceLogic wechatInvoiceLogicImpl;
    @Autowired
    private WechatUserLogic wechatUserLogicImpl;
    @Autowired
    private WechatHelpLogic wechatHelpLogicImpl;
    @Autowired
    private WechatCustomIdLogic wechatCustomIdLogicImpl;
    @Autowired
    private InvoiceService invoiceServiceImpl;
    @Autowired
    private UserService userServiceImpl;
    @Autowired
    private SystemService systemServiceImpl;
    @Autowired
    private LoginService loginServiceImpl;

    @Value("${rzsd.output.template.path}")
    private String templatePath;
    @Value("${rzsd.output.template.name}")
    private String templateName;
    @Value("${rzsd.output.file.path}")
    private String outputPath;
    @Value("${rzsd.output.file.name}")
    private String outputName;
    @Value("${rzsd.input.file.path}")
    private String inputPath;

    @RequestMapping("/login/auth")
    public BaseJsonDto auth(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        boolean authRes = loginServiceImpl.doAuth(request.getParameter("userName"), request.getParameter("password"),
                request);
        result.setFail(!authRes);
        return result;
    }

    @RequestMapping("/login/regist")
    public BaseJsonDto regist(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String customId = request.getParameter("customId");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String telNo = request.getParameter("telNo");
        boolean authRes = loginServiceImpl.doRegist(userName, password, customId, name, address, telNo, request);
        result.setFail(!authRes);
        return result;
    }

    @RequestMapping("/custom/appoint")
    @WebAuth
    public BaseJsonDto appointInfo(HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        TInvoice tInvoice = new TInvoice();
        tInvoice.setInvoiceDate(DateUtil.parse(request.getParameter("invoiceDate")));
        tInvoice.setInvoiceTimeCd(request.getParameter("invoiceTimeCd"));
        tInvoice.setInvoideAddress(request.getParameter("invoideAddress"));
        tInvoice.setWeight(new BigDecimal(request.getParameter("weight")));
        tInvoice.setCustomCd(request.getParameter("customCd"));
        tInvoice.setName(request.getParameter("name"));
        tInvoice.setTelNo(request.getParameter("telNo"));
        tInvoice.setAddress(request.getParameter("address"));
        tInvoice.setInvoiceRequirement(request.getParameter("invoiceRequirement"));
        invoiceServiceImpl.doAppointment(tInvoice, request);
        return result;
    }

    @RequestMapping("/custom/edit")
    @WebAuth
    public BaseJsonDto editCustom(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        MCustomInfo mCustomInfo = new MCustomInfo();
        mCustomInfo.setCustomId(request.getParameter("customId"));
        mCustomInfo.setRowNo(request.getParameter("rowNo"));
        mCustomInfo.setName(request.getParameter("name"));
        mCustomInfo.setTelNo(request.getParameter("telNo"));
        mCustomInfo.setAddress(request.getParameter("address"));
        userServiceImpl.editCustomInfo(mCustomInfo, request);
        return result;
    }

    @RequestMapping("/invoice/confirm")
    @WebAuth(lever = { "2", "3" })
    public BaseJsonDto confirm(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        invoiceServiceImpl.doConfirm(request.getParameter("invoiceId"), request);
        result.setFail(false);
        return result;
    }

    @RequestMapping("/invoice/reject")
    @WebAuth(lever = { "2", "3" })
    public BaseJsonDto reject(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        invoiceServiceImpl.doReject(request.getParameter("invoiceId"), request);
        result.setFail(false);
        return result;
    }

    @RequestMapping("/system/setrate")
    @WebAuth(lever = { "3" })
    public BaseJsonDto setRate(HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        systemServiceImpl.updateSysParamByKey("JPN_CNY_RATE", request.getParameter("value"), request);
        result.setFail(false);
        return result;
    }

    @RequestMapping("/system/import")
    @WebAuth(lever = { "3" })
    public BaseJsonDto importData(ImportDataForm form, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // inputPath
        String filePath = inputPath + form.getUploadFile().getOriginalFilename();
        File inputFile = new File(filePath);
        OutputStream os = new FileOutputStream(inputFile);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        InputStream ins = form.getUploadFile().getInputStream();
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        BaseJsonDto result = new BaseJsonDto();
        XSSFWorkbook xssfWorkbook = ExcelReaderUtil.getBook(filePath);
        XSSFSheet sheet = xssfWorkbook.getSheet("TEMPLATE");
        XSSFRow row = null;
        int rowNum = 2;
        List<InvoiceDeliver> invoiceDeliverLst = new ArrayList<>();
        InvoiceDeliver invoiceDeliver;
        while (true) {
            row = sheet.getRow(rowNum);
            if (row == null) {
                break;
            }
            invoiceDeliver = new InvoiceDeliver();
            invoiceDeliver.setInvoiceId(new BigInteger(ExcelReaderUtil.getCellValue(row, 0)));
            invoiceDeliver.setInvoiceStatusName(ExcelReaderUtil.getCellValue(row, 6));
            invoiceDeliver.setLogNo(ExcelReaderUtil.getCellValue(row, 7));

            invoiceDeliver.setTrackingNo(ExcelReaderUtil.getCellValue(row, 8));
            invoiceDeliver.setWeight(new BigDecimal(ExcelReaderUtil.getCellValue(row, 9)));
            invoiceDeliverLst.add(invoiceDeliver);
            rowNum++;
        }
        invoiceServiceImpl.importInvoiceData(invoiceDeliverLst, request);
        result.setFail(false);
        return result;
    }

    @RequestMapping("/system/search_data")
    @WebAuth(lever = { "3" })
    public List<InvoiceData> searchData(HttpServletRequest request, HttpServletResponse response) {
        String condCustomCd = request.getParameter("condCustomCd");
        String condName = request.getParameter("condName");
        String condInvoiceStatus = request.getParameter("condInvoiceStatus");
        String condInvoiceDateFrom = request.getParameter("condInvoiceDateFrom");
        String condInvoiceDateTo = request.getParameter("condInvoiceDateTo");
        InvoiceData invoiceDataCond = new InvoiceData();
        invoiceDataCond.setCondCustomCd(condCustomCd);
        invoiceDataCond.setCondName(condName);
        invoiceDataCond.setCondInvoiceStatus(condInvoiceStatus);
        invoiceDataCond.setCondInvoiceDateFrom(condInvoiceDateFrom);
        invoiceDataCond.setCondInvoiceDateTo(condInvoiceDateTo);
        List<InvoiceData> lst = invoiceServiceImpl.searchInvoiceData(invoiceDataCond);
        return lst;
    }

    @RequestMapping("/system/edit_data")
    @WebAuth(lever = { "3" })
    public BaseJsonDto editData(HttpServletRequest request, HttpServletResponse response) {
        String invoiceId = request.getParameter("invoiceId");
        String customCd = request.getParameter("customCd");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String invoiceStatus = request.getParameter("invoiceStatus");
        String totalWeight = request.getParameter("totalWeight");
        String invoiceAmountJpy = request.getParameter("invoiceAmountJpy");
        String dispLotNo = request.getParameter("dispLotNo");
        String dispTrackingNo = request.getParameter("dispTrackingNo");
        String dispWeight = request.getParameter("dispWeight");
        InvoiceData invoiceDataCond = new InvoiceData();
        invoiceDataCond.setInvoiceId(invoiceId);
        invoiceDataCond.setCustomCd(customCd);
        invoiceDataCond.setName(name);
        invoiceDataCond.setAddress(address);
        invoiceDataCond.setInvoiceStatus(invoiceStatus);
        if (!StringUtils.isEmpty(totalWeight)) {
            invoiceDataCond.setTotalWeight(new BigDecimal(totalWeight));
        }
        invoiceDataCond.setInvoiceAmountJpy(invoiceAmountJpy);
        invoiceDataCond.setDispLotNo(dispLotNo);
        invoiceDataCond.setDispTrackingNo(dispTrackingNo);
        invoiceDataCond.setDispWeight(dispWeight);
        invoiceServiceImpl.editInvoiceData(invoiceDataCond, request);
        BaseJsonDto result = new BaseJsonDto();
        result.setFail(false);
        return result;
    }

    @RequestMapping("/system/onekey_update")
    @WebAuth(lever = { "3" })
    public BaseJsonDto oneKeyUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BaseJsonDto result = new BaseJsonDto();
        invoiceServiceImpl.doOnekeyUpdate(request.getParameter("lotNo"), request);
        result.setFail(false);
        return result;
    }

    @RequestMapping("/system/invoice_output")
    @WebAuth(lever = { "3" })
    public BaseJsonDto downLoadInvoiceOutput(Model model, HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {

        FileInputStream ins = new FileInputStream(templatePath + templateName);
        String newFilePath = outputPath
                + MessageFormat.format(outputName, DateUtil.format(DateUtil.getCurrentTimestamp(), "yyyyMMdd"));
        File newFile = new File(newFilePath);
        if (newFile.exists()) {
            newFile.delete();
        }
        FileOutputStream out = new FileOutputStream(newFile);
        byte[] b = new byte[1024];
        int n = 0;
        while ((n = ins.read(b)) != -1) {
            out.write(b, 0, n);
        }
        ins.close();
        out.close();

        List<TInvoice> invoiceLst = invoiceServiceImpl.getInvoiceOutputInfo(request);
        XSSFWorkbook xssfWorkbook = ExcelReaderUtil.getBook(newFilePath);
        XSSFSheet sheet = xssfWorkbook.getSheet("TEMPLATE");

        XSSFCellStyle style = xssfWorkbook.createCellStyle();
        // 设置边框样式
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // 设置边框颜色
        style.setTopBorderColor(HSSFColor.BLACK.index);
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        style.setRightBorderColor(HSSFColor.BLACK.index);

        int rowNo = 1;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (TInvoice invoice : invoiceLst) {
            rowNo++;
            XSSFRow row = sheet.getRow(rowNo);
            if (row == null) {
                row = sheet.createRow(rowNo);
                row.createCell(0).setCellValue(String.valueOf(invoice.getInvoiceId()));
                row.createCell(1).setCellValue(formatter.format(invoice.getInvoiceDate()));
                row.createCell(2).setCellValue(invoice.getCustomCd());
                row.createCell(3).setCellValue(invoice.getName());
                row.createCell(4).setCellValue(invoice.getTelNo());
                row.createCell(5).setCellValue(invoice.getAddress());
                row.createCell(6).setCellValue("4：已出库");
            } else {
                row.getCell(0).setCellValue(String.valueOf(invoice.getInvoiceId()));
                row.getCell(1).setCellValue(formatter.format(invoice.getInvoiceDate()));
                row.getCell(2).setCellValue(invoice.getCustomCd());
                row.getCell(3).setCellValue(invoice.getName());
                row.getCell(4).setCellValue(invoice.getTelNo());
                row.getCell(5).setCellValue(invoice.getAddress());
                row.getCell(6).setCellValue("4：已出库");
            }
            for (int i = 0; i < 10; i++) {
                if (row.getCell(i) == null) {
                    row.createCell(i).setCellStyle(style);
                } else {
                    row.getCell(i).setCellStyle(style);
                }
            }
        }

        xssfWorkbook.setPrintArea(0, 0, 9, 0, rowNo);
        try {
            FileOutputStream fos = new FileOutputStream(newFile);
            xssfWorkbook.setForceFormulaRecalculation(true);
            xssfWorkbook.write(fos);
            xssfWorkbook.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BaseJsonDto result = new BaseJsonDto();
        result.setFail(false);
        return result;
    }

    @RequestMapping("test")
    @WebAuth
    public void test(Model model, HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {

        InputMessage inputMsg = new InputMessage();
        inputMsg.setFromUserName("openId-test=001");
        inputMsg.setToUserName("KEOSIMAGE");
        inputMsg.setContent("创建账号 seuzc SEUZHENGCHAO 001315");
        // wechatCustomIdLogicImpl.updateCustomInfo(inputMsg, response);
        // wechatInvoiceLogicImpl.createInvoice(inputMsg, response);
        // wechatInvoiceLogicImpl.queryInvoice(inputMsg, response);
        wechatUserLogicImpl.createUser(inputMsg, response);
        // wechatHelpLogicImpl.doAppointment(inputMsg, response);
    }

    @ExceptionHandler(BussinessException.class) // (1)
    @ResponseStatus(value = HttpStatus.CONFLICT) // (2)
    public BaseJsonDto handleBindException(BussinessException e) { // (4)
        BaseJsonDto result = new BaseJsonDto();
        result.setFail(false);
        result.setMessage(e.getMessage());
        return result;
    }

}
