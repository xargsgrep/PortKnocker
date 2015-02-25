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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.filter.HostnameInputFilter;
import com.xargsgrep.portknocker.model.Host;

public class HostFragment extends Fragment
{
    public static final String TAG = "HostFragment";

    private static final InputFilter HOSTNAME_INPUT_FILTER = new HostnameInputFilter();

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
        setRetainInstance(true);
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

        hostLabelEdit.addTextChangedListener(new FieldSettingTextWatcher("hostLabel"));
        hostnameEdit.addTextChangedListener(new FieldSettingTextWatcher("hostname"));

        Bundle args = getArguments();

        if (savedInstanceState != null)
        {
            hostLabelEdit.setText(savedInstanceState.getString("hostLabel"));
            hostnameEdit.setText(savedInstanceState.getString("hostname"));
        }
        else if (args != null)
        {
            Long hostId = args.getLong(EditHostActivity.KEY_HOST_ID);
            Host host = databaseManager.getHost(hostId);
            hostLabelEdit.setText(host.getLabel());
            hostnameEdit.setText(host.getHostname());
        }

        hostnameEdit.setFilters(new InputFilter[] {HOSTNAME_INPUT_FILTER});
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("hostLabel", hostLabel);
        outState.putString("hostname", hostname);
    }

    public String getHostLabel()
    {
        return hostLabel;
    }

    public String getHostname()
    {
        return hostname;
    }

    private EditText getHostLabelEditText()
    {
        return (EditText) getView().findViewById(R.id.host_label_edit);
    }

    private EditText getHostnameEditText()
    {
        return (EditText) getView().findViewById(R.id.host_name_edit);
    }

    private class FieldSettingTextWatcher implements TextWatcher
    {
        private String field;

        private FieldSettingTextWatcher(String field)
        {
            this.field = field;
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if (field.equals("hostLabel"))
            {
                hostLabel = s.toString();
            }
            else if (field.equals("hostname"))
            {
                hostname = s.toString();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    }
}
