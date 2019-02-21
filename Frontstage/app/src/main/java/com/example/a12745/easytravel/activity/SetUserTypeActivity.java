package com.example.a12745.easytravel.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class SetUserTypeActivity extends AppCompatActivity {

    Button buttonDriver,buttonPassenger;
    UserMessage user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_type);
        Intent intent = getIntent();
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        user = (UserMessage) intent.getSerializableExtra(ConstValue.bundle_messageUser);

        buttonDriver = (Button) findViewById(R.id.set_user_driver);
        buttonPassenger = (Button) findViewById(R.id.set_user_passenger);
        buttonDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account=getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spAccount,"");
                String pwd=getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spPwd,"");
                tryUpdateUserType(account,pwd,UserMessage.USERTYPE_driver);
            }
        });
        buttonPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account=getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spAccount,"");
                String pwd=getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spPwd,"");
                tryUpdateUserType(account,pwd,UserMessage.USERTYPE_customer);
            }
        });
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstValue.noInternet:
                    Toast.makeText(SetUserTypeActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                    break;
                case UserMessage.TYPE_update:
                    UserMessage user = (UserMessage) msg.getData().getSerializable(ConstValue.bundle_messageUser);
                    Intent intent;
                    if(user.getUserType()==UserMessage.USERTYPE_driver){
                        intent = new Intent(SetUserTypeActivity.this,InitDriverInfoActivity.class);
                    }else if(user.getUserType()==UserMessage.USERTYPE_customer){
                        intent = new Intent(SetUserTypeActivity.this,PassengerActivity.class);
                    }else{
                        return;
                    }
                    intent.putExtra(ConstValue.bundle_messageUser,user);
                    startActivity(intent);
                    finish();
                    break;
                case UserMessage.TYPE_wrong:
                    Toast.makeText(SetUserTypeActivity.this, "服务器或者输入错误!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void tryUpdateUserType(String account,String pwd,int userType){
        String address=ConstValue.setUserTypeURL(account,pwd,userType);
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
}
