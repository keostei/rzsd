package com.rzsd.wechat.util;

import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class RzXppDriver extends XppDriver{
	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {
		return new RzPrettyPrintWriter(out);
	}
}
