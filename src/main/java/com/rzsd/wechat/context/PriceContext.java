package com.rzsd.wechat.context;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rzsd.wechat.common.dto.MSysParam;
import com.rzsd.wechat.common.dto.MUser;
import com.rzsd.wechat.common.mapper.MSysParamMapper;
import com.rzsd.wechat.common.mapper.MUserMapper;
import com.rzsd.wechat.util.DateUtil;

@Component
public class PriceContext {

    @Autowired
    private MSysParamMapper mSysParamMapper;
    @Autowired
    private MUserMapper mUserMapper;

    private static List<PriceContextDto> priceContextDtoLst;

    private PriceContext() {
    }

    private List<PriceContextDto> getPriceContextInstance() {
        if (priceContextDtoLst != null) {
            return priceContextDtoLst;
        }
        MSysParam mSysParamCond = new MSysParam();
        mSysParamCond.setParamName("PRICE");
        List<MSysParam> priceParamLst = mSysParamMapper.selectPriceParam(mSysParamCond);
        priceContextDtoLst = new ArrayList<>();
        for (MSysParam param : priceParamLst) {
            PriceContextDto dto = new PriceContextDto();
            String[] args = param.getParamName().split("_");
            dto.setWeightFrom(new BigDecimal(args[1]));
            if (args.length > 2) {
                dto.setWeightTo(new BigDecimal(args[2]));
            } else {
                dto.setWeightTo(new BigDecimal(args[1]));
            }
            if (args.length > 3) {
                dto.setPriceType(args[3]);
            } else {
                if (dto.getWeightFrom().compareTo(dto.getWeightTo()) == 0) {
                    // 价格合计使用
                    dto.setPriceType("0");
                } else {
                    // 单价×重量
                    dto.setPriceType("1");
                }
            }
            dto.setPrice(new BigDecimal(param.getParamValue()));
            priceContextDtoLst.add(dto);
        }
        return priceContextDtoLst;
    }

    public BigDecimal calcPrice(BigInteger userId, BigDecimal weight, BigDecimal price, BigInteger loginUserId) {
        MUser mUser = new MUser();
        mUser.setId(userId);
        List<MUser> usrLst = mUserMapper.select(mUser);
        if (!usrLst.isEmpty()) {
            mUser = usrLst.get(0);

        }

        if (price != null) {
            mUser.setPrice(price);
            mUser.setUpdateId(loginUserId);
            mUser.setUpdateTime(DateUtil.getCurrentTimestamp());
            mUserMapper.update(mUser);
            return weight.multiply(price);
        }
        if (new BigDecimal("-1").compareTo(mUser.getPrice()) != 0) {
            return weight.multiply(price);
        }
        PriceContextDto matchedDto = null;
        for (PriceContextDto dto : getPriceContextInstance()) {
            if (dto.getWeightFrom().compareTo(weight) <= 0 && dto.getWeightTo().compareTo(weight) >= 0) {
                matchedDto = dto;
                break;
            }
        }

        if ("0".equals(matchedDto.getPriceType())) {
            return matchedDto.getPrice();
        }
        return weight.multiply(matchedDto.getPrice());
    }
}
