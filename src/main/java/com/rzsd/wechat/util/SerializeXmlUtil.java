package com.rzsd.wechat.util;

import com.thoughtworks.xstream.XStream;

public class SerializeXmlUtil {

	public static XStream createXstream() {
		return new XStream(new RzXppDriver());
	}

}
