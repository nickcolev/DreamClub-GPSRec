package com.vera5.gpsrec;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class Main extends ListActivity {

  private gpsDatabase db;
  private ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
Log.d("***", "ts: "+System.currentTimeMillis());
		db = new gpsDatabase(this);
        lv = getListView();
        Cursor curs = db.query("SELECT rowid AS _id,t1,t2,tag FROM toc ORDER BY t2 DESC");
        startManagingCursor(curs);
        dbAdapter adapter = new dbAdapter(this,curs);
		lv.setAdapter(adapter);
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

}
