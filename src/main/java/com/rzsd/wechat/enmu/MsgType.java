package com.rzsd.wechat.enmu;

public enum MsgType {
	Text("1", "text"),
	Image("2", "image"),
	Voice("3", "voice"),
	Video("4", "video"),
	Shortvideo("5", "shortvideo"),
	Location("6", "location"),
	Link("7", "link"),
	Event("8", "event"),
	;
	private String _code;
	private String _value;
	MsgType(String _code, String _value) {
		this._code = _code;
		this._value = _value;
	};
	
	public String toString() {
		return this._value;
	}
}
