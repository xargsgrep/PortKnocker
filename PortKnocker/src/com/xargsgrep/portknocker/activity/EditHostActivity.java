package com.xargsgrep.portknocker.activity;

import roboguice.inject.InjectResource;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.HostFragment;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.PortsFragment;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;

public class EditHostActivity extends RoboSherlockFragmentActivity implements ActionBar.TabListener {
	
    @Inject HostDataManager hostDataManager;
    @Inject HostFragment hostFragment;
    @Inject PortsFragment portsFragment;
    @Inject MiscFragment miscFragment;
    
	@InjectResource(R.string.host_tab_name) String hostTabName;
	@InjectResource(R.string.ports_tab_name) String portsTabName;
	@InjectResource(R.string.misc_tab_name) String miscTabName;
	
	private static final int MENU_CANCEL_ITEM_ID = 1;
	private static final int MENU_SAVE_ITEM_ID = 2;
	private static final int TAB_HOST_INDEX = 0;
	private static final int TAB_PORTS_INDEX = 1;
	private static final int TAB_MISC_INDEX = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_host);
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        ActionBar.Tab hostTab = getSupportActionBar().newTab();
        hostTab.setTag(hostTabName.toLowerCase());
        hostTab.setText(hostTabName);
        hostTab.setTabListener(this);
        
        ActionBar.Tab portsTab = getSupportActionBar().newTab();
        portsTab.setTag(portsTabName.toLowerCase());
        portsTab.setText(portsTabName);
        portsTab.setTabListener(this);
        
        ActionBar.Tab miscTab = getSupportActionBar().newTab();
        miscTab.setTag(miscTabName.toLowerCase());
        miscTab.setText(miscTabName);
        miscTab.setTabListener(this);
        
        getSupportActionBar().addTab(hostTab);
        getSupportActionBar().addTab(portsTab);
        getSupportActionBar().addTab(miscTab);
    }
    
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		switch (tab.getPosition()) {
			case TAB_HOST_INDEX:
				ft.replace(R.id.fragment_content, hostFragment, hostTabName);
				break;
			case TAB_PORTS_INDEX:
				ft.replace(R.id.fragment_content, portsFragment, portsTabName);
				break;
			case TAB_MISC_INDEX:
				ft.replace(R.id.fragment_content, miscFragment, miscTabName);
				break;
			default:
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
    
    private void saveHost() {
    	//Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
    	
    	String hostLabel = hostFragment.getHostLabelEditTextView().getText().toString();
    	String hostname = hostFragment.getHostnameEditTextView().getText().toString();
    	
		LinearLayout portListView = portsFragment.getPortListLinearLayoutView();
		if (portListView != null) { // could be null if user saves without going to ports tab
			for (int i=0; i<portListView.getChildCount(); i++) {
				View row = portListView.getChildAt(i);
				
				EditText portEditText = (EditText) row.findViewById(R.id.port_row_port);
				Spinner protocolSpinner = (Spinner) row.findViewById(R.id.port_row_protocol);
				
				System.out.println(portEditText.getText().toString());
				System.out.println(protocolSpinner.getSelectedItem().toString());
			}
		}
    	
		EditText delayEditTextView = miscFragment.getDelayEdit();
		int delay = 0;
		if (delayEditTextView != null) { // could be null if user saves without going to misc tab
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
