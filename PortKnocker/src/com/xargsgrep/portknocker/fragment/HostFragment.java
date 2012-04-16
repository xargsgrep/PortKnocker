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
	
	public static String hostLabel;
	public static String hostname;
	
	@InjectView(R.id.host_label_edit) EditText hostLabelEdit;
	@InjectView(R.id.host_name_edit) EditText hostnameEdit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hostLabel = "";
		hostname = "";
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.host_fragment, container, false);
    }

    @Override
    public void onPause() {
    	super.onPause();
    	hostLabel = hostLabelEdit.getText().toString();
    	hostname = hostnameEdit.getText().toString();
    }
	
}
