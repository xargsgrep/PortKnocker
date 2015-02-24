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
package com.xargsgrep.portknocker.adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.asynctask.KnockerAsyncTask;
import com.xargsgrep.portknocker.fragment.HostListFragment;
import com.xargsgrep.portknocker.model.Host;

import java.util.List;

public class HostArrayAdapter extends ArrayAdapter<Host>
{
    private Fragment fragment;

    public HostArrayAdapter(Fragment fragment, List<Host> hosts)
    {
        super(fragment.getActivity(), -1, hosts);
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.host_row, null);

        TextView labelView = (TextView) view.findViewById(R.id.host_row_label);
        TextView hostnameView = (TextView) view.findViewById(R.id.host_row_hostname);
        TextView portsView = (TextView) view.findViewById(R.id.host_row_ports);

        Host host = getItem(position);

        labelView.setText(host.getLabel());
        hostnameView.setText(host.getHostname());
        portsView.setText(host.getPortsString());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.getActivity());
        if (sharedPreferences.getBoolean(fragment.getActivity().getString(R.string.pref_key_hide_ports), false))
        {
            portsView.setVisibility(View.GONE);
        }

        final int fPosition = position;
        view.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Host host = getItem(fPosition);
                KnockerAsyncTask knockerAsyncTask = new KnockerAsyncTask(fragment.getActivity(), host.getPorts().size());
                knockerAsyncTask.execute(host);
            }
        });

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.host_row_delete);
        deleteButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HostListFragment) fragment).setDeleteHostPosition(fPosition);
                ((HostListFragment) fragment).showDeleteDialog();
            }
        });

        ImageButton editButton = (ImageButton) view.findViewById(R.id.host_row_edit);
        editButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Host host = getItem(fPosition);
                Intent editHostIntent = new Intent(fragment.getActivity(), EditHostActivity.class);
                editHostIntent.putExtra(EditHostActivity.KEY_HOST_ID, host.getId());
                fragment.getActivity().startActivity(editHostIntent);
            }
        });

        return view;
    }
}
