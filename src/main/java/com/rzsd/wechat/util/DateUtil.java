package com.rzsd.wechat.util;

import java.sql.Timestamp;
import java.util.Calendar;

public class DateUtil {

	public static Timestamp getCurrentTimestamp() {
		return new Timestamp(Calendar.getInstance().getTimeInMillis());
	}
}
