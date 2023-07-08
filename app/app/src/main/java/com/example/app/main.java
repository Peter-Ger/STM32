package com.example.app;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
    public class main extends AppCompatActivity {
        private String host = "tcp://a1946HaV1CI.iot-as-mqtt.cn-shanghai.aliyuncs.com.:1883";
        private String userName = "app1&a1946HaV1CI";
        private String passWord = "5f351619bdb313754a9d36dc101ea628b9cb8d7ae377052f946136daae8072b1";
        private String mqtt_id = "a1946HaV1CI.app1|securemode=2,signmethod=hmacsha256,timestamp=1688453121254|";
        private String mqtt_sub_topic = "/a1946HaV1CI/app1/user/huanjingwendu"; //订阅环境温度度话题
        private String mqtt_sub_topic1 = "/a1946HaV1CI/app1/user/huanjingshidu"; //订阅环境湿度话题
        //private String mqtt_sub_topic2 = "/a1946HaV1CI/app1/user/turangshidu";  //订阅土壤湿度话题
        private String mqtt_sub_topic5 = "/a1946HaV1CI/app1/user/fengshan";//订阅风扇开关话题
        private String mqtt_sub_topic6 = "/a1946HaV1CI/app1/user/deng";     //订阅灯开关话题
        private String mqtt_sub_topic7 = "/a1946HaV1CI/app1/user/picture";  //订阅图片话题
        private String mqtt_pub_topic3 = "/a1946HaV1CI/app1/user/fengshan";//发布风扇开关话题
        private String mqtt_pub_topic4 = "/a1946HaV1CI/app1/user/deng";     //发布灯开关话题
        private String mqtt_pub_topic8 = "/a1946HaV1CI/app1/user/shuibeng";//发布水泵话题
        private String mqtt_pub_topic9 = "/a1946HaV1CI/app1/user/ding";    //发布顶话题

        public int feng_flag ;//作为开关风扇的判断
        public int deng_flag ;//作为开关灯的判断
        private ScheduledExecutorService scheduler;
        private MqttClient client;
        private MqttConnectOptions options;
        private Handler handler;
        EditText shuibeng;
        Button button1 = null;
        Button button2 = null;
        Button button_huanjingshidu=null;
        Button button_turangshidu=null;
        Button button_guangzhaoqiangdu=null;
        Button button_huanjingwendu=null;
        private SoundPool soundPool;
        private HashMap<Integer, Integer> soundMap;

        private TextView text_test;
        private TextView text_test1;
        private TextView text_test2;
        private TextView text_test3;

        private BlockingQueue<String> queue;
        private MusicPlayer musicPlayer;
        private String temp;
        private Spinner dengguangfangan;
        private Spinner penggaijiaodu;
        String[] deng_arr ={"请选择光照方案","fangan_1","fangan_2","fangan_3","fangan_4","fangan_5"};
        String[] jiaodu_arr ={"请选择角度值","20","30","40","60","90",};
        //private LineChart huanjingshidu_chart;

        private int lock=1;
        private ImageView imageView;

        private LineChartFunction huanjingshidu_LineChart1;
        private LineChartFunction huanjingwendu_LineChart1;
        private LineChartFunction turangshidu_LineChart1;
        private LineChartFunction guangzhaoqiangdu_LineChart1;
        private int lei=0;






        @SuppressLint({"HandlerLeak", "MissingInflatedId"})
        @Override

        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);//调用mainlayout
            //view =this.getLayoutInflater().inflate(R.layout.main,null);
            //button1 = (Button) findViewById(R.id.button3);
           // button2 = (Button)findViewById(R.id.button);
            //button1 = (Button)findViewById(R.id.button3);//
            button1 = (Button)findViewById(R.id.button4);//水泵
            button2 = (Button) findViewById(R.id.button);//风扇
            button_huanjingshidu=(Button) findViewById(R.id.huanjingshidu_button);
            button_huanjingwendu=(Button) findViewById(R.id.huanjingwendu_button);
            button_guangzhaoqiangdu=(Button) findViewById(R.id.guangzhaoqiangdu_button);
            button_turangshidu=(Button) findViewById(R.id.turangshidu_button);
            text_test =(TextView) findViewById(R.id.textView);//土壤湿度
            text_test1 =(TextView) findViewById(R.id.textView6);//空气湿度
            text_test2 =(TextView) findViewById(R.id.textView5);//环境温度
            text_test3 =(TextView) findViewById(R.id.textView8);
            imageView=(ImageView) findViewById(R.id.imageView2);
            dengguangfangan=(Spinner)findViewById(R.id.spinner2);
            penggaijiaodu=(Spinner)findViewById(R.id.spinner3);
            shuibeng =  findViewById(R.id.shuibengshijian);
            LayoutInflater factory = LayoutInflater.from(main.this);//获取dialog布局文件获取View
            final View huanjingshiduView = factory.inflate(R.layout.huanjingshidu, null);// 通过textEntryView来获取控件
            final View huanjingwenduView = factory.inflate(R.layout.huanjingwendu, null);
            final View turangshiduView = factory.inflate(R.layout.turangshidu, null);
            final View guangzhaoqiangduView = factory.inflate(R.layout.guangzhaoqiangdu, null);
            final LineChart huanjingshidu_chart = huanjingshiduView.findViewById(R.id.huanjingshidu_line);
            final LineChart turangshidu_chart = huanjingwenduView.findViewById(R.id.huanjingshidu_line);
            final LineChart huanjingwendu_chart =turangshiduView.findViewById(R.id.huanjingshidu_line);
            final LineChart guangzhaoqiangdu_chart = guangzhaoqiangduView.findViewById(R.id.huanjingshidu_line);
            huanjingshidu_LineChart1 = new LineChartFunction(huanjingshidu_chart, "环境湿度", Color.GREEN);
            huanjingwendu_LineChart1 = new LineChartFunction(huanjingwendu_chart, "环境温度", Color.BLUE);
            turangshidu_LineChart1 = new LineChartFunction(turangshidu_chart, "土壤湿度", Color.RED);
            guangzhaoqiangdu_LineChart1 = new LineChartFunction(guangzhaoqiangdu_chart, "光照强度", Color.GRAY);
            huanjingshidu_LineChart1.setYAxis(100, 0, 1);//最大值，最小值，中间刻度值的数量
            huanjingwendu_LineChart1.setYAxis(100, 0, 1);
            turangshidu_LineChart1.setYAxis(100, 0, 1);
            guangzhaoqiangdu_LineChart1.setYAxis(100, 0, 1);





            ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,deng_arr);
            ArrayAdapter<String> adapter1=new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,jiaodu_arr);
            dengguangfangan.setAdapter(adapter);
            penggaijiaodu.setAdapter(adapter1);
            dengguangfangan.setOnItemSelectedListener(new MyOnItemSelectedListener());
            penggaijiaodu.setOnItemSelectedListener(new MyOnItemSelectedListener1());






            musicPlayer = new MusicPlayer();
            musicPlayer.startPlaying();


            String[][] arrayName = new String[3][100];
            int[] count_array = new int[100];
            for(int i=0;i<100;i++){
                count_array[i]=0;
            }





            //musicPlayer.playMusic(setDataSourceuri);
            File file = new File(getFilesDir(), "example.txt");

            button_huanjingshidu.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    Dialog dialog = new Dialog(main.this);// 设置布局
                    dialog.setContentView(R.layout.huanjingshidu);// 设置弹窗标题
                    dialog.setTitle("环境湿度");// 显示弹窗
                    dialog.show();

                }});
            button_huanjingwendu.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {

                    Dialog dialog = new Dialog(main.this);// 设置布局
                    dialog.setContentView(R.layout.activity_huanjingwendu);// 设置弹窗标题
                    dialog.setTitle("环境温度");// 显示弹窗
                    dialog.show();

                }});
            button_turangshidu.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    Dialog dialog = new Dialog(main.this);// 设置布局
                    dialog.setContentView(R.layout.turangshidu);// 设置弹窗标题
                    dialog.setTitle("土壤湿度");// 显示弹窗
                    dialog.show();

                }});
            button_guangzhaoqiangdu.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    Dialog dialog = new Dialog(main.this);// 设置布局
                    dialog.setContentView(R.layout.guangzhaoqiangdu);// 设置弹窗标题
                    dialog.setTitle("光照强度");// 显示弹窗
                    dialog.show();

                }});




            //text_test.setText("40");
            button1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    String shuibeng_time= shuibeng.getText().toString();
                    boolean shuibeng_ceshi = shuibeng_time.matches("\\d+");

                    //Log.v("ssssss", String.valueOf(shuibeng_ceshi));
                    if(shuibeng_ceshi){
                        //Log.v("ssssss", "55555");
                        String json_shuibeng = "{\"id\":\"app\",\"version\":\"1.0\",\"params\":\"{\"shui_on\":\"" + shuibeng_time + "\"}\"}";
                        //Log.v("ssssss", "6666");
                        publishmessageplus(mqtt_pub_topic8, json_shuibeng);
                        //Log.v("aaa","aaaa");
                    }
                    else {
                        //Toast.makeText(main.this,"输入时间有误" ,Toast.LENGTH_SHORT).show();//弹窗功能
                        Dialog dialog = new Dialog(main.this);// 设置布局
                        dialog.setContentView(R.layout.dialog);// 设置弹窗标题
                        dialog.setTitle("Dialog");// 显示弹窗
                        dialog.show();
                    } }});



                        //publishmessageplus(mqtt_pub_topic,"1");
                       // publishmessageplus(mqtt_pub_topic4,"{\"deng_on\":11}");
                        //publishmessageplus(mqtt_pub_topic,"{\"method\":\"/a1cmeAg0g4h/esp8266/user/shuibeng\",\"params\":{\"WaterOutletSwitch\":0}}");
                        // publishmessageplus(mqtt_pub_topic,"{\"method\":\"thing.service.property.set\",\"id\":\"2001936421\",\"params\":{\"WaterOutletSwitch\":0},\"version\":\"1.0.0\"}");


                        //publishmessageplus(mqtt_pub_topic,"0");
                      //  publishmessageplus(mqtt_pub_topic4,"{\"deng_on\":11}");
                        //publishmessageplus(mqtt_pub_topic,"{\"method\":\"/a1cmeAg0g4h/esp8266/user/shuibeng\",\"params\":{\"WaterOutletSwitch\":1}}");
                        // publishmessageplus(mqtt_pub_topic,"{\"method\":\"thing.service.property.set\",\"id\":\"2001936421\",\"params\":{\"WaterOutletSwitch\":1},\"version\":\"1.0.0\"}");





            button2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {

                    if(feng_flag == 0)
                    {

                        //publishmessageplus(mqtt_pub_topic,"1");
                        publishmessageplus(mqtt_pub_topic3,"{\"id\":\"app\",\"version\":\"1.0\",\"params\":\"{\"deng_on\":\"  1 \"}\"}");
                        //publishmessageplus(mqtt_pub_topic,"{\"method\":\"/a1cmeAg0g4h/esp8266/user/shuibeng\",\"params\":{\"WaterOutletSwitch\":0}}");
                        // publishmessageplus(mqtt_pub_topic,"{\"method\":\"thing.service.property.set\",\"id\":\"2001936421\",\"params\":{\"WaterOutletSwitch\":0},\"version\":\"1.0.0\"}");
                        feng_flag =1;
                    }else{
                        //publishmessageplus(mqtt_pub_topic,"0");
                        publishmessageplus(mqtt_pub_topic3,"{\"id\":\"app\",\"version\":\"1.0\",\"params\":\"{\"deng_off\":\"0\" }\"}");
                        //publishmessageplus(mqtt_pub_topic,"{\"method\":\"/a1cmeAg0g4h/esp8266/user/shuibeng\",\"params\":{\"WaterOutletSwitch\":1}}");
                        // publishmessageplus(mqtt_pub_topic,"{\"method\":\"thing.service.property.set\",\"id\":\"2001936421\",\"params\":{\"WaterOutletSwitch\":1},\"version\":\"1.0.0\"}");
                        feng_flag =0;
                    }}});

            Mqtt_init();
            startReconnect();
            Log.v("mmmmm",getPackageName());
/*
            String jsonString ="{\"id\":\"ESP8266\",\"params\":{\"id\":\"ESP8266\",\"loc\":2,\"Seial\":0,\"Length\":21465,\"Picture\":\"gdueHPHpSbvobRZf2d/x2ZR\"},\"version\":\"1.0\"}";

            JSONObject result = null;
            try {
                result = new JSONObject(jsonString);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String ns="{\"id\":\"ESP8266\",\"loc\":2,\"Seial\":0,\"Length\":21465,\"Picture\":\"gdueHPHpSbvobRZf2d/x2ZR\"}";
            JSONObject T2=null;
            //Log.i("picture_neirong","picture"+id.toString());
            try {
                 T2=  result.getJSONObject("params"); // 获取值
                Log.v("picture",T2.toString());
                String spicture_neiron= T2.getString("id");
                Log.v("picture",spicture_neiron);
                String picture_neiron= T2.getString("Picture");
                Log.v("picture",picture_neiron);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
*/
            handler = new Handler() {
                @SuppressLint("HandlerLeak")
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 1: //开机校验更新回传
                            break;
                        case 2:  // 反馈回传

                            break;
                        case 3:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                            //显示收到的消息
                            //                      Toast.makeText(MainActivity.this,msg.obj.toString() ,Toast.LENGTH_SHORT).show();//弹窗功能
//                        text_test.setText(msg.obj.toString());
                            //取出温度值
                            //处理message 传过来的 obj字段（里面包了数据）
                            //  String T_val = msg.obj.toString().substring(msg.obj.toString().indexOf("temperature\":")+13,msg.obj.toString().indexOf("}"));
//                        String T_val = msg.obj.toString().substring(msg.obj.toString().indexOf("value\":")+7,msg.obj.toString().indexOf("value\":")+9);
//                        Toast.makeText(MainActivity.this,msg.obj.toString() ,Toast.LENGTH_SHORT).show();//弹窗功能
                            //String T1=msg.obj.toString();


                            String T_val="s";
                            String T_val1="s";
                            String T_val2="s";
                            String T_val5="s";

                            String picture_id1="0";

                            String picture_leibie1="0";
                            String picture_neirong="s";
                            int picture_length=0;
                            int i=0;
                            //String T_val_msg="{\"id\":\"sd\"}";

                            //Toast.makeText(main.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                            try {

                                //JSONObject result = new JSONObject(msg.obj.toString()); // String 转 JSONObject
                                JSONObject result = new JSONObject(msg.obj.toString());

                                    //Log.i("picture_neirong","picture"+id.toString());
                                    //Log.v("mmmmm", "22222");


                                  JSONObject T2= result.getJSONObject("params"); // 获取值
                                   String  jsonString= T2.toString();


                                    Log.v("mmm", jsonString);
                                    boolean contain1 = jsonString.contains("huanjingshidu");
                                    boolean contain2 = jsonString.contains("huanjingwendu");
                                    boolean contain3 = jsonString.contains("turangshidu");
                                    boolean contain4 = jsonString.contains("Picture");
                                    boolean contain7 = jsonString.contains("Length");
                                    boolean contain5 = jsonString.contains("guanzhaoqiangdu");
                                    if (contain1) {
                                        T_val1 = T2.getString("huanjingshidu");
                                    }
                                    if (contain2) {
                                        T_val = T2.getString("huanjingwendu");
                                    }
                                    if (contain3) {
                                        T_val2 = T2.getString("turangshidu");
                                    }
                                    if (contain5) {
                                        T_val2 = T2.getString("guangzhaoqiangdu");
                                    }

                                    //Log.v("mmm","1111");
                                    if (contain4) {
                                        picture_neirong = T2.getString("Picture");
                                        picture_id1 = T2.getString("loc");
                                        picture_leibie1 = T2.getString("Seial");
                                        arrayName[Integer.parseInt(picture_leibie1)][Integer.parseInt(picture_id1)] = picture_neirong;
                                        count_array[Integer.parseInt(picture_leibie1)]++;
                                        Log.v("mmm", "count" + count_array[Integer.parseInt(picture_leibie1)]);
                                    }
                                    Log.v("mmm", picture_neirong);
                                    if (contain7) {
                                        picture_length = T2.getInt("Length");
                                    }

                                    //Arrays.sort(pictures);

                                    Toast.makeText(main.this, picture_neirong, Toast.LENGTH_SHORT).show();
                                    int rowCount = arrayName.length; // 获取二维数组的行数
                                    int columnCount = arrayName[0].length; // 获取二维数组第一行的列数，假设每一行的列数相同
                                    int elementCount = rowCount * columnCount;

                                    if (count_array[Integer.parseInt(picture_leibie1)] == (picture_length / 600) + 1) {
                                        Log.v("mmm", "1111");
                                        temp = "";
                                        for (int k = 0; k < ((picture_length / 600) + 1); k++) {
                                            temp = temp + arrayName[0][k];
                                            Log.v("mmmm", arrayName[0][k]);
                                        }
                                        Log.v("mmmm", temp);

                                        Bitmap bitmap = decodeBase64ToBitmap(temp);
                                        imageView.setImageBitmap(bitmap);
                                        lei++;
                                    }

                                    //System.out.print(picture_neirong);
                                    //Log.i("picture_neirong","picture"+picture_neirong);


                                    //Toast.makeText(main.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();

                                } catch(JSONException e){
                                    e.printStackTrace();
                                }
                                //Toast.makeText(main.this,T_val,Toast.LENGTH_SHORT).show();

                                String T = msg.obj.toString().substring(msg.obj.toString().indexOf("method") + 38, msg.obj.toString().indexOf("method") + 40);
                                try {
                                    // 创建 FileWriter 对象
                                    FileWriter writer = new FileWriter(file);
                                    // 将字符串写入文件
                                    //Toast.makeText(main.this,"收到文件" ,Toast.LENGTH_SHORT).show();//弹窗功能
                                    writer.append(temp);
                                    // 关闭 FileWriter
                                    writer.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // Toast.makeText(MainActivity.this,T ,Toast.LENGTH_SHORT).show();//弹窗功能

                                if (T.equals("id")) {
                                    //环境温度
                                    boolean isDigitsOnly = T_val.matches("\\d+");
                                    if (isDigitsOnly) {

                                        if (Integer.parseInt(T_val) > 40) {
                                            musicPlayer.addMusic(String.valueOf(R.raw.huanjingwendu));
                                        }
                                        if (Integer.parseInt(T_val) < 10) {
                                            musicPlayer.addMusic(String.valueOf(R.raw.huanjingwendu_d));
                                        }
                                        int huanjingwendu_data=Integer.parseInt(T_val);
                                        huanjingwendu_LineChart1.addEntry(huanjingwendu_data);
                                        text_test2.setText(T_val);
                                    }
                                    //环境湿度
                                    boolean isDigitsOnly1 = T_val1.matches("\\d+");
                                    if (isDigitsOnly1) {
                                        text_test1.setText(T_val1);
                                        int huanjingshidu_data=Integer.parseInt(T_val1);
                                        huanjingshidu_LineChart1.addEntry(huanjingshidu_data);
                                        if (Integer.parseInt(T_val1) > 80) {
                                            musicPlayer.addMusic(String.valueOf(R.raw.huanjingshidu));
                                        }
                                        if (Integer.parseInt(T_val1) < 40) {
                                            musicPlayer.addMusic(String.valueOf(R.raw.huanjingshidu_d));
                                        }
                                    }
                                    //else {  Toast.makeText(main.this,"测量错误" ,Toast.LENGTH_SHORT).show(); }
                                    //}
                                    //if(T.equals(S2)) {

                                    //土壤湿度
                                    // String T_val2 = msg.obj.toString().substring(msg.obj.toString().indexOf("turangshidu") + 13, msg.obj.toString().indexOf("turangshidu") + 15);
                                    boolean isDigitsOnly2 = T_val2.matches("\\d+");
                                    //Toast.makeText(main.this,T_val2 ,Toast.LENGTH_SHORT).show();//弹窗功能
                                    if (isDigitsOnly2) {
                                        if (Integer.parseInt(T_val2) > 80) {
                                            musicPlayer.addMusic(String.valueOf(R.raw.turangshidu));
                                        }
                                        if (Integer.parseInt(T_val2) < 50) {
                                            musicPlayer.addMusic(String.valueOf(R.raw.turangshidu_d));
                                        }
                                        int turangshidu_data=Integer.parseInt(T_val2);
                                        turangshidu_LineChart1.addEntry(turangshidu_data);
                                        String text_val2 = "土壤湿度：" + T_val2 + " %";
                                        //在主进程 handler 里面更新UI  既保证了稳定性  又不影响网络传输
                                        text_test.setText(T_val2);
                                    }
                                    //光照强度
                                    boolean isDigitsOnly5 = T_val5.matches("\\d+");
                                    //Toast.makeText(main.this,T_val2 ,Toast.LENGTH_SHORT).show();//弹窗功能
                                    if (isDigitsOnly5) {
                                        if (Integer.parseInt(T_val5) > 80) {
                                            musicPlayer.addMusic(String.valueOf(R.raw.guangzhaoqiangdu));
                                        }
                                        if (Integer.parseInt(T_val5) < 50) {
                                            musicPlayer.addMusic(String.valueOf(R.raw.guangzhaoqiangdu_d));
                                        }
                                        //在主进程 handler 里面更新UI  既保证了稳定性  又不影响网络传输
                                        int guangzhaoqinangdu_data=Integer.parseInt(T_val1);
                                        guangzhaoqiangdu_LineChart1.addEntry(guangzhaoqinangdu_data);
                                        text_test.setText(T_val5);
                                    }


                                    //灯控制
                                    String T_val3 = msg.obj.toString().substring(msg.obj.toString().indexOf("deng") + 6, msg.obj.toString().indexOf("deng") + 7);
                                    boolean isDigitsOnly3 = T_val3.matches("\\d+");
                                    //Toast.makeText(main.this,T_val3 ,Toast.LENGTH_SHORT).show();
                                    if (isDigitsOnly3) {
                                        //Toast.makeText(main.this,T_val4 ,Toast.LENGTH_SHORT).show();
                                        deng_flag = Integer.parseInt(T_val3);
                                    }

                                    //风扇控制
                                    String T_val4 = msg.obj.toString().substring(msg.obj.toString().indexOf("feng") + 10, msg.obj.toString().indexOf("feng") + 11);
                                    boolean isDigitsOnly4 = T_val4.matches("\\d+");
                                    //Toast.makeText(main.this,T_val3 ,Toast.LENGTH_SHORT).show();
                                    if (isDigitsOnly4) {
                                        feng_flag = Integer.parseInt(T_val4);
                                    }
                                    lock = 0;
                                }
                                break;
                                case 30:  //连接失败
                                    Toast.makeText(main.this, "连接失败", Toast.LENGTH_SHORT).show();
                                    break;
                                case 31:   //连接成功
                                    Toast.makeText(main.this, "连接成功", Toast.LENGTH_SHORT).show();
                                    try {
                                        client.subscribe(mqtt_sub_topic, 1);
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        client.subscribe(mqtt_sub_topic1, 1);
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        client.subscribe(mqtt_sub_topic5, 1);
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        client.subscribe(mqtt_sub_topic6, 1);
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        client.subscribe(mqtt_sub_topic7, 1);
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                default:
                                    break;
                            }
                }

            };


        }
        class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
            //选择
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String deng = deng_arr[i];
                boolean contain6 = deng.contains("fangan");
                if(contain6){
                Toast.makeText(main.this, deng_arr[i], Toast.LENGTH_SHORT).show();
                publishmessageplus(mqtt_pub_topic4, "{\"id\":\"app\",\"version\":\"1.0\",\"params\":\"{\"shui_on\":\"" + deng + "\"}\"}");
                shuibeng.setText("");
            }}
            //未选择
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        }
        class MyOnItemSelectedListener1 implements AdapterView.OnItemSelectedListener {
            //选择
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String peng = jiaodu_arr[i];
                boolean contain7 = peng.contains("0");
                if (contain7){
                Toast.makeText(main.this, jiaodu_arr[i], Toast.LENGTH_SHORT).show();
                publishmessageplus(mqtt_pub_topic9, "{\"id\":\"app\",\"version\":\"1.0\",\"params\":\"{\"shui_on\":\"" + peng + "\"}\"}");
            }}
            //未选择
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        }




        private void Mqtt_init()
        {
            try {
                //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
                client = new MqttClient(host, mqtt_id,
                        new MemoryPersistence());
                //MQTT的连接设置
                options = new MqttConnectOptions();
                //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
                options.setCleanSession(false);
                //设置连接的用户名
                options.setUserName(userName);
                //设置连接的密码
                options.setPassword(passWord.toCharArray());
                // 设置超时时间 单位为秒
                options.setConnectionTimeout(70);
                // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
                options.setKeepAliveInterval(100);
                //设置回调
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        //连接丢失后，一般在这里面进行重连
                        System.out.println("connectionLost----------");
                        //startReconnect();
                    }
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        //publish后会执行到这里
                        System.out.println("deliveryComplete---------"
                                + token.isComplete());
                    }
                    @Override
                    public void messageArrived(String topicName, MqttMessage message)
                            throws Exception {
                        //subscribe后得到的消息会执行到这里面
                        System.out.println("messageArrived----------");
                        Message msg = new Message();
                        msg.what = 3;   //收到消息标志位
                        msg.obj = message.toString();
                        handler.sendMessage(msg);    // hander 回传
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void Mqtt_connect() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!(client.isConnected()) )  //如果还未连接
                        {
                            client.connect(options);
                            Message msg = new Message();
                            msg.what = 31;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message msg = new Message();
                        msg.what = 30;
                        handler.sendMessage(msg);
                    }
                }
            }).start();
        }
        private void startReconnect() {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (!client.isConnected()) {
                        Mqtt_connect();
                    }
                }
            }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
        }
        private void publishmessageplus(String topic,String message2)
        {
            if (client == null || !client.isConnected()) {
                return;
            }
            MqttMessage message = new MqttMessage();
            message.setPayload(message2.getBytes());
            try {

                client.publish(topic,message);
            } catch (MqttException e) {

                e.printStackTrace();
            }

        }
        public class Picture implements Comparable{
            public String neirong;
            public int xuhao;
            public int leibie;
            public Picture(int leibie,int xuhao,String neirong) {
                this.neirong = neirong;
                this.xuhao = xuhao;
                this.leibie =leibie;
            }
            public void setNeirong(String neirong) {
                this.neirong = neirong;
            }
            public void setxuhao(int xuahao) {
                this.xuhao = xuhao;
            }

            public void setleibie(int leibie) {
                this.leibie = leibie;
            }

            @Override
            public int compareTo(Object o) {
                return this.xuhao;
            }
        }
        public class MusicPlayer {
            private ArrayBlockingQueue<String> queue;
            private MediaPlayer mediaPlayer;
            public MusicPlayer() {
                queue = new ArrayBlockingQueue<>(10);
                mediaPlayer = new MediaPlayer();
            }
            public void addMusic(String musicPath) {
                queue.offer(musicPath); // 将音乐路径加入队列
            }
            public void startPlaying() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if(lock==0) {
                                String musicPath = null; // 阻塞直到队列有音乐路径可取
                                try {
                                    musicPath = queue.take();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                playMusic(musicPath);
                                try {
                                    sleep(1);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                lock=1;
                            }
                        }
                    }
                }).start();
            }
            private void playMusic(String musicPath) {
                try {
                    mediaPlayer.reset();
                    Uri setDataSourceuri = Uri.parse("android.resource://" + getPackageName() + "/" +musicPath);
                    mediaPlayer.setDataSource(main.this,setDataSourceuri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // 音乐播放完成后，从队列中取出下一首音乐继续播放
                            if (!queue.isEmpty()) {
                                String nextMusicPath = queue.poll();
                                playMusic(nextMusicPath);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
        private Bitmap decodeBase64ToBitmap(String base64String) {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }

    }

