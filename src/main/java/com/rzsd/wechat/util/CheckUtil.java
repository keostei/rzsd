package com.rzsd.wechat.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {

    public static final String REGEX_NUM = "^[0-9]*$";
    public static final String REGEX_ALPHABETA = "^[a-zA-Z]*$";

    public static boolean isValidString(String str, String regEx) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean isLengthValid(String str, int length) {
        return str.length() <= length;
    }
}
