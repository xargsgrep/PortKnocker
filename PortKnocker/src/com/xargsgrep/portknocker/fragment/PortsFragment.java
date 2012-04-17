package com.xargsgrep.portknocker.fragment;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.xargsgrep.portknocker.R;

public class PortsFragment extends RoboSherlockFragment {
	
	@InjectView(R.id.port_list) LinearLayout linearLayout;
	
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
    	linearLayout.addView(row1);
    	linearLayout.addView(row2);
    }
    
    public LinearLayout getPortListLinearLayoutView() {
    	return linearLayout;
    }
    
}
