package com.xargsgrep.portknocker.activity;

import java.util.List;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;

public class HostListActivity extends RoboSherlockActivity {
	
    @Inject HostDataManager hostDataManager;
    
	@InjectView(R.id.host_list) LinearLayout hostList;
	
	private static final int MENU_ADD_ITEM_ID = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_list);
        getSupportActionBar().setHomeButtonEnabled(false);
        
        List<Host> hosts = hostDataManager.getAllHosts();
        System.out.println(hosts);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ADD_ITEM_ID, 0, null).setIcon(R.drawable.ic_action_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case MENU_ADD_ITEM_ID: 
				Intent editHostIntent = new Intent(this, EditHostActivity.class);
		        startActivity(editHostIntent);
		        return true;
		    default:
		    	return super.onOptionsItemSelected(item);
    	}
    }
    
}