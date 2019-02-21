package com.example.a12745.easytravel.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

public class InitDriverInfoActivity extends AppCompatActivity {
    private EditText ed_carType,ed_carLicense,ed_age,ed_name;
    private Button btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_driver_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        ed_age=findViewById(R.id.driver_infor_age);
        ed_carLicense=findViewById(R.id.driver_infor_carLience);
        ed_carType=findViewById(R.id.driver_infor_carType);
        ed_name=findViewById(R.id.driver_infor_name);
        btn_update=findViewById(R.id.driver_infor_confirm);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(ed_age.getText())||TextUtils.isEmpty(ed_carLicense.getText())||TextUtils.isEmpty(ed_carType.getText())||TextUtils.isEmpty(ed_name.getText())){
                    Toast.makeText(InitDriverInfoActivity.this, "请输入完整内容！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String age=ed_age.getText().toString();
                String carLicense=ed_carLicense.getText().toString();
                String carType=ed_carType.getText().toString();
                String name=ed_name.getText().toString();
                String account=getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spAccount,"000");
                String pwd=getSharedPreferences(ConstValue.spName,MODE_PRIVATE).getString(ConstValue.spPwd,"000");
                trySetDriverInfo(account,pwd,carLicense,carType,name,Integer.valueOf(age));
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ConstValue.noInternet:
                    Toast.makeText(InitDriverInfoActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                    break;
                case UserMessage.TYPE_update:
                    UserMessage user = (UserMessage) msg.getData().getSerializable(ConstValue.bundle_messageUser);
                    Intent intent=new Intent(InitDriverInfoActivity.this,DriverActivity.class);
                    intent.putExtra(ConstValue.bundle_messageUser,user);
                    startActivity(intent);
                    finish();
                    break;
                case UserMessage.TYPE_wrong:
                    Toast.makeText(InitDriverInfoActivity.this, "服务器或者输入错误!", Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };

    public void trySetDriverInfo(String account,String pwd,String carLicense,String carType,String name,int age){
        String address=ConstValue.setDriverInfoURL(account,pwd,carLicense,carType,name,age);
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
