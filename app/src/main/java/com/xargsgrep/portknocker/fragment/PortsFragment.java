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

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.adapter.PortArrayAdapter;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;

public class PortsFragment extends ListFragment
{
    public static final String TAG = "PortsFragment";

    private DatabaseManager databaseManager;
    private PortArrayAdapter portAdapter;
    private List<Port> ports = new ArrayList<>(10);
    private String foo;
//    private boolean savedInstanceState = false;

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
        setHasOptionsMenu(true);
        databaseManager = new DatabaseManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_view, container, false);
        View header = inflater.inflate(R.layout.ports_header, null);
        ((LinearLayout) view).addView(header, 0);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        List<Port> defaultPorts = new ArrayList<>();
        defaultPorts.add(new Port());
        defaultPorts.add(new Port());
        defaultPorts.add(new Port());

//        if (args != null && !this.savedInstanceState)
        if (args != null)
        {
            // only restore state from args if onSaveInstanceState hasn't been invoked
            Long hostId = args.getLong(EditHostActivity.KEY_HOST_ID);
            Host host = databaseManager.getHost(hostId);
//            if (host.getPorts().size() > 0)
//            {
//                ports = host.getPorts();
//            }
//            ports = (host.getPorts().size() > 0) ? host.getPorts() : ports;
            portAdapter = new PortArrayAdapter(getActivity(), host.getPorts());
            setListAdapter(portAdapter);
        }
        else if (portAdapter == null)
        {
            portAdapter = new PortArrayAdapter(getActivity(), defaultPorts);
            setListAdapter(portAdapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, EditHostActivity.MENU_ITEM_ADD_PORT, 0, "Add Port").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case EditHostActivity.MENU_ITEM_ADD_PORT:
                addPort();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        ports = portAdapter.getPorts();
        foo = "foo";
        System.out.println();
    }

    public List<Port> getPorts()
    {
        foo.length();
        return portAdapter.getPorts();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState)
//    {
//        super.onSaveInstanceState(outState);
//        savedInstanceState = true;
//    }

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
