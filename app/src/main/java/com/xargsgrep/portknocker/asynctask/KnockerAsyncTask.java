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
package com.xargsgrep.portknocker.asynctask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.Toast;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.asynctask.Knocker.KnockResult;
import com.xargsgrep.portknocker.fragment.ProgressDialogFragment;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.utils.StringUtils;

public class KnockerAsyncTask extends AsyncTask<Host, Integer, KnockResult>
{
    private FragmentActivity activity;
    private int progressMax;

    public KnockerAsyncTask(FragmentActivity activity, int progressMax)
    {
        this.activity = activity;
        this.progressMax = progressMax;
    }

    @Override
    protected void onPreExecute()
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(ProgressDialogFragment.TAG);
        if (prev != null) ft.remove(prev);
        ft.addToBackStack(null);

        ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(this, activity.getString(R.string.progress_dialog_sending_packets), false, ProgressDialog.STYLE_HORIZONTAL, progressMax);
        dialogFragment.setCancelable(true);
        dialogFragment.show(ft, ProgressDialogFragment.TAG);
    }

    @Override
    protected KnockResult doInBackground(Host... params)
    {
        Host host = params[0];
        // pass in 'this' so the progress dialog can be updated
        return Knocker.doKnock(host, this);
    }

    @Override
    protected void onPostExecute(KnockResult result)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        Fragment dialog = fragmentManager.findFragmentByTag(ProgressDialogFragment.TAG);
        if (dialog != null) ((ProgressDialogFragment) dialog).dismiss();

        if (result.isSuccess())
        {
            if (StringUtils.isNotBlank(result.getLaunchIntentPackage()))
            {
                Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage(result.getLaunchIntentPackage());
                if (launchIntent == null)
                {
                    showToast("Unable to launch intent " + result.getLaunchIntentPackage());
                }
                else
                {
                    activity.startActivity(launchIntent);
                }
            }
            else
            {
                showToast(activity.getString(R.string.toast_msg_knocking_complete));
            }
        }
        else
        {
            showToast(activity.getString(R.string.toast_msg_knocking_failed) + result.getError());
        }
    }

    @Override
    protected void onCancelled()
    {
        super.onCancelled();
        showToast(activity.getString(R.string.toast_msg_knocking_canceled));
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment dialogFragment = fragmentManager.findFragmentByTag(ProgressDialogFragment.TAG);
        if (dialogFragment != null)
            ((ProgressDialogFragment) dialogFragment).setProgress(values[0]);
    }

    public void doPublishProgress(Integer value)
    {
        publishProgress(value);
    }

    private void showToast(String text)
    {
        Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
