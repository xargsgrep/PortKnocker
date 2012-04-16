package com.xargsgrep.portknocker.activity;

import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.HostFragment;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.PortsFragment;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.manager.TabManager;
import com.xargsgrep.portknocker.model.Host;

public class EditHostActivity extends RoboSherlockFragmentActivity {
	
    TabManager tabManager;
    
    @Inject HostDataManager hostDataManager;
    
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
	    		saveHost();
	    		return true;
		    default:
		    	return super.onOptionsItemSelected(item);
    	}
    }
    
    private void saveHost() {
    	Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
    	
    	// Can't inject these using roboguice because they will be null at that point
    	EditText hostLabelEdit = (EditText) findViewById(R.id.host_label_edit);
    	EditText hostNameEdit = (EditText) findViewById(R.id.host_name_edit);
    	EditText delayEdit = (EditText) findViewById(R.id.delay_edit);
    	
    	String hostLabel = HostFragment.hostLabel;
    	String hostName = HostFragment.hostname;
    	int delay = MiscFragment.delay;
    	
    	Host host = new Host();
    	host.setLabel(hostLabel);
    	host.setHostname(hostName);
    	host.setDelay(delay);
    	
    	hostDataManager.addHost(host);
    }
    
    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }
    */
}
