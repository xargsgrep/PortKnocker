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

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.model.Port.Protocol;
import com.xargsgrep.portknocker.utils.StringUtils;

import java.util.List;

public class PortArrayAdapter extends ArrayAdapter<Port>
{
    private Context context;
    private List<Port> ports;

    public PortArrayAdapter(Context context, List<Port> ports)
    {
        super(context, -1, ports);
        this.context = context;
        this.ports = ports;
    }

    @Override
    public int getCount()
    {
        return ports.size();
    }

    @Override
    public Port getItem(int position)
    {
        return ports.get(position);
    }

    public List<Port> getPorts()
    {
        return ports;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.port_row, null);

        EditText portView = (EditText) view.findViewById(R.id.port_row_port);
        Spinner protocolSpinner = (Spinner) view.findViewById(R.id.port_row_protocol);
        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.port_row_delete);

        ArrayAdapter<Protocol> protocolAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Protocol.values());
        protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        protocolSpinner.setAdapter(protocolAdapter);

        Port port = ports.get(position);
        portView.setText((port.getPort() > -1) ? Integer.valueOf(port.getPort()).toString() : "");
        protocolSpinner.setSelection(port.getProtocol().ordinal());

        final int fPosition = position;
        portView.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    String portStr = ((EditText) view).getText().toString();
                    // if fPosition is out of bounds, it means the last row was focused and a row before it was deleted
                    ports.get((fPosition >= getCount())
                            ? fPosition - 1
                            : fPosition).setPort((StringUtils.isNotBlank(portStr)) ? Integer.parseInt(portStr) : -1);
                }
            }
        });

        protocolSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                ports.get(fPosition).setProtocol(Protocol.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        deleteButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getCount() == 1)
                {
                    Toast toast = Toast.makeText(getContext(), context.getString(R.string.toast_msg_delete_all_ports), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
                else
                {
                    LinearLayout parent = (LinearLayout) v.getParent();
                    EditText dPortView = (EditText) parent.findViewById(R.id.port_row_port);
                    dPortView.setOnFocusChangeListener(null);
                    remove(getItem(fPosition));
                }
            }
        });

        return view;
    }
}
