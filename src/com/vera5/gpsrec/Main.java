package com.vera5.gpsrec;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class Main extends ListActivity {

  private gpsDatabase db;
  private ListView lv;
  private dbAdapter adapter;
  private LocationManager locationManager;
  private LocationListener locationListener;
  private String provider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);
		try {
			Location lastGood = locationManager.getLastKnownLocation(provider);
			if (lastGood == null)
				Tooltip("Last known location unavailable");
			else
				setTitle(getString(R.string.app_name)+" @"+lastGood.getLatitude()+","+lastGood.getLongitude());
		} catch (Exception e) {
			Log.e("***", e.getMessage());
		}
		locationListener = new MyListener();
Log.d("***", "ts: "+System.currentTimeMillis());
		db = new gpsDatabase(this);
        Cursor curs = db.query("SELECT rowid AS _id,t1,t2,tag FROM toc ORDER BY t2 DESC");
        startManagingCursor(curs);
        adapter = new dbAdapter(this,curs);
		lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
				Cursor curs = (Cursor) adapter.getItem(position);
				// Populate location array from frames and pass to the view
Log.d("***", "onclick: "+id+", t1="+curs.getFloat(1)+", t2="+curs.getFloat(2));
				startActivity(new Intent(".MapView"));
			}
		});
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id) {
				Cursor curs = (Cursor) adapter.getItem(position);
				Delete(curs,id);
				return true;
			}
		});
		lv.setAdapter(adapter);
	}

	private void Delete(final Cursor curs, final long id) {
		new AlertDialog.Builder(this)
			.setMessage("Delete "+curs.getString(3)+"?")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					db.del(curs.getLong(1),curs.getLong(2));
					adapter.getCursor().requery();
				}
			}).show();
	}

	public void Record(View view) {
Log.d("***", "Record()");
	}

	public void Snapshot(View view) {
Log.d("***", "Snapshot()");
	}

	private void Tooltip(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	final class MyListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			Log.d("***", ""+location.getLatitude()+","+location.getLongitude()+" ("+Lib.round1(location.getAccuracy())+")");
			Tooltip(""+location.getLatitude()+","+location.getLongitude());
		}
		@Override
		public void onProviderDisabled(String provider) {
			Tooltip(provider+" disabled");
		}
		@Override
		public void onProviderEnabled(String provider) {
			Tooltip(provider+" enabled");
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("***", "Status of "+provider+" changed to "+status);
		}
	}

}
