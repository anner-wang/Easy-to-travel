package com.example.demo.bean;

public class User {
    //客户端发送请求类别,如果成功，则返回的type值不变,wrong除外
    public static final int TYPE_wrong=0;   //出错
    public static final int TYPE_saveRecord=3;			//客户端上传位置记录
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
    public static final int lOGIN_noUser=-2;		//不存在该用户

    //注册操作错误时的返回值
    public static final int SIGNUP_alreadyHasThisUser=-1;		//已经存在该用户

    public int type;        //操作类型
    private int userType;	//账户类型
    private Long account;
    private String name;	//用户名字
    private String passWord;
    private int age;			//年龄
    private double balance;			//余额
    private String carLicense;  //车牌号
    private String carType;  //车型
    private double driveDistance;	//行驶里程
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

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public double getDriveDistance() {
        return driveDistance;
    }
    public void setDriveDistance(double driveDistance) {
        this.driveDistance = driveDistance;
    }
    public int getCustomers() {
        return customers;
    }
    public void setCustomers(int customers) {
        this.customers = customers;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
