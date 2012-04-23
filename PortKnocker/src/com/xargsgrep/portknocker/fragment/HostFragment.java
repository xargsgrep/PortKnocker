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

public class HostFragment extends SherlockFragment {
	
	public static HostFragment newInstance(Long hostId) {
		HostFragment fragment = new HostFragment();
		if (hostId != null) {
			Bundle args = new Bundle();
			args.putLong("hostId", hostId);
			fragment.setArguments(args);
		}
		return fragment;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.host_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
    	InputFilter spaceInputFilter = new InputFilter() {
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
		
    	EditText hostnameEdit = (EditText) view.findViewById(R.id.host_name_edit);
		hostnameEdit.setFilters(new InputFilter[] { spaceInputFilter });
    }
}
