package com.xargsgrep.portknocker.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;

public class SettingsActivity extends SherlockPreferenceActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	getSupportActionBar().setSubtitle(getResources().getString(R.string.settings_subtitle));
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

}