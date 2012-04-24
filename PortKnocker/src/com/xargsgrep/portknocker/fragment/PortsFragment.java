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

public class PortsFragment extends SherlockListFragment {
	
	private static final String PORTS_BUNDLE_KEY = "ports";
	private static final String PROTOCOLS_BUNDLE_KEY = "protocols";
	
    HostDataManager hostDataManager;
	
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
		hostDataManager = new HostDataManager(getActivity());
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
    	
    	List<Port> ports = new ArrayList<Port>();
    	Bundle args = getArguments();
    	
    	if (savedInstanceState != null) {
    		String[] portsStrArray = savedInstanceState.getStringArray(PORTS_BUNDLE_KEY);
    		String[] protocolsStrArray = savedInstanceState.getStringArray(PROTOCOLS_BUNDLE_KEY);
    		
    		for (int i=0; i<portsStrArray.length; i++) {
    			int port = (portsStrArray[i] == null || portsStrArray[i].length() == 0) ? -1 : Integer.parseInt(portsStrArray[i]);
    			Protocol protocol = Protocol.valueOf(protocolsStrArray[i]);
    			ports.add(new Port(port, protocol));
    		}
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
    	
    	ListView listView = getListView();
    	String[] ports = new String[listView.getChildCount()];
    	String[] protocols = new String[listView.getChildCount()];
		for (int i=0; i<listView.getChildCount(); i++) {
			View row = listView.getChildAt(i);
			ports[i] = getPortEditTextFromRowView(row).getText().toString();
			protocols[i] = getProtocolSpinnerFromRowView(row).getSelectedItem().toString();
		}
		
		outState.putStringArray(PORTS_BUNDLE_KEY, ports);
		outState.putStringArray(PROTOCOLS_BUNDLE_KEY, protocols);
    }
    
    public EditText getPortEditTextFromRowView(View row) {
		return (EditText) row.findViewById(R.id.port_row_port);
    }
    
    public Spinner getProtocolSpinnerFromRowView(View row) {
		return (Spinner) row.findViewById(R.id.port_row_protocol);
    }
    
    private void addPort() {
    	PortArrayAdapter adapter = (PortArrayAdapter) getListAdapter();
    	adapter.refreshArrayFromListView(getListView());
    	adapter.add(new Port());
    	adapter.notifyDataSetChanged();
    }
}
