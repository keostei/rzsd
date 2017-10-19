package com.rzsd.wechat.service;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

    boolean doAuth(String userName, String password, HttpServletRequest request);
}
