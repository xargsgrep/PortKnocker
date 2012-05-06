package com.xargsgrep.portknocker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.HostListActivity;
import com.xargsgrep.portknocker.manager.HostDataManager;
import com.xargsgrep.portknocker.model.Host;

public class HostWidget extends AppWidgetProvider {
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
            long hostId = ConfigureWidgetActivity.getHostIdPreference(context, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, hostId);
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		for (int i=0; i<appWidgetIds.length; i++) {
			ConfigureWidgetActivity.deleteHostIdPreference(context, appWidgetIds[i]);
		}
	}
	
	public static void updateAllAppWidgets(Context context, long hostId) {
    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    	int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, HostWidget.class));
    	for (int appWidgetId : appWidgetIds) {
    		updateAppWidget(context, appWidgetManager, appWidgetId, hostId);
    	}
	}
	
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, long hostId) {
		// workaround for phantom widgets
        boolean configured = ConfigureWidgetActivity.getConfiguredPreference(context, appWidgetId);
        if (!configured) return;
        
        long widgetHostId = ConfigureWidgetActivity.getHostIdPreference(context, appWidgetId);
        if (widgetHostId != hostId) return;
    	
    	HostDataManager hostDataManager = new HostDataManager(context);
    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
    	
    	boolean hostExists = hostDataManager.hostExists(hostId);
    	if (hostExists) {
	    	Host host = hostDataManager.getHost(hostId);
        
			Intent intent = new Intent(context, HostListActivity.class);
			intent.putExtra("hostId", hostId);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
			views.setOnClickPendingIntent(R.id.widget, pendingIntent);
		
	        views.setTextViewText(R.id.widget_host_label, host.getLabel());
	        views.setTextViewText(R.id.widget_host_hostname, host.getHostname());
	        views.setTextViewText(R.id.widget_host_ports, host.getPortsString());
    	}
    	else {
    		views.setTextViewText(R.id.widget_host_label, "Invalid Host");
    		views.setViewVisibility(R.id.widget_host_hostname, View.GONE);
    		views.setViewVisibility(R.id.widget_host_ports, View.GONE);
    	}

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  
}
