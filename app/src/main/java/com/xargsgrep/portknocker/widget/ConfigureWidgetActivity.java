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

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.model.Host;

import java.util.List;

public class ConfigureWidgetActivity extends ListActivity
{
    private static final String PREFS_NAME = "com.xargsgrep.portknocker.widget";
    private static final String PREF_HOST_ID_KEY = "hostid_";
    private static final String PREF_CONFIGURED_KEY = "configured_";

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.list_view);

        Bundle extras = getIntent().getExtras();
        int appWidgetId = (extras == null)
                ? AppWidgetManager.INVALID_APPWIDGET_ID
                : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish();

        databaseManager = new DatabaseManager(this);
        List<Host> hosts = databaseManager.getAllHosts();
        ConfigureWidgetHostArrayAdapter hostAdapter = new ConfigureWidgetHostArrayAdapter(this, hosts, appWidgetId);
        setListAdapter(hostAdapter);
    }

    public static void saveHostIdPreference(Context context, int appWidgetId, long hostId)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(getAppWidgetHostIdKey(appWidgetId), hostId);
        prefs.commit();
    }

    public static long getHostIdPreference(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(getAppWidgetHostIdKey(appWidgetId), -1);
    }

    public static void deleteHostIdPreference(Context context, int appWidgetId)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(getAppWidgetHostIdKey(appWidgetId));
        prefs.commit();
    }

    public static void saveConfiguredPreference(Context context, int appWidgetId)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(getAppWidgetConfiguredKey(appWidgetId), true);
        prefs.commit();
    }

    public static boolean getConfiguredPreference(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(getAppWidgetConfiguredKey(appWidgetId), false);
    }

    private static String getAppWidgetHostIdKey(int appWidgetId)
    {
        return PREF_HOST_ID_KEY + appWidgetId;
    }

    private static String getAppWidgetConfiguredKey(int appWidgetId)
    {
        return PREF_CONFIGURED_KEY + appWidgetId;
    }
}
