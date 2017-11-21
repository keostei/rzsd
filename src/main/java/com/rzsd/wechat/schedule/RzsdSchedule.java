package com.rzsd.wechat.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rzsd.wechat.enmu.InvoiceStatus;
import com.rzsd.wechat.service.InvoiceService;
import com.rzsd.wechat.util.DateUtil;

@Component
public class RzsdSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(RzsdSchedule.class.getName());
    @Autowired
    private InvoiceService invoiceServiceImpl;

    @Scheduled(cron = "0 0 * * * ?")
    public void UpdateStatus1() {
        LOGGER.info("状态更新开始：出库---日本通关中");
        invoiceServiceImpl.doUpdateDetailStatus(InvoiceStatus.CHUKU.getCode(), DateUtil.getCurrentTimestamp(),
                InvoiceStatus.RBTGZ.getCode());
        LOGGER.info("状态更新结束：出库---日本通关中");
    }

    @Scheduled(cron = "0 5 * * * ?")
    public void UpdateStatus2() {
        LOGGER.info("状态更新开始：日本通关中---专机起飞");
        invoiceServiceImpl.doUpdateDetailStatus(InvoiceStatus.RBTGZ.getCode(), DateUtil.getCurrentTimestamp(),
                InvoiceStatus.ZJQF.getCode());
        LOGGER.info("状态更新结束：日本通关中---专机起飞");
    }

    @Scheduled(cron = "0 10 * * * ?")
    public void UpdateStatus3() {
        LOGGER.info("状态更新开始：专机起飞---清关口岸");
        invoiceServiceImpl.doUpdateDetailStatus(InvoiceStatus.ZJQF.getCode(),
                DateUtil.addDays(DateUtil.getCurrentTimestamp(), -3), InvoiceStatus.GNQGKA.getCode());
        LOGGER.info("状态更新结束：专机起飞---清关口岸");
    }

}
