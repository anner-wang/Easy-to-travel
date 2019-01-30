package com.example.demo.bean;

//热力图数据

public class Map implements Comparable<Map> {
	private double longitude;
	private double latitude;
	private int predictNumber;
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
	public int getPredictNumber() {
		return predictNumber;
	}
	public void setPredictNumber(int predictNumber) {
		this.predictNumber = predictNumber;
	}
	@Override
	public String toString() {
		return "Map [longitude=" + longitude + ", latitude=" + latitude + ", predictNumber=" + predictNumber + "]";
	}
	public Map(double longitude, double latitude, int predictNumber) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.predictNumber = predictNumber;
	}
	public Map() {
		
	}
	@Override
	public int compareTo(Map o) {
		// TODO Auto-generated method stub
		if (this.getPredictNumber()<o.getPredictNumber()) {
			return 1;
		}
		return -1;
	}
}
