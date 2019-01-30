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
import com.example.demo.service.MapService;

@Controller
@RequestMapping("/map")
public class MapController {
	//日志文件
	private static final Logger logger=LoggerFactory.getLogger(MapController.class);
	@Autowired
	private MapService mapService=new MapService();
	
	@ResponseBody
	@RequestMapping("/list")
	public 	List<Map>getMap(){
		logger.info("从数据库读取map的全部信息");
		logger.info("信息读取如下:");
		logger.info(mapService.toString());
		return mapService.getList();
	}
	
	@ResponseBody
	@RequestMapping("/near")
	public String getNearMap(@RequestParam(value="longitude",required=true) double longitude,
			@RequestParam(value="latitude",required=true) double latitude){
		logger.info("接收到请求乘客附近热力图请求:longitude="+longitude+" latitude="+latitude);
		List<Map>map= mapService.getNear(longitude, latitude);
		logger.info("后台返回前端"+map.size()+"条热力图数据");
		//mapService.updateDatabase(longitude,latitude);
		return JSON.toJSONString(map);
	}
}
