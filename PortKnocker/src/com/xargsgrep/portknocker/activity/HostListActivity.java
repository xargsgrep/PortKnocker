package com.xargsgrep.portknocker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.HostListFragment;

public class HostListActivity extends SherlockFragmentActivity {
	
	private static final int MENU_ADD_ITEM_ID = 1;
	private static final int MENU_SETTINGS_ITEM_ID = 2;
	
	private static final String HOST_LIST_FRAGMENT_TAG = "host_list";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_list);
        
        getSupportActionBar().setHomeButtonEnabled(false);
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment hostListFragment = getSupportFragmentManager().findFragmentByTag(HOST_LIST_FRAGMENT_TAG);
		if (hostListFragment == null) {
			hostListFragment = HostListFragment.newInstance();
			ft.add(R.id.fragment_content, hostListFragment, HOST_LIST_FRAGMENT_TAG);
		}
		ft.show(hostListFragment);
		ft.commit();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(EditHostActivity.SAVE_HOST_RESULT_BUNDLE_KEY)) {
			Boolean saveResult = extras.getBoolean(EditHostActivity.SAVE_HOST_RESULT_BUNDLE_KEY);
			Toast.makeText(this, getResources().getString(saveResult ? R.string.save_success : R.string.save_failure), Toast.LENGTH_SHORT).show();
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ADD_ITEM_ID, 0, "Add Host").setIcon(R.drawable.ic_action_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, MENU_SETTINGS_ITEM_ID, 0, "Settings").setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case MENU_ADD_ITEM_ID: 
				Intent editHostIntent = new Intent(this, EditHostActivity.class);
		        startActivity(editHostIntent);
		        return true;
	    	case MENU_SETTINGS_ITEM_ID: 
		        return true;
		    default:
		    	return super.onOptionsItemSelected(item);
    	}
    }
    
}