package com.xargsgrep.portknocker.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.adapter.ApplicationArrayAdapter;
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
    	} else if (args != null) {
    		Long hostId = args.getLong(EditHostActivity.HOST_ID_BUNDLE_KEY);
    		Host host = hostDataManager.getHost(hostId);
    		
			delayEditText.setText(new Integer(host.getDelay()).toString());
			launchIntent = host.getLaunchIntentPackage();
    	}
    	
    	if (applications == null) {
	    	RetrieveInstalledApplicationsTask retrieveAppsTask = new RetrieveInstalledApplicationsTask();
	    	retrieveAppsTask.execute();
    	} else {
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
    
    private void initializeApplicationAdapter(List<Application> apps) {
        ApplicationArrayAdapter applicationsAdapter = new ApplicationArrayAdapter(getActivity(), apps);
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
    
    private class RetrieveInstalledApplicationsTask extends AsyncTask<Void, Void, List<Application>> {
    	
    	@Override
    	protected void onPreExecute() {
	    	FragmentTransaction ft = getFragmentManager().beginTransaction();
	    	Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    	if (prev != null) ft.remove(prev);
	    	ft.addToBackStack(null);
	    	
			ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(getString(R.string.progress_dialog_retrieving_applications));
			dialogFragment.show(ft, "dialog");
    	}
    	
		@Override
		protected List<Application> doInBackground(Void... params) {
			PackageManager packageManager = getActivity().getPackageManager();
			List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
			
	        List<Application> applications = new ArrayList<Application>();
	        for (ApplicationInfo applicationInfo : installedApplications) {
	        	if (isSystemPackage(applicationInfo) || packageManager.getLaunchIntentForPackage(applicationInfo.packageName) == null) continue;
	        	applications.add(new Application(packageManager.getApplicationLabel(applicationInfo).toString(), applicationInfo.loadIcon(packageManager), applicationInfo.packageName));
	        }
	        
	        Collections.sort(applications, new Comparator<Application>() {
				@Override
				public int compare(Application app1, Application app2) {
					return app1.getLabel().compareTo(app2.getLabel());
				}
			});
	        applications.add(0, new Application("None", null, ""));
	        
	        return applications;
		}
		
		@Override
		protected void onPostExecute(List<Application> apps) {
			applications = apps;
			initializeApplicationAdapter(apps);
	    	Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    	if (prev != null) ((ProgressDialogFragment) prev).dismiss();
		}

	    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
	        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	    }
    }

}
