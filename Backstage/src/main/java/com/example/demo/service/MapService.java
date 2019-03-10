package com.example.demo.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.jar.JarOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.Map;

/*
 * 生成热力图数据
 * */
@Service
public class MapService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private final static Logger logger=LoggerFactory.getLogger(MapService.class);
	
	//展示全部信息
		public List<Map>getList(){
			String sql="select longitude,latitude,predictNumber from map";
			return jdbcTemplate.query(sql, new RowMapper<Map>() {
				@Override
				public Map mapRow(ResultSet rs, int rowNum) throws SQLException {
					// TODO Auto-generated method stub
					Map map=new Map();
					map.setLongitude(rs.getDouble("longitude"));
					map.setLatitude(rs.getDouble("latitude"));
					map.setPredictNumber(rs.getInt("predictNumber"));
					return map;
				}
			});
		}
		//展示附近的信息
		public List<Map>getNear(double longitude,double latitude){
			String sql="select longitude,latitude,predictNumber from map where longitude-"+String.valueOf(longitude)+"<1 or latitude-"
					+String.valueOf(latitude)+"< 1";
			List<Map> map= jdbcTemplate.query(sql, new RowMapper<Map>() {
				@Override
				public Map mapRow(ResultSet rs, int rowNum) throws SQLException {
					// TODO Auto-generated method stub
					Map map=new Map();
					map.setLongitude(rs.getDouble("longitude"));
					map.setLatitude(rs.getDouble("latitude"));
					map.setPredictNumber(rs.getInt("predictNumber"));
					return map;
				}
			});
			return map;
		}
		//写入数据库数据
		public void insertInfo(Map map) {
			double longitude=map.getLongitude();
			double latitude=map.getLatitude();
			int predictNumber=map.getPredictNumber();
			String sql="insert into map (longitude,latitude,predictNumber ) select "+longitude+","+latitude+""
					+ ","+predictNumber+" from dual where not exists (select longitude,latitude from map where longitude="+longitude+" and "
							+ "latitude ="+latitude+")";
			jdbcTemplate.update(sql);
			
		}
		//更新数据库预测值
		public void update() {
			int predictNumber=15;
			int index=0;
			List<Map>maps=getList();
			for(int i=0;i<maps.size();i++) {
				double longitude=maps.get(i).getLongitude();
				double latitude=maps.get(i).getLatitude();
				int number=new Random().nextInt(100)+predictNumber;
				maps.set(i, new Map(longitude,latitude,number));
				//数据库操作
				String sql="delete from map where longitude="+longitude+"and latitude="+latitude;
				jdbcTemplate.update(sql);
				sql="insert into map values ("+longitude+","+latitude+","+number+")";
				jdbcTemplate.update(sql);
				index++;
			}
			
			logger.info("后台数据周期更新完成,更新:"+index+"条数据");
		}

		//清空数据库信息
		public void deleteAllInfo() {
			String sql="delete from map where 1=1";
			jdbcTemplate.update(sql);
		}
}
