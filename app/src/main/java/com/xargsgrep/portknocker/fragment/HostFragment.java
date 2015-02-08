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
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;

public class HostFragment extends Fragment
{
    public static final String TAG = "HostFragment";

    private DatabaseManager databaseManager;
    private String hostLabel;
    private String hostname;

    public static HostFragment newInstance(Long hostId)
    {
        HostFragment fragment = new HostFragment();
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
        databaseManager = new DatabaseManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.host_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        EditText hostLabelEdit = getHostLabelEditText();
        EditText hostnameEdit = getHostnameEditText();

        Bundle args = getArguments();
        if (args != null)
        {
            Long hostId = args.getLong(EditHostActivity.KEY_HOST_ID);
            Host host = databaseManager.getHost(hostId);
            hostLabelEdit.setText(host.getLabel());
            hostnameEdit.setText(host.getHostname());
        }

//        hostLabelEdit.setText(hostLabel);
//        hostnameEdit.setText(hostname);

        hostnameEdit.setFilters(new InputFilter[] {hostnameCharacterFilter});
    }

    @Override
    public void onPause()
    {
        super.onPause();
        hostLabel = getHostLabelEditText().getText().toString();
        hostname = getHostnameEditText().getText().toString();
    }

    public String getHostLabel()
    {
        return (getHostLabelEditText() != null) ? getHostLabelEditText().getText().toString() : hostLabel;
    }

    public String getHostname()
    {
        return (getHostnameEditText() != null) ? getHostnameEditText().getText().toString() : hostname;
    }

    private EditText getHostLabelEditText()
    {
        return (getView() != null) ? (EditText) getView().findViewById(R.id.host_label_edit) : null;
    }

    private EditText getHostnameEditText()
    {
        return (getView() != null) ? (EditText) getView().findViewById(R.id.host_name_edit) : null;
    }

    private InputFilter hostnameCharacterFilter = new InputFilter()
    {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            for (int i = start; i < end; i++)
            {
                char c = source.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != '.' && c != '-') return "";
            }
            return null;
        }
    };
}
