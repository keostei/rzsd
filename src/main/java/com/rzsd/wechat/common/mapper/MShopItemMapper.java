package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.MShopItem;

public interface MShopItemMapper {

    List<MShopItem> select(MShopItem selectCond);

    int insert(MShopItem mShopItem);

    int update(MShopItem mShopItem);
}
