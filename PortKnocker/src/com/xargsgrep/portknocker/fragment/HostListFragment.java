package com.xargsgrep.portknocker.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.adapter.HostArrayAdapter;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;

public class HostListFragment extends SherlockListFragment {
	
	public static final String TAG = "HostListFragment";
	
    HostDataManager hostDataManager;
    
	public static HostListFragment newInstance() {
		return new HostListFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		hostDataManager = new HostDataManager(getActivity());
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.host_list_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
        List<Host> hosts = hostDataManager.getAllHosts();
		HostArrayAdapter hostAdapter = new HostArrayAdapter(getActivity(), this, hosts);
		setListAdapter(hostAdapter);
    }
    
}
