package com.vera5.gpsrec;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class gpsDatabase extends SQLiteOpenHelper {

  private final static int DB_VERSION = 1;
  private final static String DB_NAME = "gpsrec.db";
  private Context context;

	public gpsDatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Index
		db.execSQL("CREATE TABLE toc (t1 INTEGER NOT NULL,t2 INTEGER NOT NULL,tag VARCHAR(40))");
		db.execSQL("CREATE INDEX toc_t2 ON toc(t2)");
		// Frames
		db.execSQL("CREATE TABLE frames (a INTEGER NOT NULL DEFAULT 0,ts INTEGER NOT NULL,lat REAL NOT NULL,lng REAL NOT NULL,p REAL,src VARCHAR(10))");
		db.execSQL("CREATE INDEX frames_ts ON frames(ts)");
		// Test data
		// some snapshots
		addIndex(db,1438523596246L,1438523596246L,"Eindhoven");
		addFrame(db,1438523596246L,51.439425F,5.475918F,20F,"mock",0);
		addIndex(db,1438523973450L,1438523973450L,"Reni place");
		addFrame(db,1438523973450L,51.423071F,5.574119F,20F,"mock",0);
		addIndex(db,1438524197378L,1438524197378L,"Shumen");
		addFrame(db,1438524197378L,43.271641F,26.923873F,20F,"mock",0);
		// a path (Eindhoven-Antwerp-Brussels)
		addIndex(db,1438524474450L,1438524705614L,"Eindhoven-Antwerp-Brussels");
		addFrame(db,1438524474450L,51.439425F,5.475918F,20F,"mock",1);
		addFrame(db,1438524563682L,51.216371F,4.403981F,20F,"mock",1);
		addFrame(db,1438524705614L,50.878812F,4.385538F,20F,"mock",1);
		// more snapshots to test ListView larger than the screen
		long ts = 1438530651346L;
		addIndex(db,ts,ts,"Extra test1");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
		ts = 1438530766129L;
		addIndex(db,ts,ts,"Extra test2");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
		ts = 1438530870229L;
		addIndex(db,ts,ts,"Extra test3");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
		ts = 1438530909416L;
		addIndex(db,ts,ts,"Extra test4");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
		ts = 1438530949898L;
		addIndex(db,ts,ts,"Extra test5");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
		ts = 1438531011749L;
		addIndex(db,ts,ts,"Extra test6");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
		ts = 1438531065447L;
		addIndex(db,ts,ts,"Extra test7");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
		ts = 1438531092489L;
		addIndex(db,ts,ts,"Extra test8");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
		ts = 1438531158030L;
		addIndex(db,ts,ts,"Extra test9");
		addFrame(db,ts,51.439425F,5.475918F,20F,"mock",0);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public Cursor query(String sql) {
		return getWritableDatabase().rawQuery(sql,null);
	}

	public void addIndex(SQLiteDatabase db,long t1,long t2,String tag) {
		db.execSQL("INSERT INTO toc (t1,t2,tag) VALUES ("+t1+","+t2+",'"+tag+"')");
	}
	public void addFrame(SQLiteDatabase db,long ts,float lat,float lng,float p,String src,int a) {
		db.execSQL("INSERT INTO frames (a,ts,lat,lng,p,src) VALUES ("+a+","+ts+","+lat+","+lng+","+p+",'"+src+"')");
	}

	// Overloaded
	public void add(SQLiteDatabase db,float frequency,String tag) {
		db.execSQL("INSERT INTO frequency (freq,tag) VALUES ("+frequency+",'"+escape(tag)+"')");
	}

	public void del(long t1,long t2) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM toc WHERE t1="+t1+" AND t2="+t2);
		db.execSQL("DELETE FROM frames WHERE ts BETWEEN "+t1+" AND "+t2);
	}

	private String escape(String s) {
		return s.replace("'","''");
	}

}
