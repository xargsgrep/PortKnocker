package com.xargsgrep.portknocker.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.adapter.ApplicationArrayAdapter;
import com.xargsgrep.portknocker.asynctask.RetrieveApplicationsAsyncTask;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Application;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.utils.StringUtils;

public class MiscFragment extends SherlockFragment {
	
    HostDataManager hostDataManager;
    
    String delayStr;
    String selectedLaunchIntent;
    ApplicationArrayAdapter applicationAdapter;
    boolean savedInstanceState = false;
	
	public static MiscFragment newInstance(Long hostId) {
		MiscFragment fragment = new MiscFragment();
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
		hostDataManager = new HostDataManager(getActivity());
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.misc_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
		EditText delayEditText = getDelayEditText();
        Spinner launchIntentSpinner = getLaunchIntentSpinner();
        
        launchIntentSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedLaunchIntent = ((ApplicationArrayAdapter) parent.getAdapter()).getItem(position).getIntent();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
        });
    	
    	Bundle args = getArguments();
    	
    	if (this.savedInstanceState) {
			delayEditText.setText(delayStr);
        	launchIntentSpinner.setAdapter(applicationAdapter);
        	setSelectedLaunchIntent();
    	}
    	else if (args != null) {
    		Long hostId = args.getLong(EditHostActivity.KEY_HOST_ID);
    		Host host = hostDataManager.getHost(hostId);
    		
			delayEditText.setText(new Integer(host.getDelay()).toString());
			selectedLaunchIntent = host.getLaunchIntentPackage();
    	}
    	else {
    		// editing a new host
			delayEditText.setText(new Integer(Host.DEFAULT_DELAY).toString());
    	}
    	
    	if (applicationAdapter == null) {
	    	RetrieveApplicationsAsyncTask retrieveAppsTask = new RetrieveApplicationsAsyncTask(this);
	    	retrieveAppsTask.execute();
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	delayStr = getDelayEditText().getText().toString();
    	savedInstanceState = true;
    }
    
    public void initializeApplicationAdapter(List<Application> applications) {
        applicationAdapter = new ApplicationArrayAdapter(getActivity(), applications);
        getLaunchIntentSpinner().setAdapter(applicationAdapter);
        setSelectedLaunchIntent();
    }
    
    private void setSelectedLaunchIntent() {
        if (applicationAdapter != null && StringUtils.isNotBlank(selectedLaunchIntent)) {
	        for (int i=0; i<applicationAdapter.getCount(); i++) {
	        	Application application = applicationAdapter.getItem(i);
	        	if (application.getIntent().equals(selectedLaunchIntent)) {
	        		getLaunchIntentSpinner().setSelection(i);
	        		break;
	        	}
	        }
        }
    }
    
    public String getSelectedLaunchIntent() {
    	return selectedLaunchIntent;
    }
    
    public EditText getDelayEditText() {
		return (EditText) getView().findViewById(R.id.delay_edit);
    }
    
    private Spinner getLaunchIntentSpinner() {
		return (Spinner) getView().findViewById(R.id.launch_intent);
    }
    
}
