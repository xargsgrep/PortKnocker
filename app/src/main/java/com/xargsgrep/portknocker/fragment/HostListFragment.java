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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.activity.HostListActivity;
import com.xargsgrep.portknocker.adapter.HostArrayAdapter;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.widget.HostWidget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HostListFragment extends ListFragment
{
    public static final String TAG = "HostListFragment";

    private DatabaseManager databaseManager;
    private int deleteHostPosition;

    public static HostListFragment newInstance()
    {
        return new HostListFragment();
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
        return inflater.inflate(R.layout.list_view, container, false);
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
                Intent editHostIntent = new Intent(getActivity(), EditHostActivity.class);
                startActivity(editHostIntent);
            }
        });

        List<Host> hosts = databaseManager.getAllHosts();
        HostArrayAdapter hostAdapter = new HostArrayAdapter(this, hosts);
        setListAdapter(hostAdapter);
    }

    public void showDeleteDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.confirm_dialog_delete_host_title);
        dialogBuilder.setIcon(R.drawable.ic_alert);

        final int position = deleteHostPosition;
        dialogBuilder.setPositiveButton(R.string.confirm_dialog_confirm,
                new DialogInterface.OnClickListener()
                {
                    @SuppressWarnings("unchecked")
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Host host = (Host) getListAdapter().getItem(position);
                        databaseManager.deleteHost(host);
                        ((ArrayAdapter<Host>) getListAdapter()).remove(host);
                        HostWidget.updateAllAppWidgetsForHost(getActivity(), host.getId());
                    }
                }
        );
        dialogBuilder.setNegativeButton(R.string.confirm_dialog_cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) { }
                }
        );

        AlertDialog deleteDialog = dialogBuilder.create();
        ((HostListActivity) getActivity()).setDeleteDialog(deleteDialog);
        deleteDialog.show();
    }

    public void setDeleteHostPosition(int position)
    {
        this.deleteHostPosition = position;
    }

    public void refreshHosts()
    {
        List<Host> hosts = databaseManager.getAllHosts();
        setHosts(hosts);
    }

    public void sortByHostname()
    {
        sortHosts(new Comparator<Host>()
        {
            @Override
            public int compare(Host lhs, Host rhs)
            {
                return lhs.getHostname().compareTo(rhs.getHostname());
            }
        });
    }

    public void sortByLabel()
    {
        sortHosts(new Comparator<Host>()
        {
            @Override
            public int compare(Host lhs, Host rhs)
            {
                return lhs.getLabel().compareTo(rhs.getLabel());
            }
        });
    }

    public void sortByNewest()
    {
        sortHosts(new Comparator<Host>()
        {
            @Override
            public int compare(Host lhs, Host rhs)
            {
                return Long.valueOf(rhs.getId()).compareTo(lhs.getId());
            }
        });
    }

    private void sortHosts(Comparator<Host> comparator)
    {
        List<Host> hosts = databaseManager.getAllHosts();
        Collections.sort(hosts, comparator);
        setHosts(hosts);
    }

    private void setHosts(List<Host> hosts)
    {
        ((ArrayAdapter) getListAdapter()).clear();

        for (Host host : hosts)
        {
            ((ArrayAdapter) getListAdapter()).add(host);
        }
    }
}
