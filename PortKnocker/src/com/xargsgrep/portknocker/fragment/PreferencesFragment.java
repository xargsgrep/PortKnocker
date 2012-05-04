package com.xargsgrep.portknocker.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.xargsgrep.portknocker.R;

public class PreferencesFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
	}
	
}
