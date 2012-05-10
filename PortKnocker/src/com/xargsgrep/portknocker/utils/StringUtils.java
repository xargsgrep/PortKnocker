package com.xargsgrep.portknocker.utils;


public class StringUtils {
	
	public static boolean contains(String stringToCheck, String str) {
		return (stringToCheck == null) ? false : stringToCheck.contains(str);
	}
	
	// String.isEmpty only available starting API 9
	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0);
	}
	
	public static boolean isBlank(String str) {
		return (str == null || isEmpty(str.trim()));
	}
	
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
}
