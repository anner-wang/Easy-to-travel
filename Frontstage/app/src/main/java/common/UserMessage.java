package common;

import java.io.Serializable;

public class UserMessage implements Serializable {
	public static final long serialVersionUID = 1L;
	
	
	//客户端发送请求类别,如果成功，则返回的type值不变,wrong除外
	public static final int TYPE_wrong=0;   //服务端出错
	public static final int TYPE_getUserInfor=4;	//客户端向服务端请求用户信息
	public static final int TYPE_login=5;			//客户端登入
	public static final int TYPE_signup=6;			//客户端注册
	public static final int TYPE_update=7;			//客户端更新用户信息
	
	//用户类别
	public static final int USERTYPE_driver=1;		//代表司机
	public static final int USERTYPE_customer=2;	//代表乘客
	public static final int USERTYPE_unkonw=3;		//代表未知身份
	
	//登入操作错误时的返回值
	public static final int lOGIN_wrongPassword=-1;		//密码错误
	public static final int lOGIN_noUser=-2;		//不存在该用户错误
	
	//注册操作错误时的返回值
	public static final int SIGNUP_alreadyHasThisUser=-1;		//已经存在该用户

	
	
	public int type=TYPE_wrong;        //操作类型
	private int userType = USERTYPE_unkonw;	//账户类型
	private String account = "null";	//账号
	private String userName;	//用户名字
	private String password = "null";	//用户密码
	private int age;			//年龄
	private double balance;			//余额
	private String carLicense;  //车牌号
	private String carType;  //车型
	private String driveDistance;	//行驶里程
	private String customers;	//载客数量
	private String location;	//位置
	
	
	
	//登入/注册/获取用户信息
	public UserMessage(int type, String account, String password) {
		super();
		this.type = type;
		this.account = account;
		this.password = password;
	}



	//更新司机信息时调用
	public UserMessage(int type, String account, String userName, String password, int age, String carLicense,
			String carType) {
		super();
		this.type = type;
		this.account = account;
		this.userName = userName;
		this.password = password;
		this.age = age;
		this.carLicense = carLicense;
		this.carType = carType;
	}
	
	//更新乘客信息时调用
	public UserMessage(int type, String account, String userName, String password, int age) {
		super();
		this.type = type;
		this.account = account;
		this.userName = userName;
		this.password = password;
		this.age = age;
	}
	
	
	public int getUserType() {
		return userType;
	}


	public void setUserType(int userType) {
		this.userType = userType;
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public double getBalance() {
		return balance;
	}


	public void setBalance(double balance) {
		this.balance = balance;
	}


	public String getCarLicense() {
		return carLicense;
	}


	public void setCarLicense(String carLicense) {
		this.carLicense = carLicense;
	}


	public String getCarType() {
		return carType;
	}


	public void setCarType(String carType) {
		this.carType = carType;
	}


	public String getDriveDistance() {
		return driveDistance;
	}


	public void setDriveDistance(String driveDistance) {
		this.driveDistance = driveDistance;
	}


	public String getCustomers() {
		return customers;
	}


	public void setCustomers(String customers) {
		this.customers = customers;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}
	

}
