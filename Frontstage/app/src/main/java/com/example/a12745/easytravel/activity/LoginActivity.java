package com.example.a12745.easytravel.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12745.easytravel.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Util.HttpUtil;
import common.ConstValue;
import common.UserMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import static common.ConstValue.BAIDU_READ_PHONE_STATE;
import static common.ConstValue.LOCATIONGPS;
import static common.ConstValue.PRIVATE_CODE;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * 登陆界面
 * @author XuCong
 */
public class LoginActivity extends AppCompatActivity {


    private EditText editId,editPassenger;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private TextView textForgetPassword,textRegister;
    private final int mRequestCode = 100;//权限请求码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        editId = (EditText) findViewById(R.id.login_id);
        editPassenger = (EditText) findViewById(R.id.login_password);
        buttonLogin = (Button) findViewById(R.id.login_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account=editId.getText().toString();
                String pwd=editPassenger.getText().toString();
                tryLogin(account,pwd);
            }
        });
        textForgetPassword = (TextView) findViewById(R.id.forgetPassword);
        textRegister = (TextView) findViewById(R.id.register);
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        showGPSContacts();

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstValue.noInternet:
                    closeProgressDialog();
                    Toast.makeText(LoginActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                    break;
                case UserMessage.TYPE_login:
                    UserMessage user = (UserMessage) msg.getData().getSerializable(ConstValue.bundle_messageUser);
                    Intent intent;
                    if(user.getUserType()==UserMessage.USERTYPE_driver){
                        intent = new Intent(LoginActivity.this,DriverActivity.class);
                    }else if(user.getUserType()==UserMessage.USERTYPE_customer){
                        intent = new Intent(LoginActivity.this,PassengerActivity.class);
                    }else{
                        intent = new Intent(LoginActivity.this,SetUserTypeActivity.class);
                    }
                    saveAccountAndPwd(editId.getText().toString(),editPassenger.getText().toString());
                    intent.putExtra(ConstValue.bundle_messageUser,user);
                    startActivity(intent);
                    finish();
                    closeProgressDialog();
                    break;
                case UserMessage.TYPE_wrong:
                    closeProgressDialog();
                    Toast.makeText(LoginActivity.this, "服务器或者输入错误!", Toast.LENGTH_SHORT).show();
                    break;
                case UserMessage.lOGIN_noUser:
                    closeProgressDialog();
                    Toast.makeText(LoginActivity.this, "不存在该用户", Toast.LENGTH_SHORT).show();
                    break;
                case UserMessage.lOGIN_wrongPassword:
                    closeProgressDialog();
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
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

    private void saveAccountAndPwd(String accont,String pwd){
        SharedPreferences.Editor editor = getSharedPreferences(ConstValue.spName,MODE_PRIVATE).edit();
        editor.putString(ConstValue.spAccount,accont);
        editor.putString(ConstValue.spPwd,pwd);
        editor.apply();
    }




    /**
     * 检测GPS、位置权限是否开启
     */
    private void showGPSContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //需要检查的三个权限
            List<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < LOCATIONGPS.length; i++) {
                if (ContextCompat.checkSelfPermission(this, LOCATIONGPS[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(LOCATIONGPS[i]);//添加还未授予的权限
                }
            }
            //申请权限
            if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
                ActivityCompat.requestPermissions(this, LOCATIONGPS, mRequestCode);
            }
        }
    }



}
