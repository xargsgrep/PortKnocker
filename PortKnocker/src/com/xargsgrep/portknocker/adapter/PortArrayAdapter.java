package com.xargsgrep.portknocker.adapter;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.listener.PositionOnClickListener;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.model.Port.Protocol;

public class PortArrayAdapter extends ArrayAdapter<Port> {
	
	List<Port> ports;

	public PortArrayAdapter(Context context, List<Port> ports) {
		super(context, -1, ports);
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
		
		TextView portView = (TextView) view.findViewById(R.id.port_row_port);
		Spinner protocolSpinner  = (Spinner) view.findViewById(R.id.port_row_protocol);
		ImageButton deleteButton = (ImageButton) view.findViewById(R.id.port_row_delete);
		
		ArrayAdapter<Protocol> protocolAdapter = new ArrayAdapter<Protocol>(getContext(), android.R.layout.simple_spinner_item, Protocol.values());
		protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		protocolSpinner.setAdapter(protocolAdapter);
		
		Port port = ports.get(position);
		portView.setText((port.getPort() > 0) ? new Integer(port.getPort()).toString() : "");
		protocolSpinner.setSelection(port.getProtocol().ordinal());
		
		final ListView listView = (ListView) parent;
		deleteButton.setOnClickListener(new PositionOnClickListener(position) {
			@Override
			public void onClick(View v) {
				if (getCount() == 1) {
					Toast toast = Toast.makeText(getContext(), "Can't delete all ports!", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
					return;
				}
				refreshArrayFromListView(listView);
				ports.remove(position);
				notifyDataSetChanged();
			}
		});
		
		return view;
	}

	public void refreshArrayFromListView(ListView view) {
		for (int i=0; i<view.getChildCount(); i++) {
			View row = view.getChildAt(i);
			
			EditText portEditText = (EditText) row.findViewById(R.id.port_row_port);
			Spinner protocolSpinner = (Spinner) row.findViewById(R.id.port_row_protocol);
			
			String portStr = portEditText.getText().toString();
			ports.get(i).setPort((portStr != null && portStr.length() > 0) ? Integer.parseInt(portStr) : -1);
			Protocol protocol = Protocol.valueOf(protocolSpinner.getSelectedItem().toString());
			ports.get(i).setProtocol(protocol);
		}
	}
}
