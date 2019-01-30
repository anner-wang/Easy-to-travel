package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.Map;
import com.example.demo.bean.Point;
import com.example.demo.service.MapService;
import com.example.demo.service.PathService;

/*
 * 路径点规划
 * */

@Controller
@RequestMapping("/path")
public class PathController {

	//日志文件
	private final static Logger logger=LoggerFactory.getLogger(PathController.class);
	//获取SERVICE实例
	@Autowired
	private PathService pathService=new PathService();
	@Autowired
	private MapService mapService=new MapService();
	
	@ResponseBody
	@RequestMapping("/simple")
	public String getPosition(@RequestParam(value="longitude",required=true) double longitude,
			@RequestParam(value="latitude",required=true) double latitude) {
		logger.info("接收到请求司机巡航路线请求:longitude="+longitude+" latitude="+latitude);
		Point startPoint=new Point(longitude,latitude);
		  List<Map>near=mapService.getNear(longitude, latitude);
		  //简单路径选择算法
		  List<List<Point>> points=pathService.simplePath(near,startPoint); 
		  return JSON.toJSONString(points);
		 
	}
}
