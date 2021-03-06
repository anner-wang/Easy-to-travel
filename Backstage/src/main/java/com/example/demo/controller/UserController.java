package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.User;
import com.example.demo.service.PointService;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

//为乘客选择打车点
@Controller
@RequestMapping("/user")
public class UserController {
	
	private final static Logger logger=LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService=new UserService();
	
	
	@ResponseBody
	@RequestMapping("/login")
	public User login(@RequestParam(value="account",required=true) String account,
					  @RequestParam(value="password",required=true) String password) {
		User user;
		try {
			user= userService.login(account,password);
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		user=new User();
		user.type=User.TYPE_wrong;
		return user;
	}

	@ResponseBody
	@RequestMapping("/get")
	public User get(@RequestParam(value="account",required=true) String account,
					@RequestParam(value="password",required=true) String password) {
		User user;
		user= userService.get(account,password);
		if(user!=null){
			user.type=User.TYPE_getUserInfor;
		}else{
			user=new User();
			user.type=User.TYPE_wrong;
		}
		return user;
	}

	@ResponseBody
	@RequestMapping("/signup")
	public User signup(@RequestParam(value="account",required=true) String account,
					  @RequestParam(value="password",required=true) String password,
					   @RequestParam(value="username",required=false) String username
					   ) {
		User user;
		try {
			user= userService.signup(account,password);
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		user=new User();
		user.type=User.TYPE_wrong;
		return user;
	}

	@ResponseBody
	@RequestMapping("/update")
	public User update(@RequestParam(value="account",required=true) String account,
					  @RequestParam(value="password",required=true)String password ,
					   @RequestParam(value="username",required=false)String username,
					   @RequestParam(value="age",required=false)Integer age,
					   @RequestParam(value="balance",required=false)Double balance,
					   @RequestParam(value="carLicense",required=false)String carLicense,
					   @RequestParam(value="carType",required=false)String carType,
					   @RequestParam(value="driveDistance",required=false)Integer driveDistance,
					   @RequestParam(value="customers",required=false)Integer customers,
					   @RequestParam(value="userType",required=false)Integer userType
					   ) {
		User user=userService.get(account,password);
		user.type=User.TYPE_update;
		if(user==null){
			user = new User();
			user.type=User.lOGIN_noUser;
			return user;
		}
		try{
			if(username!=null)userService.update(account,password,"name",username);
			if(carLicense!=null)userService.update(account,password,"carLicense",carLicense);
			if(carType!=null)userService.update(account,password,"carType",carType);
			if(age!=null)userService.update(account,password,"age",age+"");
			if(balance!=null)userService.update(account,password,"balance",balance+"");
			if(driveDistance!=null)userService.update(account,password,"driveDistance",driveDistance+"");
			if(customers!=null)userService.update(account,password,"customers",customers+"");
			if(userType!=null)userService.update(account,password,"userType",userType+"");
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Wrong!");
			user.type=User.TYPE_wrong;
		}

		return user;
	}
}
