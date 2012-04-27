package com.xargsgrep.portknocker.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	
    public static ProgressDialogFragment newInstance(String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        //fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getArguments().getString("message"));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		return dialog;
	}
	
}
