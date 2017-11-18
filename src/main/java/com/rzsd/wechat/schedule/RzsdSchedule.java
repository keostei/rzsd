package com.rzsd.wechat.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rzsd.wechat.enmu.InvoiceDetailStatus;
import com.rzsd.wechat.service.InvoiceService;

@Component
public class RzsdSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(RzsdSchedule.class.getName());
    @Autowired
    private InvoiceService invoiceServiceImpl;

    @Scheduled(cron = "0 0 * * * ?")
    public void UpdateStatus1() {
        LOGGER.info("状态更新开始：出库包裹---日本海关仓库");
        invoiceServiceImpl.doUpdateDetailStatus(InvoiceDetailStatus.CHUKU.getCode(),
                InvoiceDetailStatus.RBHGCK.getCode());
        LOGGER.info("状态更新结束：出库包裹---日本海关仓库");
    }

    @Scheduled(cron = "0 5 * * * ?")
    public void UpdateStatus2() {
        LOGGER.info("状态更新开始：日本海关仓库---日本海关清关完成");
        invoiceServiceImpl.doUpdateDetailStatus(InvoiceDetailStatus.RBHGCK.getCode(),
                InvoiceDetailStatus.RBHGQGWC.getCode());
        LOGGER.info("状态更新结束：日本海关仓库---日本海关清关完成");
    }

    @Scheduled(cron = "0 10 * * * ?")
    public void UpdateStatus3() {
        LOGGER.info("状态更新开始：日本海关清关完成---转机起飞");
        invoiceServiceImpl.doUpdateDetailStatus(InvoiceDetailStatus.RBHGQGWC.getCode(),
                InvoiceDetailStatus.ZJQF.getCode());
        LOGGER.info("状态更新结束：日本海关清关完成---转机起飞");
    }

    @Scheduled(cron = "0 15 * * * ?")
    public void UpdateStatus4() {
        LOGGER.info("状态更新开始：转机起飞---到达国内海关仓库");
        invoiceServiceImpl.doUpdateDetailStatus(InvoiceDetailStatus.ZJQF.getCode(),
                InvoiceDetailStatus.GNHGCK.getCode());
        LOGGER.info("状态更新结束：转机起飞---到达国内海关仓库");
    }
}
