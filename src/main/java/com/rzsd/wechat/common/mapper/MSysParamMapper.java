package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.MSysParam;

public interface MSysParamMapper {

	List<MSysParam> select(MSysParam selectCond);

	int insert(MSysParam mSysParam);

	int update(MSysParam mSysParam);
}
