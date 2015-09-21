package com.vera5.gpsrec;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;

public class MapViewInfo extends Activity {

  private static TextView mLog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapviewinfo);
		mLog = (TextView) findViewById(R.id.gpslog);
		Bundle extras = getIntent().getExtras();
		mLog.append(extras.getString("csv"));
	}

}
