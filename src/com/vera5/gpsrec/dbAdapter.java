package com.vera5.gpsrec;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class dbAdapter extends CursorAdapter {
  private final Context context;

	public dbAdapter(Context context, Cursor cursor) {
		super(context,cursor);
		this.context = context;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.row, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor curs) {
		ImageView ico = (ImageView) view.findViewById(R.id.ico);
		TextView tag  = (TextView) view.findViewById(R.id.tag);
		long t1 = curs.getLong(1);
		long t2 = curs.getLong(2);
		ico.setImageResource(t1 == t2 ? R.drawable.ic_action_photo : R.drawable.ic_action_video);
		tag.setText(curs.getString(3)==null ? Lib.ts2dts(t2) : curs.getString(3));
	}

}