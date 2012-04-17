package com.xargsgrep.portknocker.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.model.Host;

public class HostArrayAdapter extends ArrayAdapter<Host> {
	
	Context context;
	List<Host> hosts;

	public HostArrayAdapter(Context context, int textViewResourceId, List<Host> hosts) {
		super(context, textViewResourceId, hosts);
		this.context = context;
		this.hosts = hosts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.host_row, null);
		}
		
		TextView labelView = (TextView) view.findViewById(R.id.host_row_label);
		TextView hostnameView = (TextView) view.findViewById(R.id.host_row_hostname);
		TextView portsView = (TextView) view.findViewById(R.id.host_row_ports);
		
		Host host = hosts.get(position);
		
		labelView.setText(host.getLabel());
		hostnameView.setText(host.getHostname());
		portsView.setText("2565:TCP, 2345:UDP");
		
		/*
		TextView tt = (TextView) view.findViewById(R.id.toptext);
		TextView bt = (TextView) view.findViewById(R.id.bottomtext);
		ImageView imageView = (ImageView) view.findViewById(R.id.icon);

		if (tt != null) {
			tt.setText(contact.getName());
		}
		if (bt != null) {
			bt.setText(contact.getDate());
		}
		if (imageView != null) {
			if ("WEB".equals(contact.getLeadType()))
				imageView.setImageResource(R.drawable.email_icon);
			else if ("CALL".equals(contact.getLeadType()))
				imageView.setImageResource(R.drawable.phone_icon);
		}
		*/
		
		return view;
	}

}
