package com.vera5.gpsrec;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
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
		webview.loadUrl("file:///android_asset/main.htm");
	}

	private int screenX() {
		return getWindowManager().getDefaultDisplay().getWidth(); 
	}

	private int screenY() {
		return getWindowManager().getDefaultDisplay().getHeight() - 48; 
	}

	private void Tooltip(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

}
