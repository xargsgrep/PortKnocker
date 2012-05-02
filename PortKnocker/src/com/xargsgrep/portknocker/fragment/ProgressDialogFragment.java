package com.xargsgrep.portknocker.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_INDETERMINATE = "indeterminate";
	private static final String KEY_STYLE = "style";
	
	ProgressDialog dialog;
	
    public static ProgressDialogFragment newInstance(String message, boolean indeterminate, int style) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MESSAGE, message);
        args.putBoolean(KEY_INDETERMINATE, indeterminate);
        args.putInt(KEY_STYLE, style);
        fragment.setArguments(args);
        return fragment;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.dialog = new ProgressDialog(getActivity());
		this.dialog.setMessage(getArguments().getString(KEY_MESSAGE));
		this.dialog.setIndeterminate(getArguments().getBoolean(KEY_INDETERMINATE));
		this.dialog.setProgressStyle(getArguments().getInt(KEY_STYLE));
		return this.dialog;
	}
	
	@Override
	public Dialog getDialog() {
		return this.dialog;
	}
}
