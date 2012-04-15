package com.xargsgrep.portknocker.activity;

import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.HostFragment;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.PortsFragment;
import com.xargsgrep.portknocker.manager.TabManager;

public class EditHostActivity extends RoboSherlockFragmentActivity {
	
    TabManager tabManager;
    
	@InjectView(R.id.tab_host) TabHost tabHost;
	
	@InjectResource(R.string.host_tab_name) String hostTabName;
	@InjectResource(R.string.ports_tab_name) String portsTabName;
	@InjectResource(R.string.misc_tab_name) String miscTabName;
	
	private static final int MENU_CANCEL_ITEM_ID = 1;
	private static final int MENU_SAVE_ITEM_ID = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_host);
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        tabHost.setup();
        tabManager = new TabManager(this, tabHost, R.id.real_tab_content);
        
        tabManager.addTab(tabHost.newTabSpec("host").setIndicator(hostTabName), HostFragment.class, null);
        tabManager.addTab(tabHost.newTabSpec("ports").setIndicator(portsTabName), PortsFragment.class, null);
        tabManager.addTab(tabHost.newTabSpec("misc").setIndicator(miscTabName), MiscFragment.class, null);
        
        /*
        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        */
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_CANCEL_ITEM_ID, 0, null).setIcon(R.drawable.ic_action_cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, MENU_SAVE_ITEM_ID, 1, null).setIcon(R.drawable.ic_action_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case android.R.id.home: 
				Intent hostListIntent = new Intent(this, HostListActivity.class);
				hostListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(hostListIntent);
		        return true;
	    	case MENU_CANCEL_ITEM_ID:
	    		Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
	    		return true;
	    	case MENU_SAVE_ITEM_ID:
	    		Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
	    		return true;
		    default:
		    	return super.onOptionsItemSelected(item);
    	}
    }
    
    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }
    */
}
