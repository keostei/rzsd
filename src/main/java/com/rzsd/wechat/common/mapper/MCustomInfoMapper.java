package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.MCustomInfo;

public interface MCustomInfoMapper {

    List<MCustomInfo> select(MCustomInfo selectCond);

    int insert(MCustomInfo mCustomInfo);

    int update(MCustomInfo mCustomInfo);

    int updateCustomId(MCustomInfo mCustomInfo);
}
