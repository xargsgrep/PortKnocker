package com.xargsgrep.portknocker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;

public class PortsFragment extends SherlockFragment {
	
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
    	View row1 = getLayoutInflater(savedInstanceState).inflate(R.layout.port_row, null);
    	View row2 = getLayoutInflater(savedInstanceState).inflate(R.layout.port_row, null);
    	getPortListLinearLayoutView().addView(row1);
    	getPortListLinearLayoutView().addView(row2);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
		menu.add(Menu.NONE, Menu.NONE, 2, "Add Port").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    
    public LinearLayout getPortListLinearLayoutView() {
    	return (LinearLayout) getView().findViewById(R.id.port_list);
    }
    
}
