package com.xargsgrep.portknocker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
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
	
    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, long hostId) {
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
    	Host host = hostDataManager.getHost(hostId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        
		Intent intent = new Intent(context, HostListActivity.class);
		intent.putExtra("hostId", hostId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        
        views.setTextViewText(R.id.widget_host_label, host.getLabel());
        views.setTextViewText(R.id.widget_host_hostname, host.getHostname());
        views.setTextViewText(R.id.widget_host_ports, host.getPortsString());

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  
}
