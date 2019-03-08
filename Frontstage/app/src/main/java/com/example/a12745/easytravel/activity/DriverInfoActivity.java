package com.example.a12745.easytravel.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12745.easytravel.R;
import com.google.gson.Gson;

import java.io.IOException;

import Util.ActivityCollector;
import Util.HttpUtil;
import common.ConstValue;
import common.UserMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DriverInfoActivity extends AppCompatActivity {

    private RelativeLayout rlBtnMore;
    private Button btnBack,btnlogout;
    private UserMessage user;
    private ProgressDialog progressDialog;
    private TextView tv_name,tv_account;
    private long firstTime;//再按一次退出程序


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_self);
        rlBtnMore=findViewById(R.id.driver_self_btn_more);
        rlBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null){
                    Intent intent = new Intent(DriverInfoActivity.this,DriverInfoMoreActivity.class);
                    intent.putExtra(ConstValue.bundle_messageUser,user);
                    startActivity(intent);
                }

            }
        });
        btnBack=findViewById(R.id.driver_self_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnlogout=findViewById(R.id.driver_self_logout);
        btnlogout = findViewById(R.id.driver_self_logout);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverInfoActivity.this,LoginActivity.class);
                saveAccountAndPwd("","");
                if (ActivityCollector.isContains("driverActivity")) {
                    ActivityCollector.finishActivity("driverActivity");
                }
                startActivity(intent);
                finish();
            }
        });


        tv_name=findViewById(R.id.driver_self_name);
        tv_account=findViewById(R.id.driver_self_account);
        tv_account.setText(getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spAccount,""));
        getUser();
    }







    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstValue.noInternet:
                    closeProgressDialog();
                    Toast.makeText(DriverInfoActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                    break;
                case UserMessage.TYPE_getUserInfor:
                    user = (UserMessage) msg.getData().getSerializable(ConstValue.bundle_messageUser);
                    Log.e("TAG", user.getAccount()+""+user.getPassword());
                    tv_name.setText(user.getName());
                    closeProgressDialog();
                    break;
                case UserMessage.TYPE_wrong:
                    closeProgressDialog();
                    Toast.makeText(DriverInfoActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void getUser(){
        showProgressDialog();
        SharedPreferences sp = getSharedPreferences(ConstValue.spName,MODE_PRIVATE);
        String account = sp.getString(ConstValue.spAccount,"");
        String pwd = sp.getString(ConstValue.spPwd,"");
        String address=ConstValue.getUserInfoUrl(account,pwd);
        System.out.println(address);
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
    private void saveAccountAndPwd(String accont,String pwd){
        SharedPreferences.Editor editor = getSharedPreferences(ConstValue.spName,MODE_PRIVATE).edit();
        editor.putString(ConstValue.spAccount,accont);
        editor.putString(ConstValue.spPwd,pwd);
        editor.apply();

    }



}
