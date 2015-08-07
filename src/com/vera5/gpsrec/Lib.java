package com.vera5.gpsrec;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Lib {

  private final static String fmtDate = "yyyy-MM-dd";
  private final static String fmtDateTime = "yyyy-MM-dd HH:mm:ss";

	protected static long dts2ts(String DateTimeString) {
		SimpleDateFormat fmt = new SimpleDateFormat(fmtDateTime);
		try {
			Date date = (Date)fmt.parse(DateTimeString);
			return date.getTime();
		} catch (ParseException e) {
			return -1L;
		}
	}

	protected static boolean isDateString(String s) {
		return s.matches("^\\d{4}\\-\\d{2}\\-\\d{2}$");
	}

	protected static boolean isSnapshot(long t1, long t2) {
		return (t1 == t2);
	}

	protected static String round1(double f) {
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(f);
	}

	protected static String round5(double f) {
		DecimalFormat df = new DecimalFormat("#.#####");
		return df.format(f);
	}

	protected static String ts2dts(long timestamp) {	// TimeStamp-to-DateTimeString
		return ts2ds(timestamp,"yyyy-MM-dd HH:mm:ss");
	}

	protected static String ts2sdts(long timestamp) {	// TimeStamp-to-ShortDateTimeString
		return ts2ds(timestamp,"yyyy-MM-dd HH:mm");
	}

	protected static String ts2ts(long timestamp) {		// TimeStamp-to-TimeString
		return ts2ds(timestamp,"HH:mm");
	}

	protected static String ts2ds(long timestamp,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(timestamp));
	}

}
