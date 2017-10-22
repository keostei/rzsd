package com.rzsd.wechat.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.rzsd.wechat.common.dto.MCustomInfo;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MCustomInfoMapper;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.entity.LoginUser;
import com.rzsd.wechat.exception.BussinessException;
import com.rzsd.wechat.util.DateUtil;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private MUserMapper mUserMapper;
    @Autowired
    private MCustomInfoMapper mCustomInfoMapper;

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

    @Override
    @Transactional
    public boolean doRegist(String userName, String password, String customId, String name, String address,
            String telNo, HttpServletRequest request) {
        // 用户名重复检查
        MUser selectCond = new MUser();
        selectCond.setUserName(userName);
        List<MUser> userList = mUserMapper.select(selectCond);
        if (!userList.isEmpty()) {
            throw new BussinessException("该用户名已被注册，请选用其他用户名！");
        }
        // 客户编码存在检查
        selectCond = new MUser();
        selectCond.setCustomId(customId);
        userList = mUserMapper.select(selectCond);
        if (userList.isEmpty()) {
            throw new BussinessException("您输入的客户编码不存在，请重新输入！可以在微信公众号回复查询编码进行查询。");
        }
        MUser mUser = userList.get(0);
        if (!StringUtils.isEmpty(mUser.getUserName())) {
            throw new BussinessException("您输入的客户编码已经创建过账号。");
        }
        mUser.setUserName(userName);
        mUser.setPassword(password);
        mUser.setNickName(name);
        mUser.setUpdateId(mUser.getId());
        mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
        mUserMapper.update(mUser);

        // 创建客户编码地址信息
        MCustomInfo mCustomInfo = new MCustomInfo();
        mCustomInfo.setCustomId(customId);
        mCustomInfo.setRowNo("1");
        mCustomInfo.setName(name);
        mCustomInfo.setAddress(address);
        mCustomInfo.setTelNo(telNo);
        mCustomInfo.setUpdateTime(DateUtil.getCurrentTimestamp());
        mCustomInfo.setUpdateId(mUser.getId());
        if (mCustomInfoMapper.update(mCustomInfo) == 0) {
            mCustomInfo.setCreateTime(DateUtil.getCurrentTimestamp());
            mCustomInfo.setCreateId(mUser.getId());
            mCustomInfoMapper.insert(mCustomInfo);
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setId(mUser.getId());
        loginUser.setUserName(mUser.getUserName());
        loginUser.setNickName(mUser.getNickName());
        loginUser.setWechatOpenId(mUser.getWechatOpenId());
        loginUser.setCustomId(mUser.getCustomId());
        loginUser.setUserType(mUser.getUserType());
        request.getSession().setAttribute("LOGIN_USER", loginUser);
        return false;
    }

}
