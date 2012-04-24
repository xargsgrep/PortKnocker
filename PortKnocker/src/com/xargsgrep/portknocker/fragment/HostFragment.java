package com.xargsgrep.portknocker.fragment;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;

public class HostFragment extends SherlockFragment {
	
	private static final String HOST_LABEL_BUNDLE_KEY = "hostLabel";
	private static final String HOST_NAME_BUNDLE_KEY = "hostname";
	
    HostDataManager hostDataManager;
	
	public static HostFragment newInstance(Long hostId) {
		HostFragment fragment = new HostFragment();
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
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.host_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
    	EditText hostLabelEdit = getHostLabelEditText();
    	EditText hostnameEdit = getHostnameEditText();
    	
    	Bundle args = getArguments();
    	
    	if (savedInstanceState != null) {
    		hostLabelEdit.setText(savedInstanceState.getString(HOST_LABEL_BUNDLE_KEY));
    		hostnameEdit.setText(savedInstanceState.getString(HOST_NAME_BUNDLE_KEY));
    	} else if (args != null) {
    		Long hostId = args.getLong(EditHostActivity.HOST_ID_BUNDLE_KEY);
    		Host host = hostDataManager.getHost(hostId);
    		hostLabelEdit.setText(host.getLabel());
    		hostnameEdit.setText(host.getHostname());
    	}
    	
		hostnameEdit.setFilters(new InputFilter[] { hostnameCharacterFilter });
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
   
    	outState.putString(HOST_LABEL_BUNDLE_KEY, getHostLabelEditText().getText().toString());
    	outState.putString(HOST_NAME_BUNDLE_KEY, getHostnameEditText().getText().toString());
    }
    
    public EditText getHostLabelEditText() {
    	return (EditText) getView().findViewById(R.id.host_label_edit);
    }
    
    public EditText getHostnameEditText() {
    	return (EditText) getView().findViewById(R.id.host_name_edit);
    }
    
	InputFilter hostnameCharacterFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			for (int i=start; i<end; i++) {
				char c = source.charAt(i);
				if (!Character.isLetterOrDigit(c) && c != '.' && c != '-') {
					return "";
				}
			}
			return null;
		}
	};
}
