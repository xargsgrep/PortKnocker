package com.xargsgrep.portknocker.asynctask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.ProgressDialogFragment;
import com.xargsgrep.portknocker.model.Application;

public class RetrieveApplicationsAsyncTask extends AsyncTask<Void, Void, List<Application>> {
	
	FragmentActivity activity;
	Fragment fragment;
	
	public RetrieveApplicationsAsyncTask(FragmentActivity activity, Fragment fragment) {
		this.activity = activity;
		this.fragment = fragment;
	}
    	
	@Override
	protected void onPreExecute() {
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		
    	FragmentTransaction ft = fragmentManager.beginTransaction();
    	Fragment prev = fragmentManager.findFragmentByTag(ProgressDialogFragment.TAG);
    	if (prev != null) ft.remove(prev);
    	ft.addToBackStack(null);
    	
		ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(activity.getString(R.string.progress_dialog_retrieving_applications), true, ProgressDialog.STYLE_SPINNER);
		dialogFragment.setCancelable(false);
		dialogFragment.show(ft, ProgressDialogFragment.TAG);
	}
    	
	@Override
	protected List<Application> doInBackground(Void... params) {
		PackageManager packageManager = activity.getPackageManager();
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
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		
		((MiscFragment) fragment).initializeApplicationAdapter(applications);
    	Fragment dialog = fragmentManager.findFragmentByTag(ProgressDialogFragment.TAG);
    	if (dialog != null) ((ProgressDialogFragment) dialog).dismiss();
	}

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
    
}
