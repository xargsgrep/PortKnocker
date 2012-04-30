package com.xargsgrep.portknocker.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.activity.SettingsActivity;
import com.xargsgrep.portknocker.asynctask.KnockerAsyncTask;
import com.xargsgrep.portknocker.listener.PositionOnClickListener;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;

public class HostArrayAdapter extends ArrayAdapter<Host> {
	
    HostDataManager hostDataManager;
	Context context;
	Fragment fragment;
	List<Host> hosts;

	public HostArrayAdapter(Context context, Fragment fragment, List<Host> hosts) {
		super(context, -1, hosts);
        hostDataManager = new HostDataManager(context);
		this.context = context;
		this.fragment = fragment;
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
		
		StringBuilder ports = new StringBuilder();
		for (Port port : host.getPorts()) {
			ports.append(port.getPort());
			ports.append(":");
			ports.append(port.getProtocol());
			ports.append(", ");
		}
		if (ports.length() > 0) ports.replace(ports.length()-2, ports.length(), "");
		portsView.setText(ports);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean(context.getString(R.string.pref_key_hide_ports), false)) {
			portsView.setVisibility(View.GONE);
		}
		
		view.setOnClickListener(new PositionOnClickListener(position) {
			@Override
			public void onClick(View v) {
				Host host = getItem(position);
				KnockerAsyncTask knockerAsyncTask = new KnockerAsyncTask(fragment, host.getLaunchIntentPackage());
				knockerAsyncTask.execute(host);
			}
		});
		
		deleteButton.setOnClickListener(new PositionOnClickListener(position) {
			@Override
			public void onClick(View v) { showDeleteDialog(position); }
		});
		
		editButton.setOnClickListener(new PositionOnClickListener(position) {
			@Override
			public void onClick(View v) {
				Host host = hosts.get(position);
				Intent editHostIntent = new Intent(context, EditHostActivity.class);
				editHostIntent.putExtra(EditHostActivity.KEY_HOST_ID, host.getId());
		        context.startActivity(editHostIntent);
			}
		});
		
		return view;
	}
	
	private void showDeleteDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(R.string.confirm_dialog_delete_host_title);
        dialogBuilder.setIcon(R.drawable.confirm_dialog_icon);
        
        dialogBuilder.setPositiveButton(R.string.confirm_dialog_confirm,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
					Host host = hosts.get(position);
					hostDataManager.deleteHost(host);
					hosts.remove(position);
					notifyDataSetChanged();
                }
            }
        );
        dialogBuilder.setNegativeButton(R.string.confirm_dialog_cancel,
    		new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) { }
    		}
        );
        
        dialogBuilder.create().show();
	}
	
}
