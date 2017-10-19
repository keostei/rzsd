package com.rzsd.wechat.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rzsd.wechat.common.dto.MCustomInfo;

public interface UserService {

    List<MCustomInfo> getPersonCustomInfo(HttpServletRequest request);

    int editCustomInfo(MCustomInfo mCustomInfo, HttpServletRequest request);
}
