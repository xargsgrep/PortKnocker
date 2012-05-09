package com.xargsgrep.portknocker.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.adapter.HostArrayAdapter;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;

public class HostListFragment extends SherlockListFragment {
	
	public static final String TAG = "HostListFragment";
	
    DatabaseManager databaseManager;
    
	public static HostListFragment newInstance() {
		return new HostListFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		databaseManager = new DatabaseManager(getActivity());
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.list_view, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
        List<Host> hosts = databaseManager.getAllHosts();
		HostArrayAdapter hostAdapter = new HostArrayAdapter(getActivity(), hosts);
		setListAdapter(hostAdapter);
    }
    
}
