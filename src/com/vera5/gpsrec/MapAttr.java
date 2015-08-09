package com.vera5.gpsrec;

public class MapAttr {
	double lat;
	double lng;
	LatLng center;
	int zoom;
	double dim;
	public MapAttr() {
		this.center = new LatLng();
	}
	public class LatLng {
		double lat;
		double lng;
	}
}
