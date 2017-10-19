package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.MUser;

public interface MUserMapper {

	List<MUser> select(MUser selectCond);

	int insert(MUser mUser);

	int update(MUser mUser);
}
