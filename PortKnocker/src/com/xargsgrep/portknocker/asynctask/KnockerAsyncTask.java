package com.xargsgrep.portknocker.asynctask;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.fragment.ProgressDialogFragment;
import com.xargsgrep.portknocker.model.Host;

public class KnockerAsyncTask extends AsyncTask<Host, Void, Boolean> {
	
	FragmentActivity fragmentActivity;
	
	public KnockerAsyncTask(FragmentActivity fragmentActivity) {
		this.fragmentActivity = fragmentActivity;
	}

	@Override
	protected void onPreExecute() {
    	FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
    	Fragment prev = fragmentActivity.getSupportFragmentManager().findFragmentByTag("dialog");
    	if (prev != null) ft.remove(prev);
    	ft.addToBackStack(null);
    	
		ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(fragmentActivity.getString(R.string.progress_dialog_sending_packets));
		dialogFragment.show(ft, "dialog");
	}
	
	@Override
	protected Boolean doInBackground(Host... params) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { }
		return null;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
    	Fragment prev = fragmentActivity.getSupportFragmentManager().findFragmentByTag("dialog");
    	if (prev != null) ((ProgressDialogFragment) prev).dismiss();
	}

}
