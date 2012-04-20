package com.xargsgrep.portknocker.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.model.Port;

public class PortArrayAdapter extends ArrayAdapter<Port> {
	
	Context context;
	List<Port> ports;

	public PortArrayAdapter(Context context, List<Port> ports) {
		super(context, -1, ports);
		this.context = context;
		this.ports = ports;
	}
	
	@Override
	public int getCount() {
		return ports.size();
	}

	@Override
	public Port getItem(int position) {
		return ports.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.port_row, null);
		
		/*
		TextView labelView = (TextView) view.findViewById(R.id.host_row_label);
		TextView hostnameView = (TextView) view.findViewById(R.id.host_row_hostname);
		TextView portsView = (TextView) view.findViewById(R.id.host_row_ports);
		
		Host host = hosts.get(position);
		
		labelView.setText(host.getLabel());
		hostnameView.setText(host.getHostname());
		portsView.setText("2565:TCP, 2345:UDP");
		
		view.setOnClickListener(new PositionOnClickListener(position) {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Click ListItem Number " + this.position, Toast.LENGTH_SHORT).show();
			}
		});
		*/
		
		return view;
	}

}
