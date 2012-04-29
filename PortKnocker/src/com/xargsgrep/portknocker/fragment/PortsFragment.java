package com.xargsgrep.portknocker.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
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
import com.xargsgrep.portknocker.model.Port.Protocol;
import com.xargsgrep.portknocker.utils.StringUtils;

public class PortsFragment extends SherlockListFragment {
	
    HostDataManager hostDataManager;
    
    boolean savedInstanceState = false;
    List<Port> ports = new ArrayList<Port>();
	
	public static PortsFragment newInstance(Long hostId) {
		PortsFragment fragment = new PortsFragment();
		if (hostId != null) {
			Bundle args = new Bundle();
			args.putLong(EditHostActivity.HOST_ID_BUNDLE_KEY, hostId);
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
    	
    	if (this.savedInstanceState) {
    		// do nothing
    	} else if (args != null) {
    		Long hostId = args.getLong(EditHostActivity.HOST_ID_BUNDLE_KEY);
    		Host host = hostDataManager.getHost(hostId);
    		ports = host.getPorts();
    	}
    	
    	if (ports.size() == 0) ports.add(new Port());
        
		PortArrayAdapter portAdapter = new PortArrayAdapter(getActivity(), ports);
		setListAdapter(portAdapter);
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
    	adapter.refreshArrayFromListView(getListView());
    	adapter.notifyDataSetChanged();
    }
    
    public List<Port> getPortsFromView() {
		List<Port> ports = new ArrayList<Port>();
		
		ListView portsListView = getListView();
		for (int i=0; i<portsListView.getChildCount(); i++) {
			View row = portsListView.getChildAt(i);
			
			String portStr = getPortEditTextFromRowView(row).getText().toString();
			if (StringUtils.isBlank(portStr)) continue;
			int portVal = Integer.parseInt(portStr);
			
			Protocol protocol = Protocol.valueOf(getProtocolSpinnerFromRowView(row).getSelectedItem().toString());
			
			Port port = new Port(portVal, protocol);
			ports.add(port);
		}
		
		return ports;
    }
}
