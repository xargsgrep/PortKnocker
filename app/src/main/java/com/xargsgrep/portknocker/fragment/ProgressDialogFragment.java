/*
 *  Copyright 2014 Ahsan Rabbani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xargsgrep.portknocker.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment
{
    public static final String TAG = "ProgressDialogFragment";

    private static final String KEY_MESSAGE = "message";
    private static final String KEY_INDETERMINATE = "indeterminate";
    private static final String KEY_STYLE = "style";
    private static final String KEY_MAX = "max";

    private ProgressDialog mDialog;
    private AsyncTask<?, ?, ?> mAsyncTask;
    private int mDialogProgress = 0;

    public static ProgressDialogFragment newInstance(AsyncTask<?, ?, ?> asyncTask, String message, boolean indeterminate, int style, int max)
    {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.mAsyncTask = asyncTask;

        Bundle args = new Bundle();
        args.putString(KEY_MESSAGE, message);
        args.putBoolean(KEY_INDETERMINATE, indeterminate);
        args.putInt(KEY_STYLE, style);
        args.putInt(KEY_MAX, max);
        fragment.setArguments(args);

        return fragment;
    }

    public static ProgressDialogFragment newInstance(String message, boolean indeterminate, int style)
    {
        return newInstance(null, message, indeterminate, style, 100);
    }

    @Override
    public Dialog getDialog()
    {
        return mDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage(getArguments().getString(KEY_MESSAGE));
        mDialog.setIndeterminate(getArguments().getBoolean(KEY_INDETERMINATE));
        mDialog.setProgressStyle(getArguments().getInt(KEY_STYLE));
        mDialog.setMax(getArguments().getInt(KEY_MAX));
        return mDialog;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mDialog.setProgress(mDialogProgress);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mDialogProgress = mDialog.getProgress();
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        if (isCancelable() && mAsyncTask != null) mAsyncTask.cancel(true);
    }

    @Override
    public void onDestroyView()
    {
        // NOTE: without this the dialog doesn't seem to get recreated on screen rotation
        if (mDialog != null && getRetainInstance())
        {
            mDialog.setOnDismissListener(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mDialog = null;
        mAsyncTask = null;
    }

    public void setProgress(int progress)
    {
        if (mDialog != null) mDialog.setProgress(progress);
    }
}
