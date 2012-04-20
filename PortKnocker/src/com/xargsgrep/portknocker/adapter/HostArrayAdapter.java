package com.xargsgrep.portknocker.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.listener.PositionOnClickListener;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;

public class HostArrayAdapter extends ArrayAdapter<Host> {
	
    HostDataManager hostDataManager;
	Context context;
	List<Host> hosts;

	public HostArrayAdapter(Context context, List<Host> hosts) {
		super(context, -1, hosts);
        hostDataManager = new HostDataManager(context);
		this.context = context;
		this.hosts = hosts;
	}
	
	@Override
	public int getCount() {
		return hosts.size();
	}
	
	@Override
	public Host getItem(int position) {
		return hosts.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.host_row, null);
		
		TextView labelView = (TextView) view.findViewById(R.id.host_row_label);
		TextView hostnameView = (TextView) view.findViewById(R.id.host_row_hostname);
		TextView portsView = (TextView) view.findViewById(R.id.host_row_ports);
		ImageButton deleteButton = (ImageButton) view.findViewById(R.id.host_row_delete);
		ImageButton editButton = (ImageButton) view.findViewById(R.id.host_row_edit);
		
		Host host = hosts.get(position);
		
		labelView.setText(host.getLabel());
		hostnameView.setText(host.getHostname());
		
		String ports = "";
		for (Port port : host.getPorts()) {
			ports += port.getPort()+":"+port.getProtocol().toString()+", ";
		}
		if (ports.length() > 0) ports = ports.substring(0, ports.length()-2);
		portsView.setText(ports);
		
		view.setOnClickListener(new PositionOnClickListener(position) {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Click ListItem Number " + this.position, Toast.LENGTH_SHORT).show();
			}
		});
		
		deleteButton.setOnClickListener(new PositionOnClickListener(position) {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Delete ListItem Number " + this.position, Toast.LENGTH_SHORT).show();
				Host host = hosts.get(position);
				hostDataManager.deleteHost(host);
				hosts.remove(position);
				notifyDataSetChanged();
			}
		});
		
		editButton.setOnClickListener(new PositionOnClickListener(position) {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Edit ListItem Number " + this.position, Toast.LENGTH_SHORT).show();
			}
		});
		
		return view;
	}

}
