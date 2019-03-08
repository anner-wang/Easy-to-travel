package com.example.demo.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.jar.JarOutputStream;

import com.example.demo.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/*
 * 用户登入，注册，更新信息
 * */
@Service
public class UserService {
    //用户信息表名
    public static final String usertablename="user_info";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static Logger logger=LoggerFactory.getLogger(UserService.class);

    /**
     * 登入
     */
    public User login(String account, String password) throws SQLException, IOException {
        User user=get(account);
        if(user!=null) {
            //搜索到了User
            if(user.getPassWord().equals(password)) {
                //密码正确
                user.type=User.TYPE_login;
            }else {
                //密码错误
                user.type=User.lOGIN_wrongPassword;
            }
        }else {
            //未搜索到User
            user=new User();
            user.type=User.lOGIN_noUser;
        }
        return user;
    }



    /**
     * 注册
     */
    public User signup(String account,String password) throws SQLException, IOException {
        User user=get(account);
        if(user!=null) {
            //已经存在该账号
            user = new User();
            user.type=User.SIGNUP_alreadyHasThisUser;
            return user;
        }else {
            int resultCode=jdbcTemplate.update("insert into "+usertablename+"(account,password) values(?,?)", account,password);
            if(resultCode!=-1) user=get(account);
            user.type=User.TYPE_signup;
            return user;
        }
    }

    /**
     * 更新用户信息
     */
    public User update(String account,String password,String type,String value) throws Exception {
        if(get(account,password)!=null){
            if(type.equals("name")||type.equals("carLicense")||type.equals("carType")){
                jdbcTemplate.update("update "+usertablename+" set "+type+" = '"+value+"' where account = ?",account);
            }else{
                jdbcTemplate.update("update "+usertablename+" set "+type+" = ? where account = ?", value,account);
            }

            User user=get(account);
            user.type=User.TYPE_update;
            return user;
        }else{
            User user=get(account);
            user.type=User.lOGIN_wrongPassword;
            return user;
        }
    }

    /**
     * 根据账号查询用户信息
     */
    public User get(String account) {
        List<User> result = jdbcTemplate.query("select * from "+usertablename+" where account = ?",
                new Object[] {account }, new BeanPropertyRowMapper(User.class));
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
    /**
     * 根据账号和密码查询用户信息
     */
    public User get(String account,String password) {
        List<User> result = jdbcTemplate.query("select * from "+usertablename+" where account=? and password=?",
                new Object[] {account ,password}, new BeanPropertyRowMapper(User.class));
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }



}
