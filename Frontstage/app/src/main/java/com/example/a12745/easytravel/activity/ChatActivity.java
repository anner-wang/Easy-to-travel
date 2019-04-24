package com.example.a12745.easytravel.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.a12745.easytravel.R;
import com.example.a12745.easytravel.chatinterface.Msg;
import com.example.a12745.easytravel.chatinterface.MsgAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<Msg>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initMsgs(); // 初始化消息数据
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容
                }
            }
        });
    }

    private void initMsgs() {
        Msg msg0 = new Msg("欢迎咨询在线客服，本次为您服务的是客服小白", Msg.TYPE_RECEIVED);
        msgList.add(msg0);
        Msg msg1 = new Msg("您好，很高兴为您服务", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("请问有什么可以帮助您的吗？", Msg.TYPE_RECEIVED);
        msgList.add(msg2);
    }

}

