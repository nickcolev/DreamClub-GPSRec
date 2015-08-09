package com.vera5.gpsrec;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
Tooltip("dim: "+Lib.round5(mapAttr.dim)+", zoom: "+mapAttr.zoom);
		// Setup the WebView
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(false);
		webview.getSettings().setSupportZoom(false);
		String html = setHtml(getMarkers());
//Log.d("***", html);
		webview.loadData(html,"text/html","UTF-8");
	}

	private String setHtml(String markers) {
Log.d("***", "mapAttr.zoom="+mapAttr.zoom+", LatLng="+mapAttr.lat+","+mapAttr.lng);
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
			+ "</script>";
	}

	private String setMapMarkers(String markers) {
		return "var locations = [\n" + markers + "\n];\n"
			+ "var infowindow = new google.maps.InfoWindow();\n"
			+ "var marker, i;\n"
			+ "for (i = 0; i < locations.length; i++) {\n"
			+ " marker = new google.maps.Marker({\n"
			+ " position: new google.maps.LatLng(locations[i][1], locations[i][2]),\n"
			+ " map: map\n"
			+ "});\n"
			+ "google.maps.event.addListener(marker, 'click', (function(marker, i) {\n"
			+ " return function() {\n"
			+ "  infowindow.setContent(locations[i][0]);\n"
			+ "  infowindow.open(map, marker);\n"
			+ " }\n"
			+ "})(marker, i));\n"
			+ "}\n";
	}

	private String getMarkers() {
		return db.getMarkers(t1,t2);
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

}
