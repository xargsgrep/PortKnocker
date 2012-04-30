package com.xargsgrep.portknocker.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;

public class SettingsActivity extends SherlockPreferenceActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	getSupportActionBar().setSubtitle("Preferences");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public void onBuildHeaders(List<Header> target) {
    	loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case android.R.id.home: 
				Intent hostListIntent = new Intent(this, HostListActivity.class);
				hostListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(hostListIntent);
		        return true;
		    default:
		    	return super.onOptionsItemSelected(item);
    	}
    }

    public static class PreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            //PreferenceManager.setDefaultValues(getActivity(), R.layout.preferences_fragment, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }
    
}