package com.rzsd.wechat.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rzsd.wechat.common.dto.MCustomInfo;
import com.rzsd.wechat.common.mapper.MCustomInfoMapper;
import com.rzsd.wechat.entity.LoginUser;
import com.rzsd.wechat.util.DateUtil;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MCustomInfoMapper mCustomInfoMapper;

    @Override
    @Transactional
    public List<MCustomInfo> getPersonCustomInfo(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        MCustomInfo selectCond = new MCustomInfo();
        selectCond.setDelFlg("0");
        selectCond.setCustomId(loginUser.getCustomId());
        List<MCustomInfo> lst = mCustomInfoMapper.select(selectCond);
        if (lst.size() < 3) {
            for (int i = 0; i < 3 - lst.size(); i++) {
                MCustomInfo mCustomInfo = new MCustomInfo();
                mCustomInfo.setCustomId(loginUser.getCustomId());
                mCustomInfo.setRowNo(String.valueOf(lst.size() + i + 1));
                lst.add(mCustomInfo);
            }
        }
        return lst;
    }

    @Override
    @Transactional
    public int editCustomInfo(MCustomInfo mCustomInfo, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        mCustomInfo.setUpdateId(loginUser.getId());
        mCustomInfo.setUpdateTime(DateUtil.getCurrentTimestamp());
        if (mCustomInfoMapper.update(mCustomInfo) == 0) {
            mCustomInfo.setCreateId(loginUser.getId());
            mCustomInfoMapper.insert(mCustomInfo);
        }
        return 1;
    }

}
