package com.rzsd.wechat.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface WechatCmdService {
	
	void acceptMessage(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
