package com.vera5.gpsrec;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class gpsDatabase extends SQLiteOpenHelper {

  private final static int DB_VERSION = 1;
  private final static String DB_NAME = "gpsrec.db";
  private final static int fSingle = 1;		// Single frame, i.e. a snapshot
  private final static int fSerie = 2;		// Frame part of a serie, i.e. "movie"
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
		db.execSQL("CREATE TABLE frames (a INTEGER NOT NULL DEFAULT "+fSerie+",ts INTEGER NOT NULL,lat REAL NOT NULL,lng REAL NOT NULL,p REAL,src VARCHAR(10))");
		db.execSQL("CREATE INDEX frames_ts ON frames(ts)");
		// Test data
		// some snapshots
		addIndex(db,1438523596246L,1438523596246L,"Eindhoven");
		addFrame(db,1438523596246L,51.439425D,5.475918D,20F,"mock",fSingle);
		addIndex(db,1438523973450L,1438523973450L,"Reni place");
		addFrame(db,1438523973450L,51.423071D,5.574119D,20F,"mock",fSingle);
		addIndex(db,1438524197378L,1438524197378L,"Shumen");
		addFrame(db,1438524197378L,43.271641D,26.923873D,20F,"mock",fSingle);
		addIndex(db,1438554197378L,1438554197378L,"Graz");
		addFrame(db,1438554197378L,47.071876D,15.441456D,20F,"mock",fSingle);
		// a path (Eindhoven-Antwerp-Brussels)
		addIndex(db,1438524474450L,1438524705614L,"Eindhoven-Antwerp-Brussels");
		addFrame(db,1438524474450L,51.439425D,5.475918D,20F,"mock",fSerie);
		addFrame(db,1438524563682L,51.216371D,4.403981D,20F,"mock",fSerie);
		addFrame(db,1438524705614L,50.878812D,4.385538D,20F,"mock",fSerie);
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
	// Overloaded
	public void addIndex(long t1,long t2,String tag) {
		addIndex(getWritableDatabase(),t1,t2,tag);
	}

	public void addFrame(SQLiteDatabase db,long ts,double lat,double lng,float p,String src,int a) {
		db.execSQL("INSERT INTO frames (a,ts,lat,lng,p,src) VALUES ("+a+","+ts+","+lat+","+lng+","+p+",'"+src+"')");
	}
	// Overloaded
	public void addFrame(long ts,double lat,double lng,float p,String src,int a) {
		addFrame(getWritableDatabase(),ts,lat,lng,p,src,a);
	}
	
	public void del(long t1,long t2) {
		SQLiteDatabase db = getWritableDatabase();
		int a = getA(t1,t2);	// Snapshot/Record
		db.execSQL("DELETE FROM toc WHERE t1="+t1+" AND t2="+t2);
		db.execSQL("DELETE FROM frames WHERE a="+a+" AND ts BETWEEN "+t1+" AND "+t2);
	}

	public List<MapAttr> getMarkers(long t1, long t2) {
		SQLiteDatabase db = getReadableDatabase();
		int a = getA(t1,t2);
		String sql = "SELECT ts,lat,lng,p,src FROM frames WHERE a="+a
			+ " AND ts BETWEEN "+t1+" AND "+t2;
		Cursor curs = db.rawQuery(sql,null);
		List<MapAttr> list = new ArrayList<MapAttr>();
		if (curs.moveToFirst()) {
			do {
				list.add(new MapAttr(curs.getLong(0),curs.getFloat(1),curs.getFloat(2)));
			} while (curs.moveToNext());
		}
		curs.close();
		return list;
	}

	protected MapAttr calcMapAttributes(long t1,long t2) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor curs;
		MapAttr mapAttr = new MapAttr(100,40.0,8.0);
		// Defaults
		mapAttr.lat = 40f;
		mapAttr.lng = 8f;
		mapAttr.zoom = 14;
		// Process
		if (t1 == t2) {		// Snapshot
			curs = db.rawQuery("SELECT lat,lng FROM frames WHERE ts="+t2,null);
			if (curs.moveToFirst()) {
				mapAttr.lat = curs.getFloat(0);
				mapAttr.lng = curs.getFloat(1);
			}
		} else {			// Record
			// Get bound rectangle
			curs = db.rawQuery("SELECT min(lat),max(lat),min(lng),max(lng) FROM frames WHERE ts BETWEEN "+t1+" AND "+t2,null);
			if (curs.moveToFirst()) {
				// Calc the center
				mapAttr.lat = (curs.getFloat(0) + curs.getFloat(1)) / 2;
				mapAttr.lng = (curs.getFloat(2) + curs.getFloat(3)) / 2;
				// Set bound rectangle
				// Farther point
				// Hypothenuse length
				double h = Math.sqrt(
					(float)Math.pow(curs.getFloat(1)-curs.getFloat(0), 2d) +
					(float)Math.pow(curs.getFloat(3)-curs.getFloat(2), 2d)
				);
				mapAttr.dim = h;
				// Calc the zoom -- FIXME Further test and adjustment
				// Home-Coevering 0.00682 ~800m
				// Ein-Antw-Bru 1.22606
				int zoom;
				if (h > 1.7) zoom = 7;
				else if (h > 1.2) zoom = 8;
				else if (h > 0.9) zoom = 9;
				else if (h > 0.5) zoom = 10;
				else if (h > 0.1) zoom = 11;
				else if (h > 0.075) zoom = 12;
				else if (h > 0.025) zoom = 13;
				else if (h > 0.01) zoom = 14;
				else if (h > 0.005) zoom = 15;
				else if (h > 0.0001) zoom = 16;
				else if (h > 0.00015) zoom = 17;
				else if (h > 0.00021) zoom = 18;
				else zoom = 19;
				mapAttr.zoom = zoom;
//Log.d("***", "hyp: "+Lib.round5(h)+", zoom: "+zoom);
			}
		}
		curs.close();
		return mapAttr;
	}

	public void rec2snapshot(long ts) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("UPDATE frames SET a="+fSingle+" WHERE ts="+ts+" AND a="+fSerie);
	}

	public void setTag (long id, String tag) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("UPDATE toc SET tag='"+escape(tag)+"' WHERE rowid="+id);
	}

	private int getA(long t1, long t2) {
		return (t1 == t2 ? fSingle : fSerie);
	}

	private String escape(String s) {
		return s.replace("'","''");
	}

	protected void snapshot(Location loc) {
		SQLiteDatabase db = getWritableDatabase();
		addFrame(db,loc.getTime(),loc.getLatitude(),loc.getLongitude(),loc.getAccuracy(),loc.getProvider(),fSingle);
		addIndex(db,loc.getTime(),loc.getTime(),Lib.ts2sdts(loc.getTime()));
	}

}
