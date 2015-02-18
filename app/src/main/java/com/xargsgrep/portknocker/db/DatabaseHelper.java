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
package com.xargsgrep.portknocker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.xargsgrep.portknocker.model.Host;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_FILENAME = "hosts.db";

    public static final String HOST_TABLE_NAME = "host";
    public static final String HOST_ID_COLUMN = BaseColumns._ID;
    public static final String HOST_LABEL_COLUMN = "label";
    public static final String HOST_HOSTNAME_COLUMN = "hostname";
    public static final String HOST_DELAY_COLUMN = "delay";
    public static final String HOST_TCP_CONNECT_TIMEOUT_COLUMN = "tcp_connect_timeout";
    public static final String HOST_LAUNCH_INTENT_PACKAGE_COLUMN = "launch_intent_package";
    public static final String HOST_USERNAME_COLUMN = "username";
    public static final String HOST_TARGET_PORT_COLUMN = "target_port";

    public static final String PORT_TABLE_NAME = "port";
    public static final String PORT_HOST_ID_COLUMN = "host_id";
    public static final String PORT_INDEX_COLUMN = "idx";
    public static final String PORT_PORT_COLUMN = "port";
    public static final String PORT_PROTOCOL_COLUMN = "protocol";

    public static final String[] HOST_TABLE_COLUMNS = new String[]
    {
        HOST_ID_COLUMN,
        HOST_LABEL_COLUMN,
        HOST_HOSTNAME_COLUMN,
        HOST_DELAY_COLUMN,
        HOST_LAUNCH_INTENT_PACKAGE_COLUMN,
        HOST_USERNAME_COLUMN,
        HOST_TARGET_PORT_COLUMN,
        HOST_TCP_CONNECT_TIMEOUT_COLUMN
    };
    public static final String[] PORT_TABLE_COLUMNS = new String[]
    {
        PORT_HOST_ID_COLUMN,
        PORT_INDEX_COLUMN,
        PORT_PORT_COLUMN,
        PORT_PROTOCOL_COLUMN
    };

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createHostTableSQL =
                "create table %s (" +
                        "   %s integer primary key autoincrement," +
                        "   %s string not null," +
                        "   %s string not null," +
                        "   %s integer not null default %d," +
                        "   %s string," +
                        "   %s string," +
                        "   %s integer," +
                        "   %s integer not null default %d" +
                        ");";
        createHostTableSQL = String.format(
                createHostTableSQL,
                HOST_TABLE_NAME,
                HOST_ID_COLUMN,
                HOST_LABEL_COLUMN,
                HOST_HOSTNAME_COLUMN,
                HOST_DELAY_COLUMN, Host.DEFAULT_DELAY,
                HOST_LAUNCH_INTENT_PACKAGE_COLUMN,
                HOST_USERNAME_COLUMN,
                HOST_TARGET_PORT_COLUMN,
                HOST_TCP_CONNECT_TIMEOUT_COLUMN, Host.DEFAULT_TCP_CONNECT_TIMEOUT
        );

        String createPortTableSQL =
                "create table %s (" +
                        "   %s integer not null," +
                        "   %s integer not null," +
                        "   %s integer not null," +
                        "   %s integer not null" +
                        ");";
        createPortTableSQL = String.format(
                createPortTableSQL,
                PORT_TABLE_NAME,
                PORT_HOST_ID_COLUMN,
                PORT_INDEX_COLUMN,
                PORT_PORT_COLUMN,
                PORT_PROTOCOL_COLUMN
        );

        db.execSQL(createHostTableSQL);
        db.execSQL(createPortTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to version " + newVersion);
        switch (newVersion)
        {
            case DATABASE_VERSION:
                db.execSQL(String.format(
                        "alter table %s add column %s integer not null default %d;",
                        HOST_TABLE_NAME,
                        HOST_TCP_CONNECT_TIMEOUT_COLUMN,
                        Host.DEFAULT_TCP_CONNECT_TIMEOUT
                ));
                return;
            default:
                throw new IllegalStateException("Invalid newVersion: " + newVersion);
        }
    }
}
/*
Version 1:
create table host(
    _id integer primary key autoincrement,
    label string not null,
    hostname string not null,
    delay integer not null default 0,
    launch_intent_package string,
    username string,
    target_port integer
);

Version 2:
create table host(
    _id integer primary key autoincrement,
    label string not null,
    hostname string not null,
    delay integer not null default 1000,
    launch_intent_package string,
    username string,
    target_port integer,
    tcp_connect_timeout integer not null default 100,
);
*/
