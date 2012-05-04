package com.xargsgrep.portknocker.activity;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.adapter.PortArrayAdapter;
import com.xargsgrep.portknocker.fragment.HostFragment;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.PortsFragment;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.utils.BundleUtils;
import com.xargsgrep.portknocker.utils.StringUtils;

public class EditHostActivity extends SherlockFragmentActivity implements ActionBar.TabListener {
	
	HostDataManager hostDataManager;
    
    // null when creating a new host
    private Long hostId;
    
	//public static final int MENU_ITEM_CANCEL = 1;
	public static final int MENU_ITEM_SAVE = 2;
	public static final int MENU_ITEM_ADD_PORT = 3;
	
	public static final int TAB_INDEX_HOST = 0;
	public static final int TAB_INDEX_PORTS = 1;
	public static final int TAB_INDEX_MISC = 2;
	
	public static final String KEY_HOST_ID = "hostId";
	public static final String KEY_SELECTED_TAB_INDEX = "selectedTabIndex";
	public static final String KEY_SAVE_HOST_RESULT = "saveHostResult";
	
	private static final String FRAGMENT_TAG_HOST_TAB = "host_tab";
	private static final String FRAGMENT_TAG_PORTS_TAB = "ports_tab";
	private static final String FRAGMENT_TAG_MISC_TAB = "misc_tab";
	
    private static final int MAX_PORT_VALUE = 65535;
    private static final int MAX_DELAY_VALUE = 10000;

	private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^[a-z0-9]+([-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}$", Pattern.CASE_INSENSITIVE);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_host);
        
        hostDataManager = new HostDataManager(this);
        
		Bundle extras = getIntent().getExtras();
		hostId = (BundleUtils.contains(extras, KEY_HOST_ID)) ? extras.getLong(KEY_HOST_ID) : null;
    	Host host = (hostId == null) ? null : hostDataManager.getHost(hostId);
		
    	if (host != null) getSupportActionBar().setSubtitle(host.getLabel());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        addTab(getString(R.string.host_tab_name));
        addTab(getString(R.string.ports_tab_name));
        addTab(getString(R.string.misc_tab_name));
        
        if (savedInstanceState != null) {
        	getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(KEY_SELECTED_TAB_INDEX));
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
		Fragment hostFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_HOST_TAB);
		Fragment portsFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_PORTS_TAB);
		Fragment miscFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_MISC_TAB);
		
		switch (tab.getPosition()) {
			case TAB_INDEX_HOST:
				if (hostFragment == null) {
					hostFragment = HostFragment.newInstance(hostId);
					ft.add(R.id.fragment_content, hostFragment, FRAGMENT_TAG_HOST_TAB);
				}
    			ft.show(hostFragment);
    			if (portsFragment != null) ft.hide(portsFragment);
    			if (miscFragment != null) ft.hide(miscFragment);
    			break;
			case TAB_INDEX_PORTS:
				if (portsFragment == null) {
					portsFragment = PortsFragment.newInstance(hostId);
					ft.add(R.id.fragment_content, portsFragment, FRAGMENT_TAG_PORTS_TAB);
				}
    			ft.show(portsFragment);
    			if (hostFragment != null) ft.hide(hostFragment);
    			if (miscFragment != null) ft.hide(miscFragment);
    			break;
			case TAB_INDEX_MISC:
				if (miscFragment == null) {
					miscFragment = MiscFragment.newInstance(hostId);
					ft.add(R.id.fragment_content, miscFragment, FRAGMENT_TAG_MISC_TAB);
				}
    			ft.show(miscFragment);
    			if (hostFragment != null) ft.hide(hostFragment);
    			if (portsFragment != null) ft.hide(portsFragment);
    			break;
		}
	}
	
	@Override
	public void onBackPressed() {
   		showCancelDialog();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(Menu.NONE, MENU_ITEM_CANCEL, 1, "Cancel").setIcon(R.drawable.ic_action_cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, MENU_ITEM_SAVE, 2, "Save").setIcon(R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case android.R.id.home: 
	    		showCancelDialog();
		        return true;
	    	//case MENU_ITEM_CANCEL:
	    		//showCancelDialog();
	    		//return true;
	    	case MENU_ITEM_SAVE:
	    		saveHost();
	    		return true;
		    default:
		    	// so fragment onOptionsItemSelected methods get called
		    	return false;
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt(KEY_SELECTED_TAB_INDEX, getSupportActionBar().getSelectedNavigationIndex());
    }
    
    private void saveHost() {
		HostFragment hostFragment = (HostFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_HOST_TAB);
		PortsFragment portsFragment = (PortsFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_PORTS_TAB);
		MiscFragment miscFragment = (MiscFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_MISC_TAB);
    	
    	Host host = (hostId == null) ? new Host() : hostDataManager.getHost(hostId);
    	
    	host.setLabel(hostFragment.getHostLabelEditText().getText().toString());
    	host.setHostname(hostFragment.getHostnameEditText().getText().toString());
    	
		if (portsFragment != null) { // could be null if user saves without going to ports tab
			host.getPorts().clear();
			
			List<Port> ports = ((PortArrayAdapter) portsFragment.getListAdapter()).getPorts();
			for (Port port : ports) {
				if (port.getPort() > 0) host.getPorts().add(port);
			}
		}
    	
		if (miscFragment != null) { // could be null if user saves without going to misc tab
			String delayStr = miscFragment.getDelayEditText().getText().toString();
			int delay = (StringUtils.isNotBlank(delayStr)) ? Integer.parseInt(delayStr) : 0;
			host.setDelay(delay);
			
			String launchIntent = miscFragment.getSelectedLaunchIntent();
			host.setLaunchIntentPackage(launchIntent);
		}
		
		boolean isValid = validateAndDisplayErrors(host);
		if (isValid) {
	    	boolean saveResult = (hostId == null) ? hostDataManager.saveHost(host) : hostDataManager.updateHost(host);
	    	returnToHostListActivity(saveResult);
		}
    }
    
    private boolean validateAndDisplayErrors(Host host) {
		boolean validHostname = HOSTNAME_PATTERN.matcher(host.getHostname()).matches();
		boolean validIP = InetAddressUtils.isIPv4Address(host.getHostname());
		
		String errorText = "";
		if (StringUtils.isBlank(host.getLabel())) {
			errorText = "Please enter a label";
		}
		else if (StringUtils.isBlank(host.getHostname())) {
			errorText = "Please enter a hostname";
		}
		else if (!validHostname && !validIP) {
			errorText = "Invalid hostname/IP";
		}
		else if (host.getPorts() == null || host.getPorts().size() == 0) {
			errorText = "Please enter at least one port";
		}
		else if (host.getDelay() > MAX_DELAY_VALUE) {
			errorText = "Delay can't be more than " + MAX_DELAY_VALUE;
		}
		else {
			for (Port port : host.getPorts()) {
				if (port.getPort() > MAX_PORT_VALUE) {
					errorText = "Invalid port: " + port.getPort();
					break;
				}
			}
		}
		
		if (StringUtils.isNotBlank(errorText)) {
			Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			return false;
		}
		
		return true;
    }
    
    private void returnToHostListActivity(Boolean saveResult) {
		Intent hostListIntent = new Intent(this, HostListActivity.class);
		hostListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (saveResult != null) hostListIntent.putExtra(KEY_SAVE_HOST_RESULT, saveResult);
        startActivity(hostListIntent);
    }
    
    private void showCancelDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.confirm_dialog_cancel_edit_title);
        dialogBuilder.setIcon(R.drawable.ic_dialog_confirm);
        
        dialogBuilder.setPositiveButton(R.string.confirm_dialog_confirm,
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
