package com.rzsd.wechat.common.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

public class BaseDataAccessDto implements Serializable{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5615686425620379550L;
	/**
     * 删除标识
     */
    private String delFlg;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 创建者ID
     */
    private BigInteger createId;
    /**
     * 更新时间
     */
    private Timestamp updateTime;
    /**
     * 更新者ID
     */
    private BigInteger updateId;

    /**
     * 排序SQL
     */
    private String orderByStr;

    /**
     * 取得件数设置
     */
    private Long limitCnt;
    /**
     * 取得件数开始设置
     */
    private Long offsetRowNum;
	public String getDelFlg() {
		return delFlg;
	}
	public void setDelFlg(String delFlg) {
		this.delFlg = delFlg;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public BigInteger getCreateId() {
		return createId;
	}
	public void setCreateId(BigInteger createId) {
		this.createId = createId;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public BigInteger getUpdateId() {
		return updateId;
	}
	public void setUpdateId(BigInteger updateId) {
		this.updateId = updateId;
	}
	public String getOrderByStr() {
		return orderByStr;
	}
	public void setOrderByStr(String orderByStr) {
		this.orderByStr = orderByStr;
	}
	public Long getLimitCnt() {
		return limitCnt;
	}
	public void setLimitCnt(Long limitCnt) {
		this.limitCnt = limitCnt;
	}
	public Long getOffsetRowNum() {
		return offsetRowNum;
	}
	public void setOffsetRowNum(Long offsetRowNum) {
		this.offsetRowNum = offsetRowNum;
	}
}
