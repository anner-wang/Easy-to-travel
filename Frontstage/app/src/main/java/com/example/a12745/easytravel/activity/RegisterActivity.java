package com.example.a12745.easytravel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a12745.easytravel.R;
import com.google.gson.Gson;

import java.io.IOException;

import Util.HttpUtil;
import common.ConstValue;
import common.UserMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 注册的界面
 * @author XuCong
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText editId,editPassword,editConfirmPassword;
    private Button buttonRegister;
    private CheckBox checkBox;
    private UserMessage user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(false);
        editId = (EditText) findViewById(R.id.register_id);
        editPassword = (EditText) findViewById(R.id.register_password);
        editConfirmPassword = (EditText) findViewById(R.id.register_confirmPassword);
        buttonRegister = (Button) findViewById(R.id.register_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editId.length()==0||editPassword.length()==0||editConfirmPassword.length()==0) {
                    Toast.makeText(RegisterActivity.this,"输入不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    if (editPassword.getText().toString().equals(editConfirmPassword.getText().toString())==false)
                        Toast.makeText(RegisterActivity.this,"两次输入的密码不一致！",Toast.LENGTH_SHORT).show();
                    else {
                        tryRegister(editId.getText().toString(), editPassword.getText().toString());
                    }
                }
            }
        });
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()==true) {
                    buttonRegister.setClickable(true);
                } else{
                    Toast.makeText(RegisterActivity.this,"请同意用户协议！",Toast.LENGTH_LONG).show();
                    buttonRegister.setClickable(false);
                }
            }
        });

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UserMessage.SIGNUP_alreadyHasThisUser:
                    Toast.makeText(RegisterActivity.this, "已经存在该账号!", Toast.LENGTH_SHORT).show();
                    break;
                case ConstValue.noInternet:
                    Toast.makeText(RegisterActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                    break;
                case UserMessage.TYPE_signup:
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    saveAccountAndPwd(editId.getText().toString(),editPassword.getText().toString());
                    UserMessage user = (UserMessage) msg.getData().getSerializable(ConstValue.bundle_messageUser);
                    Intent intent;
                    /*if(user.getUserType()==UserMessage.USERTYPE_driver){
                        intent = new Intent(RegisterActivity.this,DriverActivity.class);
                    }else if(user.getUserType()==UserMessage.USERTYPE_customer){
                        intent = new Intent(RegisterActivity.this,PassengerActivity.class);
                    }else{
                        intent=new Intent(RegisterActivity.this,SetUserTypeActivity.class);
                    }*/
                    user.setUserType(UserMessage.USERTYPE_driver);
                    intent = new Intent(RegisterActivity.this,InitDriverInfoActivity.class);

                    intent.putExtra(ConstValue.bundle_messageUser,user);
                    startActivity(intent);
                    finish();
                    break;
                case UserMessage.TYPE_wrong:
                    Toast.makeText(RegisterActivity.this, "服务器或者输入错误!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public void tryRegister(String account,String pwd){
        String address=ConstValue.getRegistURL(account,pwd);
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



}
