package com.rzsd.wechat.common.dto;

public class MSysParam extends BaseDataAccessDto {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3407922701526209992L;
	private String paramName;
	private String paramValue;
	private String paramComment;
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public String getParamComment() {
		return paramComment;
	}
	public void setParamComment(String paramComment) {
		this.paramComment = paramComment;
	}

}
