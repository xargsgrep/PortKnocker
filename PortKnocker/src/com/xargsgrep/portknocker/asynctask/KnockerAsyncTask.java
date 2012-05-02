package com.xargsgrep.portknocker.asynctask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.Toast;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.asynctask.Knocker.KnockResult;
import com.xargsgrep.portknocker.fragment.ProgressDialogFragment;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.utils.StringUtils;

public class KnockerAsyncTask extends AsyncTask<Host, Integer, KnockResult> {
	
	private static final String DIALOG_FRAGMENT_TAG = "dialog";
	
	Fragment fragment;
	String launchIntentPackage;
	ProgressDialogFragment dialogFragment;
	
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
    	
		dialogFragment = ProgressDialogFragment.newInstance(fragment.getString(R.string.progress_dialog_sending_packets), false, ProgressDialog.STYLE_HORIZONTAL);
		dialogFragment.setCancelable(true);
		dialogFragment.show(ft, DIALOG_FRAGMENT_TAG);
	}
	
	@Override
	protected KnockResult doInBackground(Host... params) {
		Host host = params[0];
		
		while (true) {
			// sometimes the ui thread has not instantiated the dialog when it reaches this point, so wait until it has been
			if (dialogFragment.getDialog() != null) {
				((ProgressDialog) dialogFragment.getDialog()).setMax(host.getPorts().size());
				break;
			}
		}
		
		// pass in 'this' so we can update the progress dialog
		return Knocker.doKnock(host, this);
	}
	
	@Override
	protected void onPostExecute(KnockResult result) {
    	Fragment dialog = fragment.getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
    	if (dialog != null) ((ProgressDialogFragment) dialog).dismiss();
    	
		if (result.isSuccess()) {
			if (StringUtils.isNotBlank(launchIntentPackage)) {
				Intent launchIntent = fragment.getActivity().getPackageManager().getLaunchIntentForPackage(launchIntentPackage);
				fragment.getActivity().startActivity(launchIntent);
			}
			else {
				showToast("Knocking complete!");
			}
		}
		else {
			showToast("Knocking failed: " + result.getError());
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		((ProgressDialog) dialogFragment.getDialog()).setProgress(values[0]);
	}
	
	public void doPublishProgress(Integer value) {
		publishProgress(value);
	}
	
	private void showToast(String text) {
		Toast toast = Toast.makeText(fragment.getActivity(), text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

}
