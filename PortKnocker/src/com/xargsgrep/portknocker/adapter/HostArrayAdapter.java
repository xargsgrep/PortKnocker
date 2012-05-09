package com.xargsgrep.portknocker.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
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
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.widget.HostWidget;

public class HostArrayAdapter extends ArrayAdapter<Host> {
	
    DatabaseManager databaseManager;
	FragmentActivity activity;
	List<Host> hosts;

	public HostArrayAdapter(FragmentActivity activity, List<Host> hosts) {
		super(activity, -1, hosts);
        databaseManager = new DatabaseManager(activity);
		this.activity = activity;
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
		
		Host host = hosts.get(position);
		
		labelView.setText(host.getLabel());
		hostnameView.setText(host.getHostname());
		portsView.setText(host.getPortsString());
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		if (sharedPreferences.getBoolean(activity.getString(R.string.pref_key_hide_ports), false)) {
			portsView.setVisibility(View.GONE);
		}
		
		final int fPosition = position;
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Host host = getItem(fPosition);
				KnockerAsyncTask knockerAsyncTask = new KnockerAsyncTask(activity, host.getPorts().size());
				knockerAsyncTask.execute(host);
			}
		});
		
		ImageButton deleteButton = (ImageButton) view.findViewById(R.id.host_row_delete);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { showDeleteDialog(fPosition); }
		});
		
		ImageButton editButton = (ImageButton) view.findViewById(R.id.host_row_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Host host = hosts.get(fPosition);
				Intent editHostIntent = new Intent(activity, EditHostActivity.class);
				editHostIntent.putExtra(EditHostActivity.KEY_HOST_ID, host.getId());
		        activity.startActivity(editHostIntent);
			}
		});
		
		return view;
	}
	
	private void showDeleteDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(R.string.confirm_dialog_delete_host_title);
        dialogBuilder.setIcon(R.drawable.ic_dialog_confirm);
        
        dialogBuilder.setPositiveButton(R.string.confirm_dialog_confirm,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
					Host host = hosts.get(position);
					databaseManager.deleteHost(host);
                	remove(host);
			    	HostWidget.updateAllAppWidgetsForHost(activity, host.getId());
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
