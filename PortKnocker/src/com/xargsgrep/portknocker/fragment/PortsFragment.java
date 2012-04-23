package com.xargsgrep.portknocker.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.adapter.PortArrayAdapter;
import com.xargsgrep.portknocker.model.Port;

public class PortsFragment extends SherlockListFragment {
	
	public static PortsFragment newInstance() {
		PortsFragment fragment = new PortsFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.ports_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
        getListView().setItemsCanFocus(true);
        
		PortArrayAdapter portAdapter = new PortArrayAdapter(getActivity(), new ArrayList<Port>());
		setListAdapter(portAdapter);
		addPort();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
		menu.add(Menu.NONE, EditHostActivity.MENU_ITEM_ADD_PORT, 0, "Add Port").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case EditHostActivity.MENU_ITEM_ADD_PORT:
	    		addPort();
	    		return true;
	    	default:
	    		return false;
    	}
    }
    
    private void addPort() {
    	PortArrayAdapter adapter = (PortArrayAdapter) getListAdapter();
    	adapter.add(new Port());
    	adapter.notifyDataSetChanged();
    }
}
