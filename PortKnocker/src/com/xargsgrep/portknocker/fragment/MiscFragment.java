package com.xargsgrep.portknocker.fragment;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.xargsgrep.portknocker.R;

public class MiscFragment extends RoboSherlockFragment {
	
	@InjectView(R.id.delay_edit) EditText delayEdit;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.misc_fragment, container, false);
    }

    public EditText getDelayEdit() {
    	return delayEdit;
    }
}
