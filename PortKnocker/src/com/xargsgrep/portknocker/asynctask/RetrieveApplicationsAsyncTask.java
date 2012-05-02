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
import android.support.v4.app.FragmentTransaction;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.ProgressDialogFragment;
import com.xargsgrep.portknocker.model.Application;

public class RetrieveApplicationsAsyncTask extends AsyncTask<Void, Void, List<Application>> {
	
	private static final String DIALOG_FRAGMENT_TAG = "dialog";
	
	Fragment fragment;
	
	public RetrieveApplicationsAsyncTask(Fragment fragment) {
		this.fragment = fragment;
	}
    	
	@Override
	protected void onPreExecute() {
    	FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
    	Fragment prev = fragment.getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
    	if (prev != null) ft.remove(prev);
    	ft.addToBackStack(null);
    	
		ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(fragment.getString(R.string.progress_dialog_retrieving_applications), true, ProgressDialog.STYLE_SPINNER);
		dialogFragment.setCancelable(false);
		dialogFragment.show(ft, DIALOG_FRAGMENT_TAG);
	}
    	
	@Override
	protected List<Application> doInBackground(Void... params) {
		PackageManager packageManager = fragment.getActivity().getPackageManager();
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
		((MiscFragment) fragment).initializeApplicationAdapter(applications);
    	Fragment dialog = fragment.getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
    	if (dialog != null) ((ProgressDialogFragment) dialog).dismiss();
	}

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
}
