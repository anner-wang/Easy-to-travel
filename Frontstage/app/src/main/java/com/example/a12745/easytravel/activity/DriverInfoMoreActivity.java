package com.example.a12745.easytravel.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a12745.easytravel.R;

import common.ConstValue;
import common.UserMessage;

public class DriverInfoMoreActivity extends AppCompatActivity {
    Button btnBack;
    private TextView age,carLicense,carType,customers_all,customers_today,drivedistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info_more);

        btnBack=findViewById(R.id.driver_self_more_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        age=findViewById(R.id.driver_self_more_age);
        carType=findViewById(R.id.driver_self_more_carType);
        carLicense=findViewById(R.id.driver_self_more_carLicense);
        customers_all=findViewById(R.id.driver_self_more_customers_all);
        customers_today=findViewById(R.id.driver_self_more_customers_today);



        Intent intent = getIntent();
        UserMessage user = (UserMessage) intent.getSerializableExtra(ConstValue.bundle_messageUser);
        if(user!=null){
            age.setText(user.getAge()+"");
            carType.setText(user.getCarType());
            carLicense.setText(user.getCarLicense());
            customers_today.setText(user.getCustomers());
            customers_all.setText(user.getCustomers());
        }

    }
}
