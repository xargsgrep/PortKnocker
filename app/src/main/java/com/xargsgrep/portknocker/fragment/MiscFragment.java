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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.adapter.ApplicationArrayAdapter;
import com.xargsgrep.portknocker.asynctask.RetrieveApplicationsAsyncTask;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Application;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.utils.StringUtils;

import java.util.List;

public class MiscFragment extends Fragment
{
    public static final String TAG = "MiscFragment";

    private DatabaseManager databaseManager;
    private String selectedLaunchIntent;
    private int delay = Host.DEFAULT_DELAY;
    private int tcpConnectTimeout = Host.DEFAULT_TCP_CONNECT_TIMEOUT;
    private ApplicationArrayAdapter applicationAdapter;

    public static MiscFragment newInstance(Long hostId)
    {
        MiscFragment fragment = new MiscFragment();
        if (hostId != null)
        {
            Bundle args = new Bundle();
            args.putLong(EditHostActivity.KEY_HOST_ID, hostId);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        databaseManager = new DatabaseManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.misc_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Spinner launchIntentSpinner = getLaunchIntentSpinner();
        SeekBar delaySeekBar = (SeekBar) getView().findViewById(R.id.delay_seekbar);
        SeekBar tcpConnectTimeoutSeekBar = (SeekBar) getView().findViewById(R.id.tcp_timeout_seekbar);
        final TextView delayDisplay = (TextView) getView().findViewById(R.id.delay_display);
        final TextView tcpConnectTimeoutDisplay = (TextView) getView().findViewById(R.id.tcp_timeout_display);

        launchIntentSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedLaunchIntent = ((ApplicationArrayAdapter) parent.getAdapter()).getItem(position).getIntent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        delaySeekBar.setOnSeekBarChangeListener(new FieldSettingSeekBarChangeListener("delay", delayDisplay));
        tcpConnectTimeoutSeekBar.setOnSeekBarChangeListener(
                new FieldSettingSeekBarChangeListener("tcpConnectTimeout", tcpConnectTimeoutDisplay)
        );

        Bundle args = getArguments();

        if (savedInstanceState != null)
        {
            delaySeekBar.setProgress(savedInstanceState.getInt("delay"));
            tcpConnectTimeoutSeekBar.setProgress(savedInstanceState.getInt("tcpConnectTimeout"));
            selectedLaunchIntent = savedInstanceState.getString("selectedLaunchIntent");
            launchIntentSpinner.setAdapter(applicationAdapter);
            setSelectedLaunchIntent();
        }
        else if (args != null)
        {
            Long hostId = args.getLong(EditHostActivity.KEY_HOST_ID);
            Host host = databaseManager.getHost(hostId);

            delay = host.getDelay();
            delaySeekBar.setProgress(host.getDelay());

            tcpConnectTimeout = host.getTcpConnectTimeout();
            tcpConnectTimeoutSeekBar.setProgress(host.getTcpConnectTimeout());

            selectedLaunchIntent = host.getLaunchIntentPackage();
        }
        else
        {
            // editing a new host
            delaySeekBar.setProgress(delay);
            tcpConnectTimeoutSeekBar.setProgress(tcpConnectTimeout);
        }

        if (applicationAdapter == null)
        {
            RetrieveApplicationsAsyncTask retrieveAppsTask = new RetrieveApplicationsAsyncTask(getActivity(), this);
            retrieveAppsTask.execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("delay", delay);
        outState.putInt("tcpConnectTimeout", tcpConnectTimeout);
        outState.putString("selectedLaunchIntent", selectedLaunchIntent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initializeLaunchIntentSpinner();
    }

    public int getDelay()
    {
        return delay;
    }

    public int getTcpConnectTimeout()
    {
        return tcpConnectTimeout;
    }

    public void initializeApplicationAdapter(List<Application> applications)
    {
        applicationAdapter = new ApplicationArrayAdapter(getActivity(), applications);
        initializeLaunchIntentSpinner();
    }

    private void initializeLaunchIntentSpinner()
    {
        if (applicationAdapter != null)
        {
            getLaunchIntentSpinner().setAdapter(applicationAdapter);
            setSelectedLaunchIntent();

            if (getView() != null)
            {
                getView().findViewById(R.id.launch_intent).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.launch_intent_progress_bar).setVisibility(View.GONE);
            }
        }
    }

    private void setSelectedLaunchIntent()
    {
        if (applicationAdapter != null && StringUtils.isNotBlank(selectedLaunchIntent))
        {
            for (int i = 0; i < applicationAdapter.getCount(); i++)
            {
                Application application = applicationAdapter.getItem(i);
                if (application.getIntent().equals(selectedLaunchIntent))
                {
                    getLaunchIntentSpinner().setSelection(i);
                    break;
                }
            }
        }
    }

    public String getSelectedLaunchIntent()
    {
        return selectedLaunchIntent;
    }

    private Spinner getLaunchIntentSpinner()
    {
        return (Spinner) getView().findViewById(R.id.launch_intent);
    }

    private class FieldSettingSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener
    {
        private String field;
        private TextView textView;

        private FieldSettingSeekBarChangeListener(String field, TextView textView)
        {
            this.field = field;
            this.textView = textView;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            textView.setText(String.valueOf(progress));
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            if (field.equals("delay"))
            {
                delay = seekBar.getProgress();
            }
            else if (field.equals("tcpConnectTimeout"))
            {
                tcpConnectTimeout = seekBar.getProgress();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
    }
}
