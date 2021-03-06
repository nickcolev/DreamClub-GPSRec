package com.vera5.gpsrec;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.*;	// Animation
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class Main extends ListActivity {

  private static final int NOTIFICATION_ID = 9315;
  // Average human walk speed is 1.4 m/s
  // Average bycicle -- 4.3 m/s
  // Car (70 km/h) -- 19.5 m/s
  // FIXME For production, select good values below
  private final long interval = 3000l;	// interval between location updates in ms
  private final float distance = 12f;	// distance between location updates in meters
  private final AlphaAnimation animation = null;
  private gpsDatabase db;
  private ListView lv;
  private dbAdapter adapter;
  private LocationManager locationManager;
  private LocationListener locationListener;
  private String provider;
  private NotificationManager mNM;
  private Notification notification;
  private boolean isRecording = false;
  private long t1rec = 0L, t2rec = 0L;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Notifications
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.ic_launcher, "", System.currentTimeMillis());
		// Location
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location lastGood = getLastKnownLocation();
		if (lastGood == null)
			Tooltip("Last known location unavailable");
		else
			updateNotifiction(sLocation(lastGood));
		locationListener = new MyListener();
		// ListView
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
				Intent intent = new Intent(".MapView");
				intent.putExtra("id", id);
				intent.putExtra("t1", curs.getLong(1));
				intent.putExtra("t2", curs.getLong(2));
				intent.putExtra("tag", curs.getString(3));
				startActivity(intent);
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
		// Listen for location updates
		locationManager.requestLocationUpdates(provider,interval,distance,locationListener);
	}

	@Override
	public void onDestroy() {
		mNM.cancel(NOTIFICATION_ID);
		locationManager.removeUpdates(locationListener);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (isRecording)
			Tooltip("Recording");
		else
			super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.db_send:
				Tooltip("db send under development");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
		if(isRecording) {
			if (t1rec == 0L)	// Nothing recorded
				Tooltip("Nothing recorded");
			else {
				db.addIndex(t1rec,t2rec,Lib.ts2sdts(t2rec));
				// Exception: When only one frame recorded --
				// Lets treat it as a snapshot.
				if (t1rec == t2rec) db.rec2snapshot(t2rec);
				adapter.getCursor().requery();
				Tooltip("Saved");
			}
			t1rec = t2rec = 0L;
			setTtl(R.string.app_name,0xFFFFFFFF);
			view.clearAnimation();
		} else {
			setTtl(R.string.ttl_rec,0xFFFFCC00);
			RecAnimationStart(view);
		}
		isRecording = !isRecording;
	}

	private void RecAnimationStart(View view) {
		final Animation animation = new AlphaAnimation(0.93f, 0.34f); // Change alpha from fully visible to invisible
		animation.setDuration(510); 	// milliseconds
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);
		view.startAnimation(animation);
	}

	public void Snapshot(View view) {
		Location loc = getLastKnownLocation();
		if (loc == null)
			Tooltip("Last known location unavailable");
		else {
			db.snapshot(loc);
			adapter.getCursor().requery();
			Tooltip("Saved");
		}
	}

	private Location getLastKnownLocation() {
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria,true);
		Location loc = null;
		try {
			loc = locationManager.getLastKnownLocation(provider);
		} catch (Exception e) {
			Log.e("***", e.getMessage());
		}
		return loc;
	}

	private void Tooltip(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	protected void updateNotifiction(String message) {
		CharSequence text = message;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Main.class), 0);
		notification.setLatestEventInfo(this, getString(R.string.app_name), text, contentIntent);
		mNM.notify(NOTIFICATION_ID, notification);
	}

	private String sLocation(Location location) {
		return	Lib.ts2ts(location.getTime()) + " @ "
			+	Lib.round5(location.getLatitude())+","+Lib.round5(location.getLongitude());
	}

	private void setTtl(int title,int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			int ttlID = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");  
			TextView ttlTV = (TextView) findViewById(ttlID);
			ttlTV.setTextColor(color);
			ttlTV.setText(title);
		} else {
			setTitle(title);
			setTitleColor(color);
		}
	}

	final class MyListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			updateNotifiction(sLocation(location));
			if(isRecording) {
				db.addFrame(location.getTime(),location.getLatitude(),location.getLongitude(),location.getAccuracy(),location.getProvider(),2);
				if(t1rec == 0L)				// Fix start time
					t1rec = location.getTime();
				t2rec = location.getTime();	// Update end time
				setTitle(getTitle()+".");
			}
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
