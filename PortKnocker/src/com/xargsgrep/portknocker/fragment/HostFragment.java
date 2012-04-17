package com.xargsgrep.portknocker.fragment;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.xargsgrep.portknocker.R;

public class HostFragment extends RoboSherlockFragment {
	
	@InjectView(R.id.host_label_edit) EditText hostLabelEdit;
	@InjectView(R.id.host_name_edit) EditText hostnameEdit;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.host_fragment, container, false);
    }

    public EditText getHostLabelEditTextView() {
    	return hostLabelEdit;
    }
    
    public EditText getHostnameEditTextView() {
    	return hostnameEdit;
    }
    
}
