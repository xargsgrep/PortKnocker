package com.xargsgrep.portknocker.widget;

import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;

public class ConfigureWidgetHostArrayAdapter extends ArrayAdapter<Host> {
	
    HostDataManager hostDataManager;
	Context context;
	List<Host> hosts;
	int appWidgetId;

	public ConfigureWidgetHostArrayAdapter(Context context, List<Host> hosts, int appWidgetId) {
		super(context, -1, hosts);
        hostDataManager = new HostDataManager(context);
		this.context = context;
		this.hosts = hosts;
		this.appWidgetId = appWidgetId;
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
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean(context.getString(R.string.pref_key_hide_ports), false)) {
			portsView.setVisibility(View.GONE);
		}
		
		final int fPosition = position;
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long hostId = getItem(fPosition).getId();
	            ConfigureWidgetActivity.saveHostIdPreference(context, appWidgetId, hostId);
	            // workaround for phantom widgets
	            ConfigureWidgetActivity.saveConfiguredPreference(context, appWidgetId);

	            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	            HostWidget.updateAppWidget(context, appWidgetManager, appWidgetId, hostId);

	            Intent resultValue = new Intent();
	            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	            ((Activity) context).setResult(Activity.RESULT_OK, resultValue);
	            ((Activity) context).finish();
			}
		});
		
		((ImageButton) view.findViewById(R.id.host_row_delete)).setVisibility(View.GONE);
		((ImageButton) view.findViewById(R.id.host_row_edit)).setVisibility(View.GONE);
		((ImageView) view.findViewById(R.id.host_row_divider1)).setVisibility(View.GONE);
		((ImageView) view.findViewById(R.id.host_row_divider2)).setVisibility(View.GONE);
		
		return view;
	}
	
}
