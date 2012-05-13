package com.xargsgrep.portknocker.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.asynctask.KnockerAsyncTask;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.fragment.HostListFragment;
import com.xargsgrep.portknocker.fragment.PreferencesFragment;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.utils.BundleUtils;

public class HostListActivity extends SherlockFragmentActivity {
	
	private static final int MENU_ITEM_ID_ADD = 1;
	private static final int MENU_ITEM_ID_SETTINGS = 2;
	
	private static final String KEY_SHOW_DELETE_DIALOG = "showDeleteDialog";
	
	DatabaseManager databaseManager;
	AlertDialog deleteDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_list);
        getSupportActionBar().setHomeButtonEnabled(false);
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment hostListFragment = getSupportFragmentManager().findFragmentByTag(HostListFragment.TAG);
		if (hostListFragment == null) {
			hostListFragment = HostListFragment.newInstance();
			ft.add(R.id.fragment_content, hostListFragment, HostListFragment.TAG);
		}
		ft.show(hostListFragment);
		ft.commit();
		
		Bundle extras = getIntent().getExtras();
		if (BundleUtils.contains(extras, EditHostActivity.KEY_SAVE_HOST_RESULT) && savedInstanceState == null) {
			Boolean saveResult = extras.getBoolean(EditHostActivity.KEY_SAVE_HOST_RESULT);
			Toast.makeText(this, getResources().getString(saveResult ? R.string.toast_msg_save_success : R.string.toast_msg_save_failure), Toast.LENGTH_SHORT).show();
		}
		
		if (BundleUtils.contains(extras, "hostId") && savedInstanceState == null) {
			// clicked on widget
			databaseManager = new DatabaseManager(this);
			Long hostId = extras.getLong("hostId");
			Host host = databaseManager.getHost(hostId);
			
			KnockerAsyncTask knockerAsyncTask = new KnockerAsyncTask(this, host.getPorts().size());
			knockerAsyncTask.execute(host);
		}
		else if (savedInstanceState != null) {
        	if (savedInstanceState.getBoolean(KEY_SHOW_DELETE_DIALOG))
        		((HostListFragment) hostListFragment).showDeleteDialog();
		}
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ITEM_ID_ADD, 0, "Add Host").setIcon(R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, MENU_ITEM_ID_SETTINGS, 0, "Settings").setIcon(R.drawable.ic_menu_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
	    		Intent settingsIntent = null;
	    		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	    			// PreferenceFragment is not part of the compatibility package
					settingsIntent = new Intent(this, SettingsActivityCompat.class);
	    		}
	    		else {
					settingsIntent = new Intent(this, SettingsActivity.class);
					settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
					settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferencesFragment.class.getName());
	    		}
		        startActivity(settingsIntent);
		        return true;
		    default:
		    	return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean(KEY_SHOW_DELETE_DIALOG, (deleteDialog != null && deleteDialog.isShowing()));
    }
    
    @Override
    protected void onDestroy() {
    	if (deleteDialog != null && deleteDialog.isShowing()) deleteDialog.dismiss();
    	super.onDestroy();
    }
    
    public void setDeleteDialog(AlertDialog deleteDialog) {
    	this.deleteDialog = deleteDialog;
    }
    
}