package com.xargsgrep.portknocker.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.model.Port.Protocol;

public class HostDataManager {

	private DatabaseManager databaseManager;

	public HostDataManager(Context context) {
		databaseManager = new DatabaseManager(context);
	}
	
	public List<Host> getAllHosts() {
		List<Host> hosts = new ArrayList<Host>();
		
		SQLiteDatabase database = getReadableDatabase();
		
		Cursor hostsCursor = database.query(
			DatabaseManager.HOST_TABLE_NAME,
			DatabaseManager.HOST_TABLE_COLUMNS,
			null,
			null,
			null,
			null,
			DatabaseManager.HOST_ID_COLUMN
		);
		hostsCursor.moveToFirst();
		
		while (!hostsCursor.isAfterLast()) {
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
	
	public Host getHost(long hostId) {
		SQLiteDatabase database = getReadableDatabase();
		
		String hostSelection = String.format("%s = ?", DatabaseManager.HOST_ID_COLUMN);
		Cursor hostCursor = database.query(
			DatabaseManager.HOST_TABLE_NAME,
			DatabaseManager.HOST_TABLE_COLUMNS,
			hostSelection,
			new String[] { new Long(hostId).toString() },
			null,
			null,
			DatabaseManager.HOST_ID_COLUMN
		);
		hostCursor.moveToFirst();
		
		Host host = cursorToHost(hostCursor);
		
		List<Port> ports = getPortsForHost(database, hostId);
		host.setPorts(ports);
		
		hostCursor.close();
		database.close();
		
		return host;
	}
	
	public boolean saveHost(Host host) {
		SQLiteDatabase database = getWriteableDatabase();
		
		database.beginTransaction();
		try {
			ContentValues hostValues = new ContentValues();
			hostValues.put(DatabaseManager.HOST_LABEL_COLUMN, host.getLabel());
			hostValues.put(DatabaseManager.HOST_HOSTNAME_COLUMN, host.getHostname());
			hostValues.put(DatabaseManager.HOST_DELAY_COLUMN, host.getDelay());
			hostValues.put(DatabaseManager.HOST_LAUNCH_APP_COLUMN, host.getLaunchApp());
			
			long hostId = database.insert(DatabaseManager.HOST_TABLE_NAME, null, hostValues);
			if (hostId == -1) return false;
			
			int i = 0;
			for (Port port : host.getPorts()) {
				ContentValues portValues = new ContentValues();
				portValues.put(DatabaseManager.PORT_HOST_ID_COLUMN, hostId);
				portValues.put(DatabaseManager.PORT_INDEX_COLUMN, i);
				portValues.put(DatabaseManager.PORT_PORT_COLUMN, port.getPort());
				portValues.put(DatabaseManager.PORT_PROTOCOL_COLUMN, port.getProtocol().ordinal());
				
				long portId = database.insert(DatabaseManager.PORT_TABLE_NAME, null, portValues);
				if (portId == -1) return false;
				
				i++;
			}
			
			database.setTransactionSuccessful();
			return true;
		} finally {
			database.endTransaction();
			database.close();
		}
	}
	
	public boolean updateHost(Host host) {
		SQLiteDatabase database = getWriteableDatabase();
		
		database.beginTransaction();
		try {
			ContentValues hostValues = new ContentValues();
			hostValues.put(DatabaseManager.HOST_LABEL_COLUMN, host.getLabel());
			hostValues.put(DatabaseManager.HOST_HOSTNAME_COLUMN, host.getHostname());
			hostValues.put(DatabaseManager.HOST_DELAY_COLUMN, host.getDelay());
			hostValues.put(DatabaseManager.HOST_LAUNCH_APP_COLUMN, host.getLaunchApp());
			
			String hostSelection = String.format("%s = ?", DatabaseManager.HOST_ID_COLUMN);
			int rowsAffected = database.update(DatabaseManager.HOST_TABLE_NAME, hostValues, hostSelection, new String[] { new Long(host.getId()).toString() });
			if (rowsAffected == 0) return false;
			
			int i = 0;
			for (Port port : host.getPorts()) {
				ContentValues portValues = new ContentValues();
				portValues.put(DatabaseManager.PORT_INDEX_COLUMN, i);
				portValues.put(DatabaseManager.PORT_PORT_COLUMN, port.getPort());
				portValues.put(DatabaseManager.PORT_PROTOCOL_COLUMN, port.getProtocol().ordinal());
				
				String portSelection = String.format("%s = ?", DatabaseManager.PORT_ID_COLUMN);
				rowsAffected = database.update(DatabaseManager.PORT_TABLE_NAME, portValues, portSelection, new String[] { new Long(port.getId()).toString() });
				if (rowsAffected == 0) return false;
				
				i++;
			}
			
			database.setTransactionSuccessful();
			return true;
		} finally {
			database.endTransaction();
			database.close();
		}
	}
	
	public void deleteHost(Host host) {
		SQLiteDatabase database = getWriteableDatabase();
		
		database.beginTransaction();
		try {
			String portsSelection = String.format("%s = ?", DatabaseManager.PORT_HOST_ID_COLUMN);
			database.delete(DatabaseManager.PORT_TABLE_NAME, portsSelection, new String[] { new Long(host.getId()).toString() });
			
			String hostSelection = String.format("%s = ?", DatabaseManager.HOST_ID_COLUMN);
			database.delete(DatabaseManager.HOST_TABLE_NAME, hostSelection, new String[] { new Long(host.getId()).toString() });
			
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}
	
	private List<Port> getPortsForHost(SQLiteDatabase database, long hostId) {
		List<Port> ports = new ArrayList<Port>();
				
		String portsSelection = String.format("%s = ?", DatabaseManager.PORT_HOST_ID_COLUMN);
		Cursor portsCursor = database.query(
			DatabaseManager.PORT_TABLE_NAME,
			DatabaseManager.PORT_TABLE_COLUMNS,
			portsSelection,
			new String[] { new Long(hostId).toString() },
			null,
			null,
			DatabaseManager.PORT_INDEX_COLUMN
		);
		portsCursor.moveToFirst();
		
		while (!portsCursor.isAfterLast()) {
			Port port = cursorToPort(portsCursor);
			ports.add(port);
			portsCursor.moveToNext();
		}
		
		portsCursor.close();
		
		return ports;
	}
	
	private Host cursorToHost(Cursor cursor) {
		Host host = new Host();
		host.setId(cursor.getLong(0));
		host.setLabel(cursor.getString(1));
		host.setHostname(cursor.getString(2));
		host.setDelay(cursor.getInt(3));
		host.setLaunchApp(cursor.getString(4));
		return host;
	}
	
	private Port cursorToPort(Cursor cursor) {
		Port port = new Port();
		port.setId(cursor.getLong(0));
		port.setHostId(cursor.getLong(1));
		port.setIndex(cursor.getInt(2));
		port.setPort(cursor.getInt(3));
		port.setProtocol(Protocol.values()[cursor.getInt(4)]);
		return port;
	}
	
	private SQLiteDatabase getReadableDatabase() {
		return databaseManager.getReadableDatabase();
	}
	
	private SQLiteDatabase getWriteableDatabase() {
		return databaseManager.getWritableDatabase();
	}
	
}
