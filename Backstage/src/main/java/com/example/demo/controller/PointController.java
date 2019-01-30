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
import com.example.demo.service.PointService;

//为乘客选择打车点
@Controller
@RequestMapping("/point")
public class PointController {
	
	private final static Logger logger=LoggerFactory.getLogger(PointController.class);
	@Autowired
	private PointService pointService=new PointService();
	@Autowired
	private MapService mapService=new MapService();
	
	
	@ResponseBody
	@RequestMapping("/simple")
	public String getSimplePoints(@RequestParam(value="longitude",required=true) double longitude,
			@RequestParam(value="latitude",required=true) double latitude) {
		logger.info("接收到请求乘客打车点请求:longitude="+longitude+" latitude="+latitude);
		Point point=new Point(longitude,latitude);
		List<Map>near=mapService.getNear(longitude, latitude);
		List<Point>points =pointService.getSimplePoint(near, point);
		return JSON.toJSONString(points);
	}
}
