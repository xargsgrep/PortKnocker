package com.xargsgrep.portknocker.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.adapter.ApplicationArrayAdapter;
import com.xargsgrep.portknocker.model.Application;

public class MiscFragment extends SherlockFragment {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.misc_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
    	PackageManager packageManager = getActivity().getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        
        List<Application> applications = new ArrayList<Application>();
        for (ApplicationInfo applicationInfo : installedApplications) {
        	if (isSystemPackage(applicationInfo) || packageManager.getLaunchIntentForPackage(applicationInfo.packageName) == null) continue;
        	applications.add(new Application(packageManager.getApplicationLabel(applicationInfo).toString(), applicationInfo.packageName));
        }
        
        Collections.sort(applications, new Comparator<Application>() {
			@Override
			public int compare(Application app1, Application app2) {
				return app1.getLabel().compareTo(app2.getLabel());
			}
		});
        applications.add(0, new Application("None", ""));
        
        ArrayAdapter<Application> adapter = new ApplicationArrayAdapter(getActivity(), applications);
        Spinner launchAppSpinner = (Spinner) view.findViewById(R.id.launch_app);
        launchAppSpinner.setAdapter(adapter);
    }
    
    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

}
