package com.xargsgrep.portknocker.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.xargsgrep.portknocker.model.Application;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.model.Port.Protocol;

public class EditHostActivity extends SherlockFragmentActivity implements ActionBar.TabListener {
	
    HostDataManager hostDataManager;
    
	public static final int MENU_ITEM_CANCEL = 1;
	public static final int MENU_ITEM_SAVE = 2;
	public static final int MENU_ITEM_ADD_PORT = 3;
	
	public static final int TAB_INDEX_HOST = 0;
	public static final int TAB_INDEX_PORTS = 1;
	public static final int TAB_INDEX_MISC = 2;
	
	public static final String SAVE_HOST_RESULT_BUNDLE_KEY = "save_host_result";
	
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
			case TAB_INDEX_HOST:
				if (hostFragment == null) {
					hostFragment = HostFragment.newInstance();
					ft.add(R.id.fragment_content, hostFragment, getString(R.string.host_tab_name));
				}
    			ft.show(hostFragment);
    			if (portsFragment != null) ft.hide(portsFragment);
    			if (miscFragment != null) ft.hide(miscFragment);
    			break;
			case TAB_INDEX_PORTS:
				if (portsFragment == null) {
					portsFragment = PortsFragment.newInstance();
					ft.add(R.id.fragment_content, portsFragment, getString(R.string.ports_tab_name));
				}
    			ft.show(portsFragment);
    			if (hostFragment != null) ft.hide(hostFragment);
    			if (miscFragment != null) ft.hide(miscFragment);
    			break;
			case TAB_INDEX_MISC:
				if (miscFragment == null) {
					miscFragment = MiscFragment.newInstance();
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
		menu.add(Menu.NONE, MENU_ITEM_CANCEL, 1, null).setIcon(R.drawable.ic_action_cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, MENU_ITEM_SAVE, 2, null).setIcon(R.drawable.ic_action_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case android.R.id.home: 
	    		returnToHostListActivity(null);
		        return true;
	    	case MENU_ITEM_CANCEL:
	    		showCancelDialog();
	    		return true;
	    	case MENU_ITEM_SAVE:
	    		saveHost();
	    		return true;
		    default:
		    	return false;
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("selectedTabIndex", getSupportActionBar().getSelectedNavigationIndex());
    }
    
    private void saveHost() {
		HostFragment hostFragment = (HostFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.host_tab_name));
		PortsFragment portsFragment = (PortsFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.ports_tab_name));
		MiscFragment miscFragment = (MiscFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.misc_tab_name));
    	
    	Host host = new Host();
    	
    	EditText hostLabelEdit = (EditText) hostFragment.getView().findViewById(R.id.host_label_edit);
    	EditText hostnameEdit = (EditText) hostFragment.getView().findViewById(R.id.host_name_edit);
    	host.setLabel(hostLabelEdit.getText().toString());
    	host.setHostname(hostnameEdit.getText().toString());
    	
		if (portsFragment != null) { // could be null if user saves without going to ports tab
			ListView portsListView = portsFragment.getListView();
			for (int i=0; i<portsListView.getChildCount(); i++) {
				View row = portsListView.getChildAt(i);
				
				EditText portEditText = (EditText) row.findViewById(R.id.port_row_port);
				Spinner protocolSpinner = (Spinner) row.findViewById(R.id.port_row_protocol);
				
				String portStr = portEditText.getText().toString();
				if (portStr == null || portStr.length() == 0) continue;
				
				int portVal = Integer.parseInt(portStr);
				Protocol protocol = Protocol.valueOf(protocolSpinner.getSelectedItem().toString());
				
				Port port = new Port(portVal, protocol);
				host.getPorts().add(port);
			}
		}
    	
		if (miscFragment != null) { // could be null if user saves without going to misc tab
			EditText delayEditTextView = (EditText) miscFragment.getView().findViewById(R.id.delay_edit);
			String delayStr = delayEditTextView.getText().toString();
			int delay = (delayStr != null && delayStr.length() > 0) ? Integer.parseInt(delayStr) : 0;
			host.setDelay(delay);
			
			Spinner launchAppSpinner = (Spinner) miscFragment.getView().findViewById(R.id.launch_app);
			Application application = (Application) launchAppSpinner.getSelectedItem();
			host.setLaunchApp(application.getIntent());
		}
    	
    	boolean saveResult = hostDataManager.saveHost(host);
    	returnToHostListActivity(saveResult);
    }
    
    private void returnToHostListActivity(Boolean saveResult) {
		Intent hostListIntent = new Intent(this, HostListActivity.class);
		hostListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (saveResult != null) hostListIntent.putExtra(SAVE_HOST_RESULT_BUNDLE_KEY, saveResult);
        startActivity(hostListIntent);
    }
    
    private void showCancelDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.confirm_dialog_cancel_edit_title);
        dialogBuilder.setIcon(R.drawable.confirm_dialog_icon);
        
        dialogBuilder.setPositiveButton(R.string.confirm_dialog_ok,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	returnToHostListActivity(null);
                }
            }
        );
        dialogBuilder.setNegativeButton(R.string.confirm_dialog_cancel,
    		new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) { }
    		}
        );
        
        dialogBuilder.create().show();
    }

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) { }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) { }
    
}
