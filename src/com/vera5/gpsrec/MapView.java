package com.vera5.gpsrec;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import java.util.List;

public class MapView extends Activity {

  private WebView webview;
  private String tag;
  private long id, t1, t2;
  private gpsDatabase db;
  private MapAttr mapAttr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		// Process args
		Bundle extras = getIntent().getExtras();
		id  = extras.getLong("id");
		tag = extras.getString("tag");
		t1  = extras.getLong("t1");
		t2  = extras.getLong("t2");
		setTitle(tag == null ? Lib.ts2dts(t2) : tag);
		// db
		db = new gpsDatabase(this);
		mapAttr = db.calcMapAttributes(t1,t2);
//Tooltip("dim: "+Lib.round5(mapAttr.dim)+", zoom: "+mapAttr.zoom);
		// Setup the WebView
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(false);
		webview.getSettings().setSupportZoom(false);
		String html = setHtml("");
//Log.d("***", html);
		webview.loadData(html,"text/html","UTF-8");
	}

	private String setHtml(String markers) {
		return "<script src=\"http://maps.google.com/maps/api/js?sensor=false\" type=\"text/javascript\"></script>\n"
			+ "<style type=\"text/css\">\n"
			+ "html, body { body: 0; margin: 0; }\n"
			+ "#tag { border: 0; margin: 0; }\n"
			+ "</style>\n"
			+ "<div id=\"map\"></div>\n"
			+ "<script type=\"text/javascript\">\n"
			+ "var o = document.getElementById('map');\n"
			+ "o.style.width = window.innerWidth;\n"
			+ "o.style.height = window.innerHeight;\n"
			+ "var map = new google.maps.Map(document.getElementById('map'), {\n"
			+ "  zoom: "+mapAttr.zoom+",\n"
			+ "  center: new google.maps.LatLng("+mapAttr.lat+","+mapAttr.lng+"),\n"
			+ "  mapTypeId: google.maps.MapTypeId.ROADMAP\n"
			+ "});\n"
			+ setMapMarkers(markers)
			// FIXME See 'map.getBounds(bounds)'
			// var bounds = new google.maps.LatLngBounds();
			//+ "var bounds = new google.maps.LatLngBounds();\n"
			//+ "map.setCenter(bounds.getCenter());\n"
			//+ "map.fitBounds(bounds);\n"
			+ "</script>";
	}

	private String setMapMarkers(String markers) {
		List<MapAttr> list = db.getMarkers(t1,t2);
		switch(list.size()) {
			case 0:
				return "";
			case 1:		// Snapshot
				MapAttr ma = list.get(0);
				return jsMarker(ma,"marker",null);
			default:
				return jsRecord(list);
		}
	}

	private String jsRecord(List<MapAttr> a) {
		String firstMarker="",lastMarker="",farMarker="";
		double lat=0,lng=0,fFar=0,fpit;
		MapAttr ma;
		int iFar=0, size=a.size();
		for (int i=0; i<size; i++) {
			ma = a.get(i);
			if (i == 0) {	// Save the first point
				lat = ma.lat;
				lng = ma.lng;
				firstMarker = jsMarker(a.get(0),"markerA","A");
			}
			if (i == (size - 1)) {	// Last
				lastMarker = jsMarker(a.get(size-1),"markerB","B");
			}
			if (i > 0) {
				fpit = calcDistance(ma,lat,lng);
				if (fpit > fFar) {
					fFar = fpit;
					iFar = i;
				}
			}
		}
		if (iFar > 0) farMarker = jsMarker(a.get(iFar),"markerF","F");
		return firstMarker + farMarker + lastMarker + jsPolyline(a);
	}

	private double calcDistance(MapAttr ma, double lat, double lng) {
		return Math.pow(Math.abs(ma.lat-lat),2)+Math.pow(Math.abs(ma.lng-lng),2);
	}

	private String jsMarker(MapAttr p, String name, String label) {
		return " var "+name+" = new google.maps.Marker({\n"
			+ "  position: new google.maps.LatLng("+p.lat+","+p.lng+"),\n"
			+ (label == null ? "" : "  label: '"+label+"',\n")
			+ "  map: map\n"
			+ " });\n"
			+ "var infowindow = new google.maps.InfoWindow();\n"
			+ "google.maps.event.addListener("+name+", 'click', (function(marker) {\n"
			+ "   return function() {\n"
			+ "    infowindow.setContent('"+Lib.ts2dts(p.time)+" @ "+Lib.round5(p.lat)+","+Lib.round5(p.lng)+"');\n"
			+ "    infowindow.open(map, "+name+");\n"
			+ "   }\n"
			+ "  })("+name+"));\n";
	}

	private String jsPolyline(List<MapAttr> a) {
		String l = "";
		for (MapAttr o: a) {
			l += ",new google.maps.LatLng("+o.lat+","+o.lng+")";
		}
		if (l.startsWith(",")) l = l.substring(1);
		String s = "var coords = [" + l + "];\n"
			+ "var path = new google.maps.Polyline({\n"
			+ " path: coords,\n"
			//+ " geodesic: true,\n"
			+ " strokeColor: '#0000FF',\n"
			+ " strokeOpacity: 0.5,\n"
			+ " strokeWeight: 2\n"
			+ "});\n"
			+ "path.setMap(map);\n";
		return s;
	}

	private void Tooltip(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.tag:
				tagDialog();
				return true;
			case R.id.view_send:
				viewSend();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void tagDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setText(tag);
		input.selectAll();
		//alert.setMessage("Enter Your Message");
		alert.setTitle("Tag");
		alert.setView(input);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			tag = input.getText().toString();
				setTitle(tag);
				db.setTag(id,tag);
			}
		});
		alert.show();
	}

	private String getMarkersCSV(long t1, long t2) {
		MapAttr ma;
		List<MapAttr> list = db.getMarkers(t1,t2);
		int size = list.size();
		// Collect data
		String csv = "CSV\nTime,Lattitude,Longitude,Accuracy,Provider\n";
		for (int i=0; i<size; i++) {
			ma = list.get(i);
			csv += ""+Lib.ts2dts(ma.time)+","+ma.lat+","+ma.lng+","+Lib.round1(ma.p)+","+ma.src+"\n";
		}
		return csv;
	}

	private void viewSend() {
		// Send email
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:")); // only email apps should handle this
		intent.putExtra(Intent.EXTRA_SUBJECT, "StopWatch log");
		intent.putExtra(Intent.EXTRA_TEXT, getMarkersCSV(t1,t2));
		if (intent.resolveActivity(getPackageManager()) != null)
			startActivity(intent);
	}

}
