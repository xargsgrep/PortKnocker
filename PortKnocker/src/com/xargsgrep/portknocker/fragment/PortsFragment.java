package com.xargsgrep.portknocker.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.adapter.PortArrayAdapter;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;

public class PortsFragment extends SherlockListFragment {
	
    HostDataManager hostDataManager;
    
    PortArrayAdapter portAdapter;
    boolean savedInstanceState = false;
	
	public static PortsFragment newInstance(Long hostId) {
		PortsFragment fragment = new PortsFragment();
		if (hostId != null) {
			Bundle args = new Bundle();
			args.putLong(EditHostActivity.KEY_HOST_ID, hostId);
			fragment.setArguments(args);
		}
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		hostDataManager = new HostDataManager(getActivity());
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.ports_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
    	Bundle args = getArguments();
    	
    	if (args != null && !this.savedInstanceState) {
    		// only restore state from args if onSaveInstanceState hasn't been invoked
    		Long hostId = args.getLong(EditHostActivity.KEY_HOST_ID);
    		Host host = hostDataManager.getHost(hostId);
    		List<Port> ports = (host.getPorts().size() > 0) ? host.getPorts() : Arrays.asList(new Port());
			portAdapter = new PortArrayAdapter(getActivity(), ports);
			setListAdapter(portAdapter);
    	}
    	else if (portAdapter == null) {
    		List<Port> ports = new ArrayList<Port>();
    		ports.add(new Port());
			portAdapter = new PortArrayAdapter(getActivity(), ports);
			setListAdapter(portAdapter);
    	}
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
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
		savedInstanceState = true;
    }
    
    public EditText getPortEditTextFromRowView(View row) {
		return (EditText) row.findViewById(R.id.port_row_port);
    }
    
    public Spinner getProtocolSpinnerFromRowView(View row) {
		return (Spinner) row.findViewById(R.id.port_row_protocol);
    }
    
    private void addPort() {
    	PortArrayAdapter adapter = (PortArrayAdapter) getListAdapter();
    	adapter.add(new Port());
    }
    
}
