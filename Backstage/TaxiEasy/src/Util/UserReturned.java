package Util;

public class UserReturned {
	public int type;        //操作类型
	private int userType;	//账户类型
	private String userName;	//用户名字
	private int age;			//年龄
	private double balance;			//余额
	private String carLicense;  //车牌号
	private String carType;  //车型
	private String driveDistance;	//行驶里程
	private int customers;	//载客数量
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public int getCustomers() {
		return customers;
	}
	public void setCustomers(int customers) {
		this.customers = customers;
	}
	
	public void Message2Returned(Message user) {
		this.type=user.type;
		this.userType=user.getUserType();
		if(this.userType==Message.USERTYPE_driver) {
			this.age=user.getAge();
			this.balance=user.getBalance();
			this.carLicense=user.getCarLicense();
			this.carType=user.getCarType();
			this.customers=user.getCustomers();
			this.driveDistance=user.getDriveDistance();
		}
		this.userName=user.getUserName();
		
	}
	
}
