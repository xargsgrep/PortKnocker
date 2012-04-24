package com.xargsgrep.portknocker.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    	
    	String selectedLaunchIntent = null;
    	Bundle args = getArguments();
    	if (args != null) {
    		Long hostId = args.getLong(EditHostActivity.HOST_ID_BUNDLE_KEY);
    		Host host = hostDataManager.getHost(hostId);
    		
			EditText delayEditTextView = (EditText) view.findViewById(R.id.delay_edit);
			delayEditTextView.setText(new Integer(host.getDelay()).toString());
			selectedLaunchIntent = host.getLaunchIntent();
    	}
    	
    	RetrieveInstalledApplicationsTask retrieveAppsTask = new RetrieveInstalledApplicationsTask(getActivity(), selectedLaunchIntent);
    	retrieveAppsTask.execute();
    }
    
    private class RetrieveInstalledApplicationsTask extends AsyncTask<Void, Void, List<Application>> {
    	Context context;
    	ProgressDialog dialog;
    	String selectedLaunchIntent;
    	
    	public RetrieveInstalledApplicationsTask(Context context, String selectedLaunchIntent) {
    		this.context = context;
    		this.dialog = new ProgressDialog(context);
    		this.selectedLaunchIntent = selectedLaunchIntent;
		}
    	
    	@Override
    	protected void onPreExecute() {
    		dialog.setMessage(context.getResources().getString(R.string.progress_dialog_retrieving_applications));
    		dialog.show();
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
		protected void onPostExecute(List<Application> applications) {
	        ArrayAdapter<Application> adapter = new ApplicationArrayAdapter(getActivity(), applications);
	        Spinner launchAppSpinner = (Spinner) getView().findViewById(R.id.launch_app);
	        launchAppSpinner.setAdapter(adapter);
	        
	        if (selectedLaunchIntent != null) {
		        for (int i=0; i<adapter.getCount(); i++) {
		        	Application application = adapter.getItem(i);
		        	if (application.getIntent().equals(selectedLaunchIntent)) {
		        		launchAppSpinner.setSelection(i);
		        		break;
		        	}
		        }
	        }
	        
	        if (dialog.isShowing()) dialog.dismiss();
		}

	    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
	        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	    }
    }

}
