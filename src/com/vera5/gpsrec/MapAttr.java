package com.vera5.gpsrec;

public class MapAttr {
	long time;
	double lat;
	double lng;
	int zoom;
	double dim;
	public MapAttr(long time,double lat,double lng) {
		this.time = time;
		this.lat = lat;
		this.lng = lng;
	}
	class LatLng {
		double lat;
		double lng;
	}
}
