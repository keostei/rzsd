package com.rzsd.wechat.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.entity.LoginUser;
import com.rzsd.wechat.exception.BussinessException;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private MUserMapper mUserMapper;

    @Override
    public boolean doAuth(String userName, String password, HttpServletRequest request) {
        MUser selectCond = new MUser();
        selectCond.setUserName(userName);
        selectCond.setDelFlg("0");
        List<MUser> mUserList = mUserMapper.select(selectCond);
        if (mUserList.isEmpty()) {
            throw new BussinessException("用户名不存在或者密码不匹配，请重新输入！");
        }
        if (!mUserList.get(0).getPassword().equalsIgnoreCase(password)) {
            throw new BussinessException("用户名不存在或者密码不匹配，请重新输入！");
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setId(mUserList.get(0).getId());
        loginUser.setUserName(mUserList.get(0).getUserName());
        loginUser.setNickName(mUserList.get(0).getNickName());
        loginUser.setWechatOpenId(mUserList.get(0).getWechatOpenId());
        loginUser.setCustomId(mUserList.get(0).getCustomId());
        loginUser.setUserType(mUserList.get(0).getUserType());
        request.getSession().setAttribute("LOGIN_USER", loginUser);
        return true;
    }

}
