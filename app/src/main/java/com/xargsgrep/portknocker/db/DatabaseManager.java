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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.model.Port.Protocol;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager
{
    private DatabaseHelper databaseHelper;

    public DatabaseManager(Context context)
    {
        databaseHelper = new DatabaseHelper(context);
    }

    public List<Host> getAllHosts()
    {
        List<Host> hosts = new ArrayList<>();

        SQLiteDatabase database = getReadableDatabase();

        Cursor hostsCursor = database.query(
                DatabaseHelper.HOST_TABLE_NAME,
                DatabaseHelper.HOST_TABLE_COLUMNS,
                null,
                null,
                null,
                null,
                DatabaseHelper.HOST_ID_COLUMN
        );
        hostsCursor.moveToFirst();

        while (!hostsCursor.isAfterLast())
        {
            Host host = cursorToHost(hostsCursor);
            List<Port> ports = getPortsForHost(database, host.getId());
            host.setPorts(ports);
            hosts.add(host);
            hostsCursor.moveToNext();
        }

        hostsCursor.close();
        database.close();

        return hosts;
    }

    public Host getHost(long hostId)
    {
        SQLiteDatabase database = getReadableDatabase();

        String hostSelection = String.format("%s = ?", DatabaseHelper.HOST_ID_COLUMN);
        Cursor hostCursor = database.query(
                DatabaseHelper.HOST_TABLE_NAME,
                DatabaseHelper.HOST_TABLE_COLUMNS,
                hostSelection,
                new String[] {new Long(hostId).toString()},
                null,
                null,
                DatabaseHelper.HOST_ID_COLUMN
        );
        hostCursor.moveToFirst();

        Host host = cursorToHost(hostCursor);

        List<Port> ports = getPortsForHost(database, hostId);
        host.setPorts(ports);

        hostCursor.close();
        database.close();

        return host;
    }

    public boolean saveHost(Host host)
    {
        SQLiteDatabase database = getWriteableDatabase();

        database.beginTransaction();
        try
        {
            ContentValues hostValues = new ContentValues();
            hostValues.put(DatabaseHelper.HOST_LABEL_COLUMN, host.getLabel());
            hostValues.put(DatabaseHelper.HOST_HOSTNAME_COLUMN, host.getHostname());
            hostValues.put(DatabaseHelper.HOST_DELAY_COLUMN, host.getDelay());
            hostValues.put(DatabaseHelper.HOST_LAUNCH_INTENT_PACKAGE_COLUMN, host.getLaunchIntentPackage());
            hostValues.put(DatabaseHelper.HOST_TCP_CONNECT_TIMEOUT_COLUMN, host.getTcpConnectTimeout());

            long hostId = database.insert(DatabaseHelper.HOST_TABLE_NAME, null, hostValues);
            if (hostId == -1) return false;

            int i = 0;
            for (Port port : host.getPorts())
            {
                ContentValues portValues = new ContentValues();
                portValues.put(DatabaseHelper.PORT_HOST_ID_COLUMN, hostId);
                portValues.put(DatabaseHelper.PORT_INDEX_COLUMN, i);
                portValues.put(DatabaseHelper.PORT_PORT_COLUMN, port.getPort());
                portValues.put(DatabaseHelper.PORT_PROTOCOL_COLUMN, port.getProtocol().ordinal());

                long portId = database.insert(DatabaseHelper.PORT_TABLE_NAME, null, portValues);
                if (portId == -1) return false;

                i++;
            }

            database.setTransactionSuccessful();
            return true;
        }
        finally
        {
            database.endTransaction();
            database.close();
        }
    }

    public boolean updateHost(Host host)
    {
        SQLiteDatabase database = getWriteableDatabase();

        database.beginTransaction();
        try
        {
            ContentValues hostValues = new ContentValues();
            hostValues.put(DatabaseHelper.HOST_LABEL_COLUMN, host.getLabel());
            hostValues.put(DatabaseHelper.HOST_HOSTNAME_COLUMN, host.getHostname());
            hostValues.put(DatabaseHelper.HOST_DELAY_COLUMN, host.getDelay());
            hostValues.put(DatabaseHelper.HOST_LAUNCH_INTENT_PACKAGE_COLUMN, host.getLaunchIntentPackage());
            hostValues.put(DatabaseHelper.HOST_TCP_CONNECT_TIMEOUT_COLUMN, host.getTcpConnectTimeout());

            String hostSelection = String.format("%s = ?", DatabaseHelper.HOST_ID_COLUMN);
            int rowsAffected = database.update(DatabaseHelper.HOST_TABLE_NAME, hostValues, hostSelection, new String[] {new Long(host.getId()).toString()});
            if (rowsAffected == 0) return false;

            String portsSelection = String.format("%s = ?", DatabaseHelper.PORT_HOST_ID_COLUMN);
            database.delete(DatabaseHelper.PORT_TABLE_NAME, portsSelection, new String[] {new Long(host.getId()).toString()});

            int i = 0;
            for (Port port : host.getPorts())
            {
                ContentValues portValues = new ContentValues();
                portValues.put(DatabaseHelper.PORT_HOST_ID_COLUMN, host.getId());
                portValues.put(DatabaseHelper.PORT_INDEX_COLUMN, i);
                portValues.put(DatabaseHelper.PORT_PORT_COLUMN, port.getPort());
                portValues.put(DatabaseHelper.PORT_PROTOCOL_COLUMN, port.getProtocol().ordinal());

                long portId = database.insert(DatabaseHelper.PORT_TABLE_NAME, null, portValues);
                if (portId == -1) return false;

                i++;
            }

            database.setTransactionSuccessful();
            return true;
        }
        finally
        {
            database.endTransaction();
            database.close();
        }
    }

    public void deleteHost(Host host)
    {
        SQLiteDatabase database = getWriteableDatabase();

        database.beginTransaction();
        try
        {
            String portsSelection = String.format("%s = ?", DatabaseHelper.PORT_HOST_ID_COLUMN);
            database.delete(DatabaseHelper.PORT_TABLE_NAME, portsSelection, new String[] {new Long(host.getId()).toString()});

            String hostSelection = String.format("%s = ?", DatabaseHelper.HOST_ID_COLUMN);
            database.delete(DatabaseHelper.HOST_TABLE_NAME, hostSelection, new String[] {new Long(host.getId()).toString()});

            database.setTransactionSuccessful();
        }
        finally
        {
            database.endTransaction();
            database.close();
        }
    }

    public boolean hostExists(long hostId)
    {
        SQLiteDatabase database = getReadableDatabase();

        String hostSelection = String.format("%s = ?", DatabaseHelper.HOST_ID_COLUMN);
        Cursor hostCursor = database.query(
                DatabaseHelper.HOST_TABLE_NAME,
                DatabaseHelper.HOST_TABLE_COLUMNS,
                hostSelection,
                new String[] {new Long(hostId).toString()},
                null,
                null,
                DatabaseHelper.HOST_ID_COLUMN
        );
        int rowCount = hostCursor.getCount();

        hostCursor.close();
        database.close();

        return rowCount > 0;
    }

    private List<Port> getPortsForHost(SQLiteDatabase database, long hostId)
    {
        List<Port> ports = new ArrayList<>();

        String portsSelection = String.format("%s = ?", DatabaseHelper.PORT_HOST_ID_COLUMN);
        Cursor portsCursor = database.query(
                DatabaseHelper.PORT_TABLE_NAME,
                DatabaseHelper.PORT_TABLE_COLUMNS,
                portsSelection,
                new String[] {new Long(hostId).toString()},
                null,
                null,
                DatabaseHelper.PORT_INDEX_COLUMN
        );
        portsCursor.moveToFirst();

        while (!portsCursor.isAfterLast())
        {
            Port port = cursorToPort(portsCursor);
            ports.add(port);
            portsCursor.moveToNext();
        }

        portsCursor.close();

        return ports;
    }

    private Host cursorToHost(Cursor cursor)
    {
        Host host = new Host();
        host.setId(cursor.getLong(0));
        host.setLabel(cursor.getString(1));
        host.setHostname(cursor.getString(2));
        host.setDelay(cursor.getInt(3));
        host.setLaunchIntentPackage(cursor.getString(4));
        host.setTcpConnectTimeout(cursor.getInt(7));
        return host;
    }

    private Port cursorToPort(Cursor cursor)
    {
        Port port = new Port();
        port.setHostId(cursor.getLong(0));
        port.setIndex(cursor.getInt(1));
        port.setPort(cursor.getInt(2));
        port.setProtocol(Protocol.values()[cursor.getInt(3)]);
        return port;
    }

    private SQLiteDatabase getReadableDatabase()
    {
        return databaseHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWriteableDatabase()
    {
        return databaseHelper.getWritableDatabase();
    }
}
