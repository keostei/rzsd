package com.rzsd.wechat.logic;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.rzsd.wechat.util.InputMessage;

public interface WechatSubscribeLogic {

	void execute(InputMessage inputMsg, HttpServletResponse response) throws IOException;
}
