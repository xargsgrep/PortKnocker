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
	
	Fragment fragment;
	
	public KnockerAsyncTask(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	protected void onPreExecute() {
    	FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
    	Fragment prev = fragment.getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
    	if (prev != null) ft.remove(prev);
    	ft.addToBackStack(null);
    	
		ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(this, fragment.getString(R.string.progress_dialog_sending_packets), false, ProgressDialog.STYLE_HORIZONTAL);
		dialogFragment.setCancelable(true);
		dialogFragment.show(ft, ProgressDialogFragment.TAG);
	}
	
	@Override
	protected KnockResult doInBackground(Host... params) {
		Host host = params[0];
		
		while (true) {
			// sometimes the ui thread has not instantiated the dialog when it reaches this point, so wait until it has been
			ProgressDialogFragment dialogFragment = (ProgressDialogFragment) fragment.getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
			if (dialogFragment != null && dialogFragment.getDialog() != null) {
				((ProgressDialog) dialogFragment.getDialog()).setMax(host.getPorts().size());
				break;
			}
		}
		
		// pass in 'this' so the progress dialog can be updated
		return Knocker.doKnock(host, this);
	}
	
	@Override
	protected void onPostExecute(KnockResult result) {
    	Fragment dialog = fragment.getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
    	if (dialog != null) ((ProgressDialogFragment) dialog).dismiss();
    	
		if (result.isSuccess()) {
			if (StringUtils.isNotBlank(result.getLaunchIntentPackage())) {
				Intent launchIntent = fragment.getActivity().getPackageManager().getLaunchIntentForPackage(result.getLaunchIntentPackage());
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
	protected void onCancelled() {
		super.onCancelled();
		showToast("Knocking cancelled");
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
    	Fragment dialogFragment = fragment.getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
    	if (dialogFragment!= null) ((ProgressDialogFragment) dialogFragment).setProgress(values[0]);
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
