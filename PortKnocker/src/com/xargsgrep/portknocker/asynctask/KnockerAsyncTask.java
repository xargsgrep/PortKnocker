package com.xargsgrep.portknocker.asynctask;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.Toast;

import com.xargsgrep.portknocker.Knocker;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.ProgressDialogFragment;
import com.xargsgrep.portknocker.model.Host;

public class KnockerAsyncTask extends AsyncTask<Host, Void, Boolean> {
	
	private static final String DIALOG_FRAGMENT_TAG = "dialog";
	
	Fragment fragment;
	String launchIntentPackage;
	
	public KnockerAsyncTask(Fragment fragment, String launchIntentPackage) {
		this.fragment = fragment;
		this.launchIntentPackage = launchIntentPackage;
	}

	@Override
	protected void onPreExecute() {
    	FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
    	Fragment prev = fragment.getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
    	if (prev != null) ft.remove(prev);
    	ft.addToBackStack(null);
    	
		ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(fragment.getString(R.string.progress_dialog_sending_packets));
		dialogFragment.show(ft, DIALOG_FRAGMENT_TAG);
	}
	
	@Override
	protected Boolean doInBackground(Host... params) {
		return Knocker.doKnock(params[0]);
	}
	
	@Override
	protected void onPostExecute(Boolean success) {
    	Fragment dialog = fragment.getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
    	if (dialog != null) ((ProgressDialogFragment) dialog).dismiss();
    	
		if (success) {
			if (launchIntentPackage != null && launchIntentPackage.length() > 0) {
				Intent launchIntent = fragment.getActivity().getPackageManager().getLaunchIntentForPackage(launchIntentPackage);
				fragment.getActivity().startActivity(launchIntent);
			}
			else {
				showToast("Knocking succeeded!");
			}
		}
		else {
			showToast("Knocking failed");
		}
	}
	
	private void showToast(String text) {
		Toast toast = Toast.makeText(fragment.getActivity(), text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

}
