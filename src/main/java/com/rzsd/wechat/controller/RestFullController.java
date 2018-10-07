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

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rzsd.wechat.annotation.WebAuth;
import com.rzsd.wechat.common.constrant.RzConst;
import com.rzsd.wechat.common.dto.BaseJsonDto;
import com.rzsd.wechat.common.dto.InvoiceData;
import com.rzsd.wechat.common.dto.MCustomInfo;
import com.rzsd.wechat.common.dto.TInvoice;
import com.rzsd.wechat.common.dto.TShopInvoice;
import com.rzsd.wechat.entity.BarCodeItem;
import com.rzsd.wechat.entity.BulkShopInvoice;
import com.rzsd.wechat.entity.InvoiceDeliver;
import com.rzsd.wechat.entity.LoginUser;
import com.rzsd.wechat.exception.BussinessException;
import com.rzsd.wechat.form.ImportDataForm;
import com.rzsd.wechat.service.InvoiceService;
import com.rzsd.wechat.service.LoginService;
import com.rzsd.wechat.service.ShopService;
import com.rzsd.wechat.service.SystemService;
import com.rzsd.wechat.service.UserService;
import com.rzsd.wechat.util.DateUtil;
import com.rzsd.wechat.util.ExcelReaderUtil;

@RestController
@RequestMapping("/rest")
public class RestFullController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestFullController.class.getName());

    @Autowired
    private InvoiceService invoiceServiceImpl;
    @Autowired
    private UserService userServiceImpl;
    @Autowired
    private SystemService systemServiceImpl;
    @Autowired
    private LoginService loginServiceImpl;
    @Autowired
    private ShopService shopServiceImpl;

    @Value("${rzsd.output.template.path}")
    private String templatePath;
    @Value("${rzsd.output.template.name}")
    private String templateName;
    @Value("${rzsd.output.file.path}")
    private String outputPath;
    @Value("${rzsd.output.result.file.name}")
    private String outputResultPath;
    @Value("${rzsd.output.file.name}")
    private String outputName;
    @Value("${rzsd.input.file.path}")
    private String inputPath;

    @RequestMapping("/login/auth")
    public BaseJsonDto auth(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        boolean authRes = loginServiceImpl.doAuth(request.getParameter("userName"), request.getParameter("password"),
                request);
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        if (loginUser != null) {
            if ("3".equals(loginUser.getUserType())) {
                result.setOptStr("/system");
            } else if ("2".equals(loginUser.getUserType())) {
                result.setOptStr("/confirm");
            } else {
                result.setOptStr("/");
            }
        }
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
            if (StringUtils.isEmpty(ExcelReaderUtil.getCellValue(row, 0))
                    && StringUtils.isEmpty(ExcelReaderUtil.getCellValue(row, 1))) {
                continue;
            }
            invoiceDeliver = new InvoiceDeliver();
            // 客户编码
            invoiceDeliver.setCustomCd(ExcelReaderUtil.getCellValue(row, 1));
            // 姓名
            invoiceDeliver.setName(ExcelReaderUtil.getCellValue(row, 2));
            // 电话
            invoiceDeliver.setTelNo(ExcelReaderUtil.getCellValue(row, 3));
            // 地址
            invoiceDeliver.setAddress(ExcelReaderUtil.getCellValue(row, 4));
            // 内部编码
            invoiceDeliver.setLogNo(ExcelReaderUtil.getCellValue(row, 5));
            // 国内快递单号
            invoiceDeliver.setTrackingNo(ExcelReaderUtil.getCellValue(row, 6));
            // 重量
            invoiceDeliver.setStrWeight(ExcelReaderUtil.getCellValue(row, 7));
            // 单价
            invoiceDeliver.setStrPrice(ExcelReaderUtil.getCellValue(row, 8));
            // 变更国内快递单号
            invoiceDeliver.setNewTrackingNo(ExcelReaderUtil.getCellValue(row, 9));

            invoiceDeliverLst.add(invoiceDeliver);
            rowNum++;
        }

        //
        invoiceDeliverLst = invoiceServiceImpl.checkImportInvoiceData(invoiceDeliverLst, request);
        boolean hasError = false;
        for (InvoiceDeliver dto : invoiceDeliverLst) {
            if (!StringUtils.isEmpty(dto.getImportResult())) {
                hasError = true;
                break;
            }
        }
        if (hasError) {
            result.setFail(true);
            String resultPath = saveResult(invoiceDeliverLst, xssfWorkbook, sheet, row);
            result.setOptStr(resultPath);
            return result;
        }

        invoiceDeliverLst = invoiceServiceImpl.importInvoiceData(invoiceDeliverLst, request);
        String resultPath = saveResult(invoiceDeliverLst, xssfWorkbook, sheet, row);
        result.setOptStr(resultPath);
        result.setFail(false);
        return result;
    }

    private String saveResult(List<InvoiceDeliver> invoiceDeliverLst, XSSFWorkbook xssfWorkbook, XSSFSheet sheet,
            XSSFRow row) {
        int rowNo = 1;
        for (InvoiceDeliver dto : invoiceDeliverLst) {
            rowNo++;
            row = sheet.getRow(rowNo);
            if (row == null) {
                row = sheet.createRow(rowNo);
                row.createCell(0).setCellValue(String.valueOf(rowNo - 1));
                row.createCell(1).setCellValue(StringUtils.isEmpty(dto.getCustomCd()) ? "" : dto.getCustomCd());
                row.createCell(2).setCellValue(StringUtils.isEmpty(dto.getName()) ? "" : dto.getName());
                row.createCell(3).setCellValue(StringUtils.isEmpty(dto.getTelNo()) ? "" : dto.getTelNo());
                row.createCell(4).setCellValue(StringUtils.isEmpty(dto.getAddress()) ? "" : dto.getAddress());
                row.createCell(5).setCellValue(StringUtils.isEmpty(dto.getLogNo()) ? "" : dto.getLogNo());
                row.createCell(6).setCellValue(StringUtils.isEmpty(dto.getTrackingNo()) ? "" : dto.getTrackingNo());
                row.createCell(7).setCellValue(StringUtils.isEmpty(dto.getStrWeight()) ? "" : dto.getStrWeight());
                row.createCell(8).setCellValue(StringUtils.isEmpty(dto.getStrPrice()) ? "" : dto.getStrPrice());
                row.createCell(9)
                        .setCellValue(StringUtils.isEmpty(dto.getNewTrackingNo()) ? "" : dto.getNewTrackingNo());
                row.createCell(10)
                        .setCellValue(StringUtils.isEmpty(dto.getImportResult()) ? "" : dto.getImportResult());
            } else {
                row.getCell(0).setCellValue(String.valueOf(rowNo - 1));
                row.getCell(1).setCellValue(StringUtils.isEmpty(dto.getCustomCd()) ? "" : dto.getCustomCd());
                row.getCell(2).setCellValue(StringUtils.isEmpty(dto.getName()) ? "" : dto.getName());
                row.getCell(3).setCellValue(StringUtils.isEmpty(dto.getTelNo()) ? "" : dto.getTelNo());
                row.getCell(4).setCellValue(StringUtils.isEmpty(dto.getAddress()) ? "" : dto.getAddress());
                row.getCell(5).setCellValue(StringUtils.isEmpty(dto.getLogNo()) ? "" : dto.getLogNo());
                row.getCell(6).setCellValue(StringUtils.isEmpty(dto.getTrackingNo()) ? "" : dto.getTrackingNo());
                row.getCell(7).setCellValue(StringUtils.isEmpty(dto.getStrWeight()) ? "" : dto.getStrWeight());
                row.getCell(8).setCellValue(StringUtils.isEmpty(dto.getStrPrice()) ? "" : dto.getStrPrice());
                row.getCell(9).setCellValue(StringUtils.isEmpty(dto.getNewTrackingNo()) ? "" : dto.getNewTrackingNo());
                row.getCell(10).setCellValue(StringUtils.isEmpty(dto.getImportResult()) ? "" : dto.getImportResult());
            }
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
            for (int i = 0; i < 10; i++) {
                if (row.getCell(i) == null) {
                    row.createCell(i).setCellStyle(style);
                } else {
                    row.getCell(i).setCellStyle(style);
                }
            }
        }

        xssfWorkbook.setPrintArea(0, 0, 10, 0, rowNo);
        String newFilePath = outputPath + MessageFormat.format(outputResultPath,
                DateUtil.format(DateUtil.getCurrentTimestamp(), "yyyyMMddHHmmss"));
        try {
            File newFile = new File(newFilePath);
            FileOutputStream fos = new FileOutputStream(newFile);
            xssfWorkbook.setForceFormulaRecalculation(true);
            xssfWorkbook.write(fos);
            xssfWorkbook.close();
            fos.close();
        } catch (IOException e) {
            LOGGER.error("结果文件保存失败。", e);
        }
        return newFilePath;
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
                row.createCell(0).setCellValue(String.valueOf(rowNo - 1));
                row.createCell(1).setCellValue(formatter.format(invoice.getInvoiceDate()));
                row.createCell(2).setCellValue(invoice.getCustomCd());
                row.createCell(3).setCellValue(StringUtils.isEmpty(invoice.getName()) ? "" : invoice.getName());
                row.createCell(4).setCellValue(StringUtils.isEmpty(invoice.getTelNo()) ? "" : invoice.getTelNo());
                row.createCell(5).setCellValue(StringUtils.isEmpty(invoice.getAddress()) ? "" : invoice.getAddress());
                // row.createCell(6).setCellValue("4：已出库");
            } else {
                row.getCell(0).setCellValue(String.valueOf(rowNo - 1));
                row.getCell(1).setCellValue(formatter.format(invoice.getInvoiceDate()));
                row.getCell(2).setCellValue(invoice.getCustomCd());
                row.getCell(3).setCellValue(StringUtils.isEmpty(invoice.getName()) ? "" : invoice.getName());
                row.getCell(4).setCellValue(StringUtils.isEmpty(invoice.getTelNo()) ? "" : invoice.getTelNo());
                row.getCell(5).setCellValue(StringUtils.isEmpty(invoice.getAddress()) ? "" : invoice.getAddress());
                // row.getCell(6).setCellValue("4：已出库");
            }
            for (int i = 0; i < 6; i++) {
                if (row.getCell(i) == null) {
                    row.createCell(i).setCellStyle(style);
                } else {
                    row.getCell(i).setCellStyle(style);
                }
            }
        }

        xssfWorkbook.setPrintArea(0, 0, 5, 0, rowNo);
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

    @RequestMapping("/shop/upload_invoice")
    public BaseJsonDto uploadInvoice(@RequestBody BulkShopInvoice bulkShopInvoice) {
        BaseJsonDto result = new BaseJsonDto();
        List<TShopInvoice> tShopInvoiceLst = new ArrayList<>();
        TShopInvoice t;
        String msg = "";
        for (BarCodeItem bci : bulkShopInvoice.getBarCodeItemLst()) {
            if (StringUtils.isEmpty(bci.getResult())) {
                continue;
            }
            if (!("EAN13".equals(bci.getType()) || "EAN8".equals(bci.getType()))) {
                continue;
            }
            t = new TShopInvoice();
            t.setShopId(new BigInteger(String.valueOf(bulkShopInvoice.getShopId())));
            t.setBarcode(bci.getResult());
            t.setCount(new BigInteger(String.valueOf(bci.getCnt())));
            if ("3".equals(bulkShopInvoice.getType())) {
                t.setType("1");
                t.setReason(bulkShopInvoice.getReason() + "-入库");
            } else {
                t.setType(bulkShopInvoice.getType());
                t.setReason(bulkShopInvoice.getReason());
            }
            t.setCreateId(RzConst.SYS_ADMIN_ID);
            t.setUpdateId(RzConst.SYS_ADMIN_ID);
            tShopInvoiceLst.add(t);

            // 直接出库的时候，自动出库
            if ("3".equals(bulkShopInvoice.getType())) {
                t = new TShopInvoice();
                t.setShopId(new BigInteger(String.valueOf(bulkShopInvoice.getShopId())));
                t.setBarcode(bci.getResult());
                t.setCount(new BigInteger(String.valueOf(bci.getCnt())));
                t.setType("2");
                t.setReason(bulkShopInvoice.getReason() + "-出库");
                t.setCreateId(RzConst.SYS_ADMIN_ID);
                t.setUpdateId(RzConst.SYS_ADMIN_ID);
                tShopInvoiceLst.add(t);
            }
        }
        int insertCnt = shopServiceImpl.bulkInsertShopInvoice(tShopInvoiceLst);

        result.setOptStr(
                msg + "本次成功上传" + insertCnt + "件" + ("1".equals(bulkShopInvoice.getType()) ? "入库" : "出库") + "记录。");
        result.setFail(false);
        return result;
    }

    @RequestMapping("/shop/update_item_name")
    public BaseJsonDto updateItemName(HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        shopServiceImpl.updateItemName(new BigInteger(request.getParameter("shopId")), request.getParameter("barCode"),
                request.getParameter("itemName"));
        result.setFail(false);
        return result;
    }

    @RequestMapping("/shop/del_shop_item_invoice")
    public BaseJsonDto delShopItemInvoice(HttpServletRequest request, HttpServletResponse response) {
        BaseJsonDto result = new BaseJsonDto();
        shopServiceImpl.delShopInvoice(new BigInteger(request.getParameter("shopInvoiceId")));
        result.setFail(false);
        return result;
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
