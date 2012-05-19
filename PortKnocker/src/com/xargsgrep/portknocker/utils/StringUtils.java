/*
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 *
 * Ahsan Rabbani <ahsan@xargsgrep.com>
 *
 */
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
