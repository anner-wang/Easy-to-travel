package common;

import android.Manifest;
import android.os.Environment;

import java.io.File;

public class ConstValue {
	public static final String bundle_messageUser="MessageUser";

	public static final int serverPortUser=3001;
	public static final int noInternet=1111;
	//public static final String serverIp="http://www.anner.wang:54343/Demo/HelloWorld?";
	public static final String serverIp="http://www.anner.wang:34443/user/";
	public static final String spName="spName";
	public static final String spAccount="spAccount";
	public static final String spPwd="spPwd";
	public static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
	public static final int PRIVATE_CODE = 1315;//开启GPS权限
	public static final int mapScale=14;		//地图缩放级别
	public static final int mapLocationIternal=2000; //定时刷新GPS 2000ms
	public static final int time_refreshHotMap=1000*100;	//定时刷新hotmap
	public static final int SEARCHRADIUS = 5000;	//搜索半径




	public static final String[] LOCATIONGPS = new String[]{
	        Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET,

	};




	public static String SAVEUSERPATH = Environment.getDataDirectory().getAbsolutePath()+ File.separator + "user.bin";
	public static String getLoginURL(String account, String password)
	{
		return serverIp+"login?"+"account="+account+"&password="+password;
	}
    public static String getRegistURL(String account, String password)
    {
        return serverIp+"signup?"+"account="+account+"&password="+password;
    }
	public static String setUserTypeURL(String account, String password, int userType)
	{
		return serverIp+"update?"+"account="+account+"&password="+password+"&userType="+userType;
	}
	public static String setDriverInfoURL(String account,String pwd,String carLicense,String carType,String name,int age)
	{
		return serverIp+"update?"+"account="+account+"&password="+pwd+"&carLicense="+carLicense+"&carType="+carType+"&username="+name+"&age="+age;
	}
	public static String getHotMapUrl(double longitude,double latitude){
		return "http://www.anner.wang:34443/map/near?longitude="+longitude+"&latitude="+latitude+"&tdsourcetag=s_pctim_aiomsg";
	}
	public static String getUserInfoUrl(String account,String pwd){
		return serverIp+"get?"+"&account="+account+"&password="+pwd;
	}
	public static String getDriverRouteUrl(double longitude,double latitude){
		return "http://www.anner.wang:34443/path/simple?longitude="+longitude+"&latitude="+latitude;
	}
	public static String getCommendUrl(double longitude,double latitude){
		return "http://www.anner.wang:34443/point/simple?longitude=" + longitude + "&latitude=" + latitude;
	}



}
