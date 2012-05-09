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
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;

public class HostFragment extends SherlockFragment {
	
	public static final String TAG = "HostFragment";
	
    DatabaseManager databaseManager;
    
    boolean savedInstanceState = false;
    String hostLabel;
    String hostname;
	
	public static HostFragment newInstance(Long hostId) {
		HostFragment fragment = new HostFragment();
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
		databaseManager = new DatabaseManager(getActivity());
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
    	
    	if (this.savedInstanceState) {
    		hostLabelEdit.setText(hostLabel);
    		hostnameEdit.setText(hostname);
    	}
    	else if (args != null) {
    		Long hostId = args.getLong(EditHostActivity.KEY_HOST_ID);
    		Host host = databaseManager.getHost(hostId);
    		hostLabelEdit.setText(host.getLabel());
    		hostnameEdit.setText(host.getHostname());
    	}
    	
		hostnameEdit.setFilters(new InputFilter[] { hostnameCharacterFilter });
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
   
    	hostLabel = getHostLabelEditText().getText().toString();
    	hostname = getHostnameEditText().getText().toString();
    	
    	savedInstanceState = true;
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
				if (!Character.isLetterOrDigit(c) && c != '.' && c != '-') return "";
			}
			return null;
		}
	};
}
