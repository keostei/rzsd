package com.rzsd.wechat.service;

import javax.servlet.http.HttpServletRequest;

import com.rzsd.wechat.common.dto.MSysParam;

public interface SystemService {
    MSysParam getSysParamByKey(String key);

    int updateSysParamByKey(String key, String value, HttpServletRequest request);
}
