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
	@Autowired
	private RestTemplate restTemplate;
	
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
		//添加虚假数据
		public void addInfo(){
			int predictNumber=15;
			int index=0;
			
			
		/*
		 * String []location=
		 * {"大学","光谷","医院","景点","购物中心","地铁","高架桥","路口","公交车站","酒吧","光谷","楚河大道"
		 * ,"软件园","超市","影院","东湖绿道","雄楚大道","南湖","湖北工业大学","武汉科技大学","石牌岭","丁字桥","珞狮路","书城路"
		 * ,"黄鹤楼","户部巷","汉口","火车站","公司","武汉理工大学余家头校区","友谊大道","联盟小区",
		 * "和平大道","岳家嘴","铁机路","华城广场","群星城","武汉大学","湖北大学","肖品茂","欢乐谷","公园","武汉理工大学南湖校区",
		 * "升升公寓"};
		 */
			String []location= {"南湖","湖北工业大学","武汉科技大学","石牌岭","丁字桥","珞狮路","书城路","武汉理工大学南湖校区","升升公寓"};	
			String url="http://api.map.baidu.com/place/v2/suggestion?query=学校&region=武汉&city_limit=true&output=json&ak=AXclZFCYBqfM8nBDloQ3uGQFr54MV9Q4";
			ResponseEntity<String>results=restTemplate.exchange(url, HttpMethod.GET,null,String.class);
			String json=results.getBody();
			JSONObject jsonObj=JSON.parseObject(json);
			JSONArray results1=jsonObj.getJSONArray("result");
			for(int i=0;i<100;i++) {
				url="http://api.map.baidu.com/place/v2/suggestion?query="+location[new Random().nextInt(9)]+"&region=武汉&city_limit=true&output=json&ak=AXclZFCYBqfM8nBDloQ3uGQFr54MV9Q4";
				results=restTemplate.exchange(url, HttpMethod.GET,null,String.class);
				json=results.getBody();
				jsonObj=JSON.parseObject(json);
				results1=jsonObj.getJSONArray("result");
					//JSONObject o=(JSONObject) results1.get(0);
					logger.info("返回的城市"+location[i]+"预测热点信息如下:");
					for(int j=1;j<results1.size();j++) {
						JSONObject temp1=(JSONObject)results1.get(j);
						JSONObject temp2=(JSONObject)temp1.getJSONObject("location");
						double longitude=Double.parseDouble(temp2.getString("lng"));
						double latitude=Double.parseDouble(temp2.getString("lat"));
						int number=predictNumber+new Random().nextInt(100);
						insertInfo(new Map(longitude,latitude,number));
						logger.info(temp1.getString("name")+"\t经度:"+temp2.getString("lng")+"\t纬度:"+temp2.getString("lat")+"\t 预测值:"+number);
						index++;
					}
				}
			
			logger.info("数据更新完成,更新:"+index+"条数据");
		}
	
	//定时更新数据库map信息
		@Scheduled(initialDelay=3000,fixedRate=10000)
		public void updateDatabase() {		
			logger.info(System.currentTimeMillis()+"开始预测城市热点信息"); 
			//deleteAllInfo(); 
			update();
			//addInfo();
			logger.info(System.currentTimeMillis()+"热点数据更新成功");
	}
}
