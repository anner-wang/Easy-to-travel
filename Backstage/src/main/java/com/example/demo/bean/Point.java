package com.example.demo.bean;

public class Point {
	private double longitude;
	private double latitude;
	public Point(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public Point() {
		// TODO Auto-generated constructor stub
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	@Override
	public String toString() {
		return "Point [longitude=" + longitude + ", latitude=" + latitude + "]";
	}
}
