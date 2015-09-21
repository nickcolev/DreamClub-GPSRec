package com.vera5.gpsrec;

public class MapAttr {
	long time;
	double lat;
	double lng;
	int zoom;
	double dim;
	float p;	// precision
	String src;	// gps, network, ...
	public MapAttr(long time,double lat,double lng,float p,String src) {
		this.time = time;
		this.lat = lat;
		this.lng = lng;
		this.p = p;
		this.src = new String(src);
	}
	class LatLng {
		double lat;
		double lng;
	}
}
