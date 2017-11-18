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

    @Scheduled(cron = "0 37 3 * * ?")
    public void UpdateStatus() {
        LOGGER.info("状态更新开始：出库包裹---日本海关仓库");
        invoiceServiceImpl.doUpdateDetailStatus(InvoiceDetailStatus.CHUKU.getCode(),
                InvoiceDetailStatus.RBHGCK.getCode());
        LOGGER.info("状态更新结束：出库包裹---日本海关仓库");
    }
}
