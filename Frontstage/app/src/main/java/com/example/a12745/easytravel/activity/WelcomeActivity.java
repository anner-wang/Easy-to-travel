package com.example.a12745.easytravel.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.a12745.easytravel.R;
import com.google.gson.Gson;

import Util.HttpUtil;
import common.ConstValue;
import common.UserMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
//欢迎界面
public class WelcomeActivity extends AppCompatActivity {

    private UserMessage user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }


        Timer timer = new Timer();
        user = null;
        //user = getUser();
        //根据登录的状态判断欢迎界面结束后跳转到哪个界面
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
               String account=getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spAccount,"");
               String pwd=getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spPwd,"");
               if(!account.equals("")&&!pwd.equals("")){
                   tryLogin(account,pwd);
               }else{
                   //为保存账号密码，跳转登入界面
                   Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                   startActivity(intent);
                   finish();
               }
            }
        };
        //使用timer.schedule（）方法调用timerTask，定时3秒后执行run
        timer.schedule(timerTask, 1500);

        }


    public void tryLogin(String account,String pwd){
        String address=ConstValue.getLoginURL(account,pwd,UserMessage.TYPE_login);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.what=ConstValue.noInternet;
                handler.sendMessage(msg);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                System.out.println(responseText);
                UserMessage user = new Gson().fromJson(responseText,UserMessage.class);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstValue.bundle_messageUser,user);
                msg.setData(bundle);
                msg.what=user.type;
                handler.sendMessage(msg);
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstValue.noInternet:
                    Toast.makeText(WelcomeActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent1);
                    finish();
                    break;
                case UserMessage.TYPE_login:
                    UserMessage user = (UserMessage) msg.getData().getSerializable(ConstValue.bundle_messageUser);
                    Intent intent;
                    if(user.getUserType()==UserMessage.USERTYPE_driver){
                        intent = new Intent(WelcomeActivity.this,DriverActivity.class);
                    }else if(user.getUserType()==UserMessage.USERTYPE_customer){
                        intent = new Intent(WelcomeActivity.this,PassengerActivity.class);
                    }else{
                        intent = new Intent(WelcomeActivity.this,SetUserTypeActivity.class);
                    }

                    intent.putExtra(ConstValue.bundle_messageUser,user);
                    startActivity(intent);
                    finish();
                    break;
                case UserMessage.TYPE_wrong:
                    Toast.makeText(WelcomeActivity.this, "服务器或者输入错误!", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
                case UserMessage.lOGIN_noUser:
                    Toast.makeText(WelcomeActivity.this, "不存在该用户", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent3);
                    finish();
                    break;
                case UserMessage.lOGIN_wrongPassword:
                    Toast.makeText(WelcomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    Intent intent4 = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent4);
                    finish();
                    break;

            }
        }
    };



}
