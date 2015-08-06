package com.vera5.gpsrec;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
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
		addFrame(db,1438523596246L,51.439425D,5.475918D,20F,"mock",0);
		addIndex(db,1438523973450L,1438523973450L,"Reni place");
		addFrame(db,1438523973450L,51.423071D,5.574119D,20F,"mock",0);
		addIndex(db,1438524197378L,1438524197378L,"Shumen");
		addFrame(db,1438524197378L,43.271641D,26.923873D,20F,"mock",0);
		addIndex(db,1438554197378L,1438554197378L,"Graz");
		addFrame(db,1438554197378L,47.071876D,15.441456D,20F,"mock",0);
		// a path (Eindhoven-Antwerp-Brussels)
		addIndex(db,1438524474450L,1438524705614L,"Eindhoven-Antwerp-Brussels");
		addFrame(db,1438524474450L,51.439425D,5.475918D,20F,"mock",1);
		addFrame(db,1438524563682L,51.216371D,4.403981D,20F,"mock",1);
		addFrame(db,1438524705614L,50.878812D,4.385538D,20F,"mock",1);
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

	public void addFrame(SQLiteDatabase db,long ts,double lat,double lng,float p,String src,int a) {
		db.execSQL("INSERT INTO frames (a,ts,lat,lng,p,src) VALUES ("+a+","+ts+","+lat+","+lng+","+p+",'"+src+"')");
	}

	public void del(long t1,long t2) {
		SQLiteDatabase db = getWritableDatabase();
		int a = t1 == t2 ? 0 : 1;	// Snapshot/Record
		db.execSQL("DELETE FROM toc WHERE t1="+t1+" AND t2="+t2);
		db.execSQL("DELETE FROM frames WHERE a="+a+" AND ts BETWEEN "+t1+" AND "+t2);
	}

	public String getMarkers(long t1, long t2) {
		int a = t1 == t2 ? 0 : 1;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT ts,lat,lng,p,src FROM frames WHERE a="+a
			+ " AND ts BETWEEN "+t1+" AND "+t2;
//Log.d("***", sql);
		Cursor curs = db.rawQuery(sql,null);
		String json = "";
		if (curs.moveToFirst()) {
			do {
				json += "['"+Lib.ts2dts(curs.getLong(0))+"', "+curs.getFloat(1)+", "+curs.getFloat(2)+", 1],";
			} while (curs.moveToNext());
		}
		return json.substring(0,json.length()-1);
	}

	public void setTag (long id, String tag) {
Log.d("***", "db.setTag("+id+","+tag+")");
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("UPDATE toc SET tag='"+escape(tag)+"' WHERE rowid="+id);
	}

	private String escape(String s) {
		return s.replace("'","''");
	}

	protected void snapshot(Location loc) {
		SQLiteDatabase db = getWritableDatabase();
		addFrame(db,loc.getTime(),loc.getLatitude(),loc.getLongitude(),loc.getAccuracy(),loc.getProvider(),0);
		addIndex(db,loc.getTime(),loc.getTime(),Lib.ts2dts(loc.getTime()));
	}

}
