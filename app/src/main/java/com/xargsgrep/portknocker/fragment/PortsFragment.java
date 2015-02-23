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
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.adapter.PortArrayAdapter;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;

import java.util.ArrayList;
import java.util.List;

public class PortsFragment extends ListFragment
{
    public static final String TAG = "PortsFragment";

    private DatabaseManager databaseManager;
    private PortArrayAdapter portAdapter;
    private boolean savedInstanceState = false;

    public static PortsFragment newInstance(Long hostId)
    {
        PortsFragment fragment = new PortsFragment();
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
        setHasOptionsMenu(true);
        databaseManager = new DatabaseManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_view, container, false);
        View header = inflater.inflate(R.layout.ports_header, null);
        ((LinearLayout) view.findViewById(R.id.list_header)).addView(header);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ImageButton addButton = (ImageButton) view.findViewById(R.id.fab_image_button);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addPort();
            }
        });

        Bundle args = getArguments();

        List<Port> defaultPorts = new ArrayList<>();
        defaultPorts.add(new Port());
        defaultPorts.add(new Port());
        defaultPorts.add(new Port());

        if (args != null && !this.savedInstanceState)
        {
            // only restore state from args if onSaveInstanceState hasn't been invoked
            Long hostId = args.getLong(EditHostActivity.KEY_HOST_ID);
            Host host = databaseManager.getHost(hostId);
            List<Port> ports = (host.getPorts().size() > 0) ? host.getPorts() : defaultPorts;
            portAdapter = new PortArrayAdapter(getActivity(), ports);
            setListAdapter(portAdapter);
        }
        else if (portAdapter == null)
        {
            portAdapter = new PortArrayAdapter(getActivity(), defaultPorts);
            setListAdapter(portAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        savedInstanceState = true;
    }

    public void clearFoci()
    {
        ListView view = getListView();
        for (int i = 0; i < view.getChildCount(); i++)
        {
            View row = view.getChildAt(i);
            row.findViewById(R.id.port_row_port).clearFocus();
        }
    }

    private void addPort()
    {
        PortArrayAdapter adapter = (PortArrayAdapter) getListAdapter();
        adapter.add(new Port());
    }
}
