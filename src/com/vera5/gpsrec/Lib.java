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

	protected static String round1(double f) {
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(f);
	}

	protected static String round5(double f) {
		DecimalFormat df = new DecimalFormat("#.#####");
		return df.format(f);
	}

	protected static String ts2dts(long timestamp) {	// TimeStamp-to-DateTimeString
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(timestamp));
	}

	protected static String ts2ts(long timestamp) {	// TimeStamp-to-TimeString
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new Date(timestamp));
	}

	protected static CharSequence todays() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	protected static long todayl(boolean end) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, end ? 24 : 0);
		cal.set(Calendar.MINUTE, end ? 59 : 0);
		cal.set(Calendar.SECOND, end ? 59 : 0);
		return cal.getTimeInMillis();
	}

}
