package com.xargsgrep.portknocker.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.adapter.HostArrayAdapter;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;

public class HostListActivity extends SherlockListActivity {
	
	public static final String HOST_ID_BUNDLE_KEY = "host_id";
	
	private static final int MENU_ADD_ITEM_ID = 1;
	
    HostDataManager hostDataManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        hostDataManager = new HostDataManager(getApplicationContext());
        
        getSupportActionBar().setHomeButtonEnabled(false);
        setContentView(R.layout.host_list);
        getListView().setItemsCanFocus(true);
        
        List<Host> hosts = hostDataManager.getAllHosts();
		HostArrayAdapter hostAdapter = new HostArrayAdapter(this, hosts);
		setListAdapter(hostAdapter);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(EditHostActivity.SAVE_HOST_RESULT_BUNDLE_KEY)) {
			Boolean saveResult = extras.getBoolean(EditHostActivity.SAVE_HOST_RESULT_BUNDLE_KEY);
			Toast.makeText(this, getResources().getString(saveResult ? R.string.save_success : R.string.save_failure), Toast.LENGTH_SHORT).show();
		}
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