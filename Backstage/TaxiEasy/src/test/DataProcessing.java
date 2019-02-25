package test;
import java.util.Enumeration;
import java.util.Hashtable;

import Util.ConstValue;
import Util.Message;

import java.sql.*;

public  class DataProcessing {

	private static boolean connectToDB=false;
	static Connection connection=null;
	static Statement statement=null;
	static ResultSet resultSet=null;
	static String tableName_user="user_info";
	static String tableName_records="records";
	static String driverName="com.mysql.cj.jdbc.Driver";
	static String url="jdbc:mysql://www.anner.wang:3306/app?useSSL=false&autoReconnect=true&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8";       
	//static String url="jdbc:mysql://localhost:3306/app?useSSL=false&autoReconnect=true&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8";       
        
    //static String url="jdbc:mysql://localhost:3306/easytravel?useSSL=false&autoReconnect=true&serverTimezone=GMT%2B8&characterEncoding=utf-8";       
    static String user="root";                                   
    static String password="permit";
    //static String password="1195593460";//permit //wjs980715
	
	
	public static  void Init(){
		try {
			if(!connectToDB) {
				Class.forName("com.mysql.cj.jdbc.Driver");
	    		connection = DriverManager.getConnection(url,user,password);   
	    		statement = connection.createStatement();
	    		System.out.println("connect to DB!");
	    		connectToDB=true;
			}
    	}catch(Exception e) {
    		e.printStackTrace();
    		connectToDB=false;
    	}
	}
	
	
	//根据账号来查询该用户是否存在
	public static Message searchUser(String account) throws SQLException{
		String sql="select * from "+tableName_user+" where account="+account;
		System.out.println(sql);
		ResultSet rs=statement.executeQuery(sql);
		while(rs.next()) {
			Message u = new Message(0, "", "");
			int userType=rs.getInt("userType");
			System.out.println("userType="+userType);
			if(userType==Message.USERTYPE_driver) {
				u.setBalance(rs.getDouble("balance"));
				u.setCarLicense(rs.getString("carLicense"));
				u.setCarType(rs.getString("carType"));
				u.setDriveDistance(rs.getString("driveDistance"));
				u.setCustomers(rs.getInt("customers"));
			}else {
				u.setBalance(0.0);
				u.setCarLicense("unkonw");
				u.setCarType("unknow");
				u.setDriveDistance("0.0");
			}
			
			u.setUserType(userType);
			u.setAge(rs.getInt("age"));
			u.setPassword(rs.getString("password"));
			u.setAccount(rs.getString("account"));
			u.setUserName(rs.getString("name"));
			return u;
		}
		return null;
	}
	
	
	//根据账号和密码来得到用户信息
	public static Message searchUser(String account,String password) throws SQLException{
		String sql="select * from "+tableName_user+" where account="+account+" and password='"+password+"';";
		System.out.println(sql);
		ResultSet rs=statement.executeQuery(sql);
		while(rs.next()) {
			Message u = new Message(0, "", "");
			int userType=rs.getInt("userType");
			if(userType==Message.USERTYPE_driver) {
				u.setBalance(rs.getDouble("balance"));
				u.setCarLicense(rs.getString("carLicense"));
				u.setCarType(rs.getString("carType"));
				u.setDriveDistance(rs.getString("driveDistance"));
				u.setCustomers(rs.getInt("customers"));
			}else {
				u.setBalance(0.0);
				u.setCarLicense("unkonw");
				u.setCarType("unknow");
				u.setDriveDistance("0.0");
			}
			u.setUserType(userType);
			u.setAge(rs.getInt("age"));
			u.setPassword(rs.getString("password"));
			u.setAccount(rs.getString("account"));
			u.setUserName(rs.getString("name"));
			return u;
		}
		return null;
	}
	
	//注册账号
	public static boolean insertUser(String account,String password) throws SQLException{
		String sql="insert into "+tableName_user+" (account,password) values("+account+",'"+password+"');";
		System.out.println(sql);
		return statement.execute(sql);
		
	}
	private int age;			//年龄
	private double balance;			//余额
	private String carLicense;  //车牌号
	private String carType;  //车型
	private String driveDistance;	//行驶里程
	private String customers;	//载客数量
	private String location;	//位置
	
	//更新司机账号信息
	public static boolean updateUser(String account,int age,String carLicense,String carType) throws SQLException{
		String sql="update "+tableName_user+" set age="+age+",carLicense='"+carLicense+"',carType='"+carType+"' where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	
	//更新乘客账号信息
	public static boolean updateUser(String account,int age,double balance) throws SQLException{
		String sql="update "+tableName_user+" set age="+age+",balance="+balance+" where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	
	//更新未知账号用户类别
	public static boolean updateUser_userType(String account,int userType) throws SQLException{
		String sql="update "+tableName_user+" set userType="+userType+" where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
		
	//更新age
	public static boolean updateUser_age(String account,int age) throws SQLException{
		String sql="update "+tableName_user+" set age="+age+" where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	//更新name
	public static boolean updateUser_name(String account,String name) throws SQLException{
		String sql="update "+tableName_user+" set name='"+name+"' where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	//更新carLicense
	public static boolean updateUser_carLicense(String account,String carLicense) throws SQLException{
		String sql="update "+tableName_user+" set carLicense='"+carLicense+"' where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	//更新carType
	public static boolean updateUser_carType(String account,String carType) throws SQLException{
		String sql="update "+tableName_user+" set carType='"+carType+"' where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	//更新driveDistance
	public static boolean updateUser_driveDistance(String account,double driveDistance) throws SQLException{
		String sql="update "+tableName_user+" set driveDistance="+driveDistance+" where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	//更新balance
	public static boolean updateUser_balance(String account,double balance) throws SQLException{
		String sql="update "+tableName_user+" set balance="+balance+" where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	//更新customers
	public static boolean updateUser_customers(String account,int customers) throws SQLException{
		String sql="update "+tableName_user+" set customers=customers+"+customers+" where account="+account;
		System.out.println(sql);
		statement.executeUpdate(sql);
		return true;
	}
	
	//保存载客位置以及时间
	public static boolean saveRecords(String account,String longitude,String latitude) throws SQLException{
		String sql="insert into "+tableName_records+" (account,longitude,latitude,timestamp) values("+account+",'"+longitude+"','"+latitude+"',now());";
		System.out.println(sql);
		return statement.execute(sql);
	}
	



            
	public static void disconnectFromDB() throws SQLException {
		if ( connectToDB ){      
			try{
			statement.close();
			connection.close();
			}finally{                                            
				connectToDB = false;              
			}                             
		} 
   }           

	
}
