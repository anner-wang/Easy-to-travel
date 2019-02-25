package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import Util.Message;
import Util.UserReturned;

public class TaxiEasy extends HttpServlet{
	 
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException
	  {
	      // 执行必需的初始化
	      DataProcessing.Init();
	  }
	 
	  public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	  {
	      // 设置响应内容类型
	      response.setContentType("text/html;charset=UTF-8");
	      
	      
	      //处理中文
	      //String account =new String(request.getParameter("account").getBytes("ISO8859-1"),"UTF-8");
	      int type=-1;
	      try {
	    	  type=Integer.valueOf(new String(request.getParameter("type")));
	    	  switch(type){
		      case Message.TYPE_login:
		    	  login(response,request);
		    	  break;
		      case Message.TYPE_signup:
		    	  signup(response, request);
		    	  break;
		      case Message.TYPE_update:
		    	  update(response, request);
		    	  break;
		      case Message.TYPE_getUserInfor:
		    	  getUserInfo(response, request);
		    	  break;
		      case Message.TYPE_saveRecord:
		    	  saveRecord(response, request);
		    	  break;
		      default:
		    	  wrongRequest(response, request);
		    	  break;
		      
		    	
		    	  }
	      }catch(NullPointerException e) {
	    	  System.out.println("客户端发送的http请求错误！");
	    	  PrintWriter out = response.getWriter();
	    	  e.printStackTrace(out);
	    	  wrongRequest(response, request);
	      } catch (SQLException e) {
	    	  System.out.println("数据库异常！");
	    	  e.printStackTrace();
	    	  PrintWriter out = response.getWriter();
	    	  e.printStackTrace(out);
	    	  wrongRequest(response, request);
		}
		
	      
	     
	      
	  }
	  
	  public void destroy()
	  {
	      // 什么也不做
	  }
	  
	  public void wrongRequest(HttpServletResponse response,HttpServletRequest request) {
			try {
				UserReturned returned=new UserReturned();
				returned.type=Message.TYPE_wrong;
				Gson gson =new Gson();
				String json = gson.toJson(returned);
				PrintWriter out = response.getWriter();
				out.println(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		  
	  }
	  
	  /**
	   * 注册
	   * @param response
	   * @param request
	   * @throws SQLException
	   * @throws IOException
	   */
	  public void signup(HttpServletResponse response,HttpServletRequest request) throws SQLException, IOException {
		  String account =new String(request.getParameter("account"));
		    String password =new String(request.getParameter("password"));
		    Message user=DataProcessing.searchUser(account);
		    UserReturned returned=new UserReturned();
		    if(user!=null) {
		    	//已经存在该账号
		    	returned.type=Message.SIGNUP_alreadyHasThisUser;
		    }else {
		    	DataProcessing.insertUser(account, password);
		    	user=DataProcessing.searchUser(account);
		    	returned.Message2Returned(user);
		    	returned.type=Message.TYPE_signup;
		    }

		  //对象转json
			Gson gson =new Gson();
			String json = gson.toJson(returned);
			PrintWriter out = response.getWriter();
			out.println(json);
		      
		  }
	  
	  /**
	   * 登入
	   * @param response
	   * @param request
	   * @throws SQLException
	   * @throws IOException
	   */
	  public void login(HttpServletResponse response,HttpServletRequest request) throws SQLException, IOException {
		  String account =new String(request.getParameter("account"));
		    String password =new String(request.getParameter("password"));
		    Message user=DataProcessing.searchUser(account);
		    UserReturned returned=new UserReturned();
		    if(user!=null) {
		    	//搜索到了User
		    	if(user.getPassword().equals(password)) {
		    		//密码正确
		    		returned.Message2Returned(user);
		    		returned.type=Message.TYPE_login;
		    	}else {
		    		//密码错误
		    		returned.type=Message.lOGIN_wrongPassword;
		    		
		    	}
		    }else {
		    	//未搜索到User
		    	returned.type=Message.lOGIN_noUser;
		    }
		  //对象转json
			Gson gson =new Gson();
			String json = gson.toJson(returned);
			PrintWriter out = response.getWriter();
			out.println(json);
	      
	  }
	  
	  /**
	   * 更新数据
	   * @param response
	   * @param request
	   * @throws SQLException
	   * @throws IOException
	   */
	  public void update(HttpServletResponse response,HttpServletRequest request) throws SQLException, IOException {
		  String account =new String(request.getParameter("account"));
		    String password =new String(request.getParameter("password"));
		    Message user=DataProcessing.searchUser(account,password);
		    UserReturned returned=new UserReturned();
		    if(user==null) {
		    	//密码或者或者账号出错，未查询到相关账号
		    	returned.type=Message.TYPE_wrong;
		    }else {
		    	//密码账号匹配，执行更新操作
		    	
		    	String carLicense=request.getParameter("carLicense");
		    	String carType=request.getParameter("carType");
		    	String userType=request.getParameter("userType");
		    	String age=request.getParameter("age");
		    	String balance=request.getParameter("balance");
		    	String driveDistance=request.getParameter("driveDistance");
		    	String customers=request.getParameter("customers");
		    	String name=request.getParameter("name");
		    	if(carLicense!=null) {
		    		DataProcessing.updateUser_carLicense(account, new String(carLicense.getBytes("ISO8859-1"),"UTF-8"));
		    	}
		    	if(carType!=null) {
		    		DataProcessing.updateUser_carType(account,new String(carType.getBytes("ISO8859-1"),"UTF-8"));
		    	}
		    	if(name!=null) {
		    		DataProcessing.updateUser_name(account, new String(name.getBytes("ISO8859-1"),"UTF-8"));
		    	}
		    	if(age!=null) {
		    		DataProcessing.updateUser_age(account, Integer.valueOf(age));
		    	}
		    	if(balance!=null) {
		    		DataProcessing.updateUser_balance(account, Double.valueOf(balance));
		    	}
		    	if(driveDistance!=null) {
		    		DataProcessing.updateUser_driveDistance(account, Double.valueOf(driveDistance));
		    	}
		    	if(customers!=null) {
		    		DataProcessing.updateUser_customers(account, Integer.valueOf(customers));
		    	}
		    	if(userType!=null) {
		    		DataProcessing.updateUser_userType(account, Integer.valueOf(userType));
		    	}
		    	
		    	user=DataProcessing.searchUser(account);
		    	returned.Message2Returned(user);
		    	returned.type=Message.TYPE_update;
		    }
		   
		    
		  //对象转json
			Gson gson =new Gson();
			String json = gson.toJson(returned);
			PrintWriter out = response.getWriter();
			out.println(json);
		      
		  }
	  
	  /**
	   * 获取最新数据
	   * @param response
	   * @param request
	   * @throws SQLException
	   * @throws IOException
	   */
	  public void getUserInfo(HttpServletResponse response,HttpServletRequest request) throws SQLException, IOException {
		  String account =new String(request.getParameter("account"));
		    String password =new String(request.getParameter("password"));
		    Message user=DataProcessing.searchUser(account,password);
		    UserReturned returned=new UserReturned();
		    
		    if(user==null) {
		    	//密码或者或者账号出错，未查询到相关账号
		    	returned.type=Message.TYPE_wrong;
		    }else {
		    	returned.Message2Returned(user);
		    	returned.type=Message.TYPE_getUserInfor;
		    }
		  //对象转json
			Gson gson =new Gson();
			String json = gson.toJson(returned);
			PrintWriter out = response.getWriter();
			out.println(json);
		  }
	  
	  public void saveRecord(HttpServletResponse response,HttpServletRequest request) throws SQLException, IOException {
		  String account =new String(request.getParameter("account"));
		  String password =new String(request.getParameter("password"));
		  Message user=DataProcessing.searchUser(account,password);
		  UserReturned returned=new UserReturned();
		  if(user!=null) {
			  String latitude=request.getParameter("latitude");
			  String longitude=request.getParameter("longitude");
			  DataProcessing.saveRecords(account, longitude, latitude);
			  String sc=request.getParameter("customers");
			  int customers=0;
			  if(sc!=null) {
				  customers=Integer.valueOf(sc);
				  DataProcessing.updateUser_customers(account, customers);
			  }
			  user=DataProcessing.searchUser(account,password);
			  returned.Message2Returned(user);
			  returned.type=Message.TYPE_saveRecord;
		  }else {
			  returned.type=Message.TYPE_wrong;
		  }
		//对象转json
			Gson gson =new Gson();
			String json = gson.toJson(returned);
			PrintWriter out = response.getWriter();
			out.println(json);
		  
	  }
}
