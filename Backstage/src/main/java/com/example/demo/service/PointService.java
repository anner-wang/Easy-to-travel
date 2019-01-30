package com.example.demo.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.bean.Map;
import com.example.demo.bean.Point;

@Service
public class PointService {
	
	private final static Logger logger=LoggerFactory.getLogger(PointService.class);
	
	//简单返回距离最近的打车点
	public List<Point>getSimplePoint(List<Map>near,Point point){
		near.sort(Comparator.naturalOrder());
		List<Point>answer=new ArrayList<Point>();
		List<Point>temp_answer=new ArrayList<Point>();
		for(int i=0;i<4;i++) {
			temp_answer.add(new Point(near.get(i).getLongitude(),near.get(i).getLatitude()));
		}
		while(answer.size()<4&&near.size()>0) {
			Map currentPoint=near.get(0);
			double longitude1=point.getLongitude();
			double latitude1=point.getLatitude();
			double longitude2=currentPoint.getLongitude();
			double latitude2=currentPoint.getLatitude();
			double d=getDistance(longitude1+"", latitude1+"", longitude2+"", latitude2+"");
			if(d<=60000) {
				answer.add(new Point(longitude2,latitude2));
			}
			//logger.info(d+"");
			near.remove(0);
		}
		if (answer.size()==0) {
			logger.info("最高打车点确定完成，如下:");
			logger.info(temp_answer.toString());
			return temp_answer;
		}
		logger.info("打车点确定完成，如下:");
		logger.info(answer.toString());
		return answer;
	}
	//根据经纬度计算距离(m)
	public double getDistance(String longitude1,String latitude1,String longitude2,String latitude2) {
	double EARTH_RADIUS=6378137; //m
	double radLat1 = (Double.parseDouble(latitude1)*Math.PI)/180.0;
	double radLat2 = (Double.parseDouble(latitude2)*Math.PI)/180.0;
	double radLng1 = (Double.parseDouble(longitude1)*Math.PI)/180.0;
	double radLng2 = (Double.parseDouble(longitude2)*Math.PI)/180.0;
	double a = radLat1 - radLat2;
	double b = radLng1 - radLng2;	
	//google maps里面实现的算法	
	double s = 2 *Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	s = s * EARTH_RADIUS;
	return s;
	}
}
