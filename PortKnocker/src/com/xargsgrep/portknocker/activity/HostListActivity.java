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
import com.xargsgrep.portknocker.utils.BundleUtils;

public class HostListActivity extends SherlockFragmentActivity {
	
	private static final int MENU_ITEM_ID_ADD = 1;
	private static final int MENU_ITEM_ID_SETTINGS = 2;
	
	private static final String FRAGMENT_TAG_HOST_LIST = "host_list";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_list);
        
        getSupportActionBar().setHomeButtonEnabled(false);
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment hostListFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_HOST_LIST);
		if (hostListFragment == null) {
			hostListFragment = HostListFragment.newInstance();
			ft.add(R.id.fragment_content, hostListFragment, FRAGMENT_TAG_HOST_LIST);
		}
		ft.show(hostListFragment);
		ft.commit();
		
		Bundle extras = getIntent().getExtras();
		if (BundleUtils.contains(extras, EditHostActivity.KEY_SAVE_HOST_RESULT)) {
			Boolean saveResult = extras.getBoolean(EditHostActivity.KEY_SAVE_HOST_RESULT);
			Toast.makeText(this, getResources().getString(saveResult ? R.string.save_success : R.string.save_failure), Toast.LENGTH_SHORT).show();
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ITEM_ID_ADD, 0, "Add Host").setIcon(R.drawable.ic_action_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, MENU_ITEM_ID_SETTINGS, 0, "Settings").setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case MENU_ITEM_ID_ADD: 
				Intent editHostIntent = new Intent(this, EditHostActivity.class);
		        startActivity(editHostIntent);
		        return true;
	    	case MENU_ITEM_ID_SETTINGS:
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
		        startActivity(settingsIntent);
		        return true;
		    default:
		    	return super.onOptionsItemSelected(item);
    	}
    }
    
}