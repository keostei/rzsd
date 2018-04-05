package com.rzsd.wechat.common.mapper;

import java.util.List;

import com.rzsd.wechat.common.dto.TShopItem;

public interface TShopItemMapper {

    List<TShopItem> select(TShopItem tShopItem);

    List<TShopItem> selectShopItemWithItemInfo(TShopItem tShopItem);

    int insert(TShopItem tShopItem);

    int update(TShopItem tShopItem);
}
