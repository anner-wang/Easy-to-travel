package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RestTemplateController {
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/testAPI")
	public 	String getJson() {
		String url="http://api.map.baidu.com/place/v2/suggestion?query=大学&region=武汉&city_limit=true&output=json&ak=AXclZFCYBqfM8nBDloQ3uGQFr54MV9Q4";
		ResponseEntity<String>results=restTemplate.exchange(url, HttpMethod.GET,null,String.class);
		String json=results.getBody();
		return json;
	}
}
