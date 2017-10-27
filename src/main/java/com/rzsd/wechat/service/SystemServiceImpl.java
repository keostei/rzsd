package com.rzsd.wechat.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rzsd.wechat.common.dto.MSysParam;
import com.rzsd.wechat.common.mapper.MSysParamMapper;
import com.rzsd.wechat.entity.LoginUser;
import com.rzsd.wechat.util.DateUtil;

@Service
public class SystemServiceImpl implements SystemService {

    @Autowired
    private MSysParamMapper mSysParamMapper;

    @Override
    public MSysParam getSysParamByKey(String key) {
        MSysParam selectCond = new MSysParam();
        selectCond.setParamName(key);
        List<MSysParam> sysParamLst = mSysParamMapper.select(selectCond);
        if (sysParamLst.isEmpty()) {
            return null;
        }
        return sysParamLst.get(0);
    }

    @Override
    @Transactional
    public int updateSysParamByKey(String key, String value, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute("LOGIN_USER");
        MSysParam selectCond = new MSysParam();
        selectCond.setParamName(key);
        selectCond.setParamValue(value);
        selectCond.setUpdateTime(DateUtil.getCurrentTimestamp());
        selectCond.setUpdateId(loginUser.getId());
        return mSysParamMapper.update(selectCond);
    }

}
