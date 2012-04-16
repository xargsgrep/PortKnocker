package com.xargsgrep.portknocker.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseManager extends SQLiteOpenHelper {
	
    private static final int DATABASE_VERSION = 1;
    
    private static final String DATABASE_FILENAME = "hosts.db";
    
    public static final String HOST_TABLE_NAME = "host";
    public static final String HOST_ID_COLUMN = BaseColumns._ID;
    public static final String HOST_LABEL_COLUMN = "label";
    public static final String HOST_HOSTNAME_COLUMN = "hostname";
    public static final String HOST_DELAY_COLUMN = "delay";
    public static final String HOST_LAUNCH_APP_COLUMN = "launch_app";
    
    public static final String PORT_TABLE_NAME = "port";
    public static final String PORT_ID_COLUMN = BaseColumns._ID;
    public static final String PORT_HOST_ID_COLUMN = "host_id";
    public static final String PORT_INDEX_COLUMN = "idx";
    public static final String PORT_PORT_COLUMN = "port";
    public static final String PORT_PROTOCOL_COLUMN = "protocol";
    
    public static final String[] HOST_TABLE_COLUMNS = new String[] { HOST_ID_COLUMN, HOST_LABEL_COLUMN, HOST_HOSTNAME_COLUMN, HOST_DELAY_COLUMN, HOST_LAUNCH_APP_COLUMN }; 
    public static final String[] PORT_TABLE_COLUMNS = new String[] { PORT_ID_COLUMN, PORT_HOST_ID_COLUMN, PORT_INDEX_COLUMN, PORT_PORT_COLUMN, PORT_PROTOCOL_COLUMN }; 

	public DatabaseManager(Context context) {
		super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createHostTableSQL =
				"create table %s (" +
				"	%s integer primary key autoincrement," +
				"	%s string not null," +
				"	%s string not null," +
				"	%s integer not null default 0," +
				"	%s text" +
				");";
		createHostTableSQL = String.format(createHostTableSQL, HOST_TABLE_NAME, HOST_ID_COLUMN, HOST_LABEL_COLUMN, HOST_HOSTNAME_COLUMN, HOST_DELAY_COLUMN, HOST_LAUNCH_APP_COLUMN);
		
		String createPortTableSQL =
				"create table %s (" +
				"	%s integer not null," +
				"	%s integer not null," +
				"	%s integer not null," +
				"	%s integer not null," +
				"	%s integer not null" +
				");";
		createPortTableSQL = String.format(createPortTableSQL, PORT_TABLE_NAME, PORT_ID_COLUMN, PORT_HOST_ID_COLUMN, PORT_INDEX_COLUMN, PORT_PORT_COLUMN, PORT_PROTOCOL_COLUMN);
		
		db.execSQL(createHostTableSQL);
		db.execSQL(createPortTableSQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
}
