package com.xargsgrep.portknocker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.HostFragment;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.PortsFragment;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;

public class EditHostActivity extends SherlockFragmentActivity implements ActionBar.TabListener {
	
    HostDataManager hostDataManager;
    
	private static final int MENU_CANCEL_ITEM_ID = 1;
	private static final int MENU_SAVE_ITEM_ID = 2;
	
	private static final int TAB_HOST_INDEX = 0;
	private static final int TAB_PORTS_INDEX = 1;
	private static final int TAB_MISC_INDEX = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_host);
        
        hostDataManager = new HostDataManager(getApplicationContext());
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        addTab(getString(R.string.host_tab_name));
        addTab(getString(R.string.ports_tab_name));
        addTab(getString(R.string.misc_tab_name));
        
        if (savedInstanceState != null) {
        	getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("selectedTabIndex"));
        }
    }
    
    private void addTab(String text) {
        ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(text);
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);
    }
    
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Fragment hostFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.host_tab_name));
		Fragment portsFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.ports_tab_name));
		Fragment miscFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.misc_tab_name));
		
		switch (tab.getPosition()) {
			case TAB_HOST_INDEX:
				if (hostFragment == null) {
					hostFragment = new HostFragment();
					ft.add(R.id.fragment_content, hostFragment, getString(R.string.host_tab_name));
				}
    			ft.show(hostFragment);
    			if (portsFragment != null) ft.hide(portsFragment);
    			if (miscFragment != null) ft.hide(miscFragment);
    			break;
			case TAB_PORTS_INDEX:
				if (portsFragment == null) {
					portsFragment = new PortsFragment();
					ft.add(R.id.fragment_content, portsFragment, getString(R.string.ports_tab_name));
				}
    			ft.show(portsFragment);
    			if (hostFragment != null) ft.hide(hostFragment);
    			if (miscFragment != null) ft.hide(miscFragment);
    			break;
			case TAB_MISC_INDEX:
				if (miscFragment == null) {
					miscFragment = new MiscFragment();
					ft.add(R.id.fragment_content, miscFragment, getString(R.string.misc_tab_name));
				}
    			ft.show(miscFragment);
    			if (hostFragment != null) ft.hide(hostFragment);
    			if (portsFragment != null) ft.hide(portsFragment);
    			break;
		}
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
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("selectedTabIndex", getSupportActionBar().getSelectedNavigationIndex());
    }
    
    private void saveHost() {
    	//Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
    	
		HostFragment hostFragment = (HostFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.host_tab_name));
		PortsFragment portsFragment = (PortsFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.ports_tab_name));
		MiscFragment miscFragment = (MiscFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.misc_tab_name));
    	
    	String hostLabel = hostFragment.getHostLabelEditTextView().getText().toString();
    	String hostname = hostFragment.getHostnameEditTextView().getText().toString();
    	
		if (portsFragment != null) { // could be null if user saves without going to ports tab
			LinearLayout portListView = portsFragment.getPortListLinearLayoutView();
			for (int i=0; i<portListView.getChildCount(); i++) {
				View row = portListView.getChildAt(i);
				
				EditText portEditText = (EditText) row.findViewById(R.id.port_row_port);
				Spinner protocolSpinner = (Spinner) row.findViewById(R.id.port_row_protocol);
				
				System.out.println(portEditText.getText().toString());
				System.out.println(protocolSpinner.getSelectedItem().toString());
			}
		}
    	
		int delay = 0;
		if (miscFragment != null) { // could be null if user saves without going to misc tab
			EditText delayEditTextView = miscFragment.getDelayEdit();
			String delayStr = delayEditTextView.getText().toString();
			delay = (delayStr != null && delayStr.length() > 0) ? Integer.parseInt(delayStr) : 0;
		}
    	
    	Host host = new Host();
    	host.setLabel(hostLabel);
    	host.setHostname(hostname);
    	host.setDelay(delay);
    	
    	//hostDataManager.saveHost(host);
    }

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) { }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) { }
    
}
