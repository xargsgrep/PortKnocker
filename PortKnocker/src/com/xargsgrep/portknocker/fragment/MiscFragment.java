package com.xargsgrep.portknocker.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MiscFragment extends SherlockFragment {
	
    HostDataManager hostDataManager;
    
    boolean savedInstanceState = false;
    String delayStr;
    String launchIntent;
    List<Application> applications;
	
	public static MiscFragment newInstance(Long hostId) {
		MiscFragment fragment = new MiscFragment();
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
    	Bundle args = getArguments();
    	
    	if (this.savedInstanceState) {
			delayEditText.setText(delayStr);
    	}
    	else if (args != null) {
    		Long hostId = args.getLong(EditHostActivity.HOST_ID_BUNDLE_KEY);
    		Host host = hostDataManager.getHost(hostId);
    		
			delayEditText.setText(new Integer(host.getDelay()).toString());
			launchIntent = host.getLaunchIntentPackage();
    	}
    	else {
			delayEditText.setText(Host.DEFAULT_DELAY);
    	}
    	
    	if (applications == null) {
	    	RetrieveApplicationsAsyncTask retrieveAppsTask = new RetrieveApplicationsAsyncTask(this);
	    	retrieveAppsTask.execute();
    	}
    	else {
    		initializeApplicationAdapter(applications);
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	delayStr = getDelayEditText().getText().toString();
    	if (getLaunchIntentSpinner().getSelectedItem() != null)
	    	launchIntent = ((Application) getLaunchIntentSpinner().getSelectedItem()).getIntent();
    	
    	savedInstanceState = true;
    }
    
    public void initializeApplicationAdapter(List<Application> applications) {
    	this.applications = applications;
    	
        ApplicationArrayAdapter applicationsAdapter = new ApplicationArrayAdapter(getActivity(), applications);
        Spinner launchAppSpinner = getLaunchIntentSpinner();
        launchAppSpinner.setAdapter(applicationsAdapter);
        
        if (launchIntent != null && launchIntent.length() > 0) {
	        for (int i=0; i<applicationsAdapter.getCount(); i++) {
	        	Application application = applicationsAdapter.getItem(i);
	        	if (application.getIntent().equals(launchIntent)) {
	        		launchAppSpinner.setSelection(i);
	        		break;
	        	}
	        }
        }
    }
    
    public EditText getDelayEditText() {
		return (EditText) getView().findViewById(R.id.delay_edit);
    }
    
    public Spinner getLaunchIntentSpinner() {
		return (Spinner) getView().findViewById(R.id.launch_intent);
    }
    
}
