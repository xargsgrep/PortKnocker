package com.xargsgrep.portknocker.utils;


public class StringUtils {
	
	public static boolean contains(String stringToCheck, String str) {
		return (stringToCheck == null) ? false : stringToCheck.contains(str);
	}
	
	public static boolean isBlank(String str) {
		return (str == null || str.trim().isEmpty());
	}
	
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
}
