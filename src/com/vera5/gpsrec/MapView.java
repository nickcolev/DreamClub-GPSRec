package com.vera5.gpsrec;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import android.util.Log;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

public class MapView extends Activity {

  private WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		WebView webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(false);
		String html = setHtml();
		webview.loadData(html,"text/html","UTF-8");
	}

	private int deviceHeight() {
		DisplayMetrics m = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(m);
		int height = m.heightPixels - 38;	// FIXME Find a way to get system decoration area height
		int y = Math.round(height*(m.ydpi/96));
Log.d("***", "scrX -- heightPixels: "+m.heightPixels+", ydpi: "+m.ydpi+", calc: "+y);
		return y;
	}

	private int deviceWidth() {
		DisplayMetrics m = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(m);
		int width = m.widthPixels - 0;	// FIXME Find a better way
		int x = Math.round(width*(m.xdpi/96));
Log.d("***", "scrX -- widthPixels: "+m.widthPixels+", xdpi: "+m.xdpi+", calc: "+x);
		return x;
	}

	private String setHtml() {
		return "<script src=\"http://maps.google.com/maps/api/js?sensor=false\" type=\"text/javascript\"></script>\n"
			+ "<style type=\"text/css\">\n"
			+ "html, body { body: 0; margin: 0; }\n"
			+ "#tag { border: 0; margin: 0; }\n"
			+ "</style>\n"
			+ "<div id=\"map\" style=\"width: "+deviceWidth()+"px; height: "+deviceHeight()+"px;\"></div>\n"
			+ "<script type=\"text/javascript\">\n"
			+ "var map = new google.maps.Map(document.getElementById('map'), {\n"
			+ "  zoom: 12,\n"
			+ "  center: new google.maps.LatLng(47.071876, 15.441456),\n"
			+ "  mapTypeId: google.maps.MapTypeId.ROADMAP\n"
			+ "});\n"
			+ "</script>";
	}

	private void Tooltip(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

}
