package com.vera5.gpsrec;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import android.util.Log;

public class MapView extends Activity {

  private WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		WebView webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		//webview.setWebChromeClient(new WebChromeClient());
		webview.getSettings().setBuiltInZoomControls(false);
		webview.getSettings().setSupportZoom(false);
		String html = setHtml(getMarkers());
		webview.loadData(html,"text/html","UTF-8");
	}

	private String setHtml(String markers) {
		String center = spotCenter(markers);
		String zoom = "14";
//Tooltip(center+" \n"+markers);
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
			+ "  zoom: "+zoom+",\n"
			+ "  center: new google.maps.LatLng("+center+"),\n"
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

	private String spotCenter(String s) {
		// ['tag',lat,lng,a]	FIXME awful logic
		int i = s.indexOf("[");
		if(i != -1) {
			s = s.substring(i+1);
			i = s.indexOf("]");
			if(i != -1) {
				s = s.substring(0,i);
				i = s.indexOf(",");
				if(i != -1) {
					s = s.substring(i+1);
					String[] a = s.split(",");
					s = a[0]+","+a[1];
					return s;
				}
			}
		}
		return "47.071876, 15.441456";	// FIXME
	}

	private String getMarkers() {
		Bundle extras = getIntent().getExtras();
		String tag = extras.getString("tag");
		long t1 = extras.getLong("t1"), t2 = extras.getLong("t2");
		// Process actual GPSs
		gpsDatabase db = new gpsDatabase(this);
		return db.getMarkers(t1,t2);
	}

	private void Tooltip(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

}
