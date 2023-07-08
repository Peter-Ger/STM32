package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {



    Button button = null;

    EditText user;  //创建账号
    EditText passwords;  //创建密码



    @SuppressLint({"HandlerLeak", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // ui_init();
        user =  findViewById(R.id.user);
        passwords = findViewById(R.id.password);
        button = (Button)findViewById(R.id.button1);//登录
       // View view = View.inflate(MainActivity.this, R.layout.main, null);//inflate：视图填充器；这个为内部类，要使用上下文则为  类.this

        //text_test.setText("40");

        /*
        button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,main.class);
                    startActivity(intent);
                }
            });
        }}

*/




        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)  //给登录按钮设置监听器
            {
                String username = user.getText().toString();
                String password = passwords.getText().toString();
                String u ="123";
                String p ="123";
                if(username.equals(u)&&password.equals(p)) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,main.class);
                    startActivity(intent);
                    //setContentView(view);

                }else
                {Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();}
            }});


    }


}