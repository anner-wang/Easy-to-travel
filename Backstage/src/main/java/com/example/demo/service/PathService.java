package com.example.demo.service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.bean.Map;
import com.example.demo.bean.Point;
import com.example.demo.controller.PathController;

@Service
public class PathService {
	
	private final static Logger logger=LoggerFactory.getLogger(PathController.class);
	//四个方向的向量
	
	
	
	//简单路径查询
	public List<List<Point>>simplePath(List<Map> near,Point startPoint){
		logger.info("司机巡游路线终点规划完成，如下:");
		
		near.sort(Comparator.naturalOrder());
		List<List<Point>>answer=new ArrayList<List<Point>>();
		
		//四个不同的方向
		List<Point>points1=new ArrayList<Point>();
		List<Point>points2=new ArrayList<Point>();
		List<Point>points3=new ArrayList<Point>();
		List<Point>points4=new ArrayList<Point>();

		while(near.size()>0) {
			Point nextPoint=new Point(near.get(0).getLongitude(),near.get(0).getLatitude());
			double longitude=nextPoint.getLongitude();
			double latitude=nextPoint.getLatitude();
			double longitude_difference=longitude-startPoint.getLongitude();
			double latitude_difference=latitude-startPoint.getLatitude();
			
			//logger.info("longitude_difference="+longitude_difference+" latitude_difference="+latitude_difference);
			if(longitude_difference>0&&latitude_difference>0&&points1.size()<4) {
				/*
				 * if(longitude_difference<0.5||latitude_difference<0.5) {
				 * points1.add(nextPoint); }
				 */
				points1.add(nextPoint);
			}
			if(longitude_difference<0&&latitude_difference>0&&points2.size()<4) {
				/*
				 * if(longitude_difference>-0.5||latitude_difference<0.5) {
				 * points2.add(nextPoint);
				 * 
				 * }
				 */
				points2.add(nextPoint);
			}
			if(longitude_difference<0&&latitude_difference<0&&points3.size()<4) {
				/*
				 * if(longitude_difference>-0.5||latitude_difference>-0.5) {
				 * points3.add(nextPoint);
				 * 
				 * }
				 */
				points3.add(nextPoint);
			}
			if(longitude_difference>0&&latitude_difference<0&&points4.size()<4) {
				/*
				 * if(longitude_difference<0.5||latitude_difference>-0.5) {
				 * points4.add(nextPoint);
				 * 
				 * }
				 */
				points4.add(nextPoint);
			}
			near.remove(0);
		}
		
		answer.add(points1);
		answer.add(points2);
		answer.add(points3);
		answer.add(points4);
		
		for (int i=1;i<=4;i++) {
			logger.info("终点"+i+": 经度="+points1.get(i-1).getLongitude()+" 纬度="+points1.get(i-1).getLatitude());
		}
		for (int i=1;i<=4;i++) {
			logger.info("终点"+i+": 经度="+points2.get(i-1).getLongitude()+" 纬度="+points2.get(i-1).getLatitude());
		}
		for (int i=1;i<=4;i++) {
			logger.info("终点"+i+": 经度="+points3.get(i-1).getLongitude()+" 纬度="+points3.get(i-1).getLatitude());
		}
		for (int i=1;i<=4;i++) {
			logger.info("终点"+i+": 经度="+points4.get(i-1).getLongitude()+" 纬度="+points4.get(i-1).getLatitude());
		}
		
		logger.info("后台巡游路线规划完成");
		//logger.info(""+answer);
		return answer;
	}
}
