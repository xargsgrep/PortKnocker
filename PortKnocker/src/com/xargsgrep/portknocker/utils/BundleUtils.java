package com.xargsgrep.portknocker.utils;

import android.os.Bundle;

public class BundleUtils {
	
	public static boolean contains(Bundle bundle, String key) {
		return (bundle != null && bundle.containsKey(key));
	}
	
}
