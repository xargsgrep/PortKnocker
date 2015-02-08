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
package com.xargsgrep.portknocker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.HostListActivity;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;

public class HostWidget extends AppWidgetProvider
{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for (int appWidgetId : appWidgetIds)
        {
            long hostId = ConfigureWidgetActivity.getHostIdPreference(context, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, hostId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        for (int i = 0; i < appWidgetIds.length; i++)
        {
            ConfigureWidgetActivity.deleteHostIdPreference(context, appWidgetIds[i]);
        }
    }

    public static void updateAllAppWidgets(Context context)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, HostWidget.class));
        for (int appWidgetId : appWidgetIds)
        {
            updateAppWidget(context, appWidgetManager, appWidgetId, null);
        }
    }

    public static void updateAllAppWidgetsForHost(Context context, long hostId)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, HostWidget.class));
        for (int appWidgetId : appWidgetIds)
        {
            updateAppWidget(context, appWidgetManager, appWidgetId, hostId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Long hostId)
    {
        // workaround for phantom widgets
        boolean configured = ConfigureWidgetActivity.getConfiguredPreference(context, appWidgetId);
        if (!configured) return;

        Long widgetHostId = ConfigureWidgetActivity.getHostIdPreference(context, appWidgetId);
        if (hostId != null && !hostId.equals(widgetHostId)) return;

        DatabaseManager hostDataManager = new DatabaseManager(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        boolean hostExists = hostDataManager.hostExists(widgetHostId);
        if (hostExists)
        {
            Host host = hostDataManager.getHost(widgetHostId);

            Intent intent = new Intent(context, HostListActivity.class);
            intent.putExtra("hostId", widgetHostId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            views.setTextViewText(R.id.widget_host_label, host.getLabel());
            views.setTextViewText(R.id.widget_host_hostname, host.getHostname());
            views.setTextViewText(R.id.widget_host_ports, host.getPortsString());

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences.getBoolean(context.getString(R.string.pref_key_hide_ports_widget), false))
            {
                views.setViewVisibility(R.id.widget_host_ports, View.GONE);
            }
            else
            {
                views.setViewVisibility(R.id.widget_host_ports, View.VISIBLE);
            }
        }
        else
        {
            views.setTextViewText(R.id.widget_host_label, "Invalid Host");
            views.setViewVisibility(R.id.widget_host_hostname, View.GONE);
            views.setViewVisibility(R.id.widget_host_ports, View.GONE);

            Intent intent = new Intent(context, HostListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
