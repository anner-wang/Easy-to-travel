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


	/**
	 * 大体思路：
	 * 1.将全部的Map点按照距离起点的距离进行从小到大的排序
	 * 2.将排序好的Map点根据象限的不同分为四组
	 * 3.每一组里面在不改变相对顺序的情况下挑出最大的四个点
	 * 4.得到需要规划的四条路径
	 * 5.注意点：可能四个点之间的距离会比较集中，需要后续的网格化处理
	 * @param near
	 * @param startPoint
	 * @return
	 */
	//简单路径查询
	public List<List<Point>>simplePath(List<Map> near,Point startPoint){
		logger.info("司机巡游路线终点规划完成，如下:");
		//1.将全部的Map点按照距离起点的距离进行从小到大的排序
		List<Map>distanceNear=new ArrayList<>();
		int size=near.size();
		for(int i=0;i<size;i++){
			int minindex=0;
			for(Map point:near){
				if(getDistance(startPoint,point)<getDistance(startPoint,near.get(minindex))){
					minindex=near.indexOf(point);
				}
			}
			distanceNear.add(near.get(minindex));
			near.remove(minindex);

		}

		List<List<Point>>answer=new ArrayList<List<Point>>();


		
		//四个不同的方向
		List<Map>maps1=new ArrayList<Map>();
		List<Map>maps2=new ArrayList<Map>();
		List<Map>maps3=new ArrayList<Map>();
		List<Map>maps4=new ArrayList<Map>();


		//2.将排序好的Map点根据象限的不同分为四组
		while(distanceNear.size()>0) {
			Map nextMap=distanceNear.get(0);
			double longitude=nextMap.getLongitude();
			double latitude=nextMap.getLatitude();
			double longitude_difference=longitude-startPoint.getLongitude();
			double latitude_difference=latitude-startPoint.getLatitude();
			
			//logger.info("longitude_difference="+longitude_difference+" latitude_difference="+latitude_difference);
			if(longitude_difference>0&&latitude_difference>0) {
				/*
				 * if(longitude_difference<0.5||latitude_difference<0.5) {
				 * points1.add(nextPoint); }
				 */
				maps1.add(nextMap);
			}
			if(longitude_difference<0&&latitude_difference>0) {
				/*
				 * if(longitude_difference>-0.5||latitude_difference<0.5) {
				 * points2.add(nextPoint);
				 * 
				 * }
				 */
				maps2.add(nextMap);
			}
			if(longitude_difference<0&&latitude_difference<0) {
				/*
				 * if(longitude_difference>-0.5||latitude_difference>-0.5) {
				 * points3.add(nextPoint);
				 * 
				 * }
				 */
				maps3.add(nextMap);
			}
			if(longitude_difference>0&&latitude_difference<0) {
				/*
				 * if(longitude_difference<0.5||latitude_difference>-0.5) {
				 * points4.add(nextPoint);
				 * 
				 * }
				 */
				maps4.add(nextMap);
			}
			near.remove(0);
		}

		//3.每一组里面在不改变相对顺序的情况下挑出最大的四个点
		List<Point>points1=toMaxFourNumber(maps1);;
		List<Point>points2=toMaxFourNumber(maps2);;
		List<Point>points3=toMaxFourNumber(maps3);;
		List<Point>points4=toMaxFourNumber(maps4);;
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



	private static final double EARTH_RADIUS = 6378.137;//地球半径,单位千米
	private static double rad(double d)
	{
		return d * Math.PI / 180.0;
	}
	//计算两个经纬度之间的距离
	public static double getDistance(Point start,Map end)
	{
		double radLat1 = rad(start.getLatitude());
		double radLat2 = rad(end.getLatitude());
		double a = radLat1 - radLat2;
		double b = rad(start.getLatitude()) - rad(end.getLatitude());

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
				Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}


	//为每条路径选取里面最大的四个值
	public static List<Point> toMaxFourNumber(List<Map>mapList){
		List<Point>pointList=new ArrayList<>();
		if(mapList.size()<=4){
			for(int i=0;i<mapList.size();i++){
				pointList.add(new Point(mapList.get(i).getLongitude(),mapList.get(i).getLatitude()));
			}
			return pointList;
		}
		//不改变相对顺序的情况下,选取最大的四个点作为返回数据
		int no4,no3,no2,no1; //no4为最小的Map点的下标，no1为最大的Map点的下标
		no4=no3=no2=no1=0;
		for(int i=1;i<mapList.size();i++ ){
			if(mapList.get(i).getPredictNumber()>mapList.get(no1).getPredictNumber()){
				no4=no3;
				no3=no2;
				no2=no1;
				no1=i;
			}else if(mapList.get(i).getPredictNumber()>mapList.get(no2).getPredictNumber()){
				no4=no3;
				no3=no2;
				no2=i;
			}else if(mapList.get(i).getPredictNumber()>mapList.get(no3).getPredictNumber()){
				no4=no3;
				no3=i;
			}else if(mapList.get(i).getPredictNumber()>mapList.get(no4).getPredictNumber()){
				no4=i;
			}
		}
		pointList.add(new Point(mapList.get(no4).getLongitude(),mapList.get(no4).getLatitude()));
		pointList.add(new Point(mapList.get(no3).getLongitude(),mapList.get(no3).getLatitude()));
		pointList.add(new Point(mapList.get(no2).getLongitude(),mapList.get(no2).getLatitude()));
		pointList.add(new Point(mapList.get(no1).getLongitude(),mapList.get(no1).getLatitude()));
		return pointList;
	}
}
