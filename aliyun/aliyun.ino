#include <ESP8266WiFi.h>   //安装esp8266arduino开发环境
#include <PubSubClient.h>  //安装PubSubClient库
#include <ArduinoJson.h>   //json  V5版本
#include <Servo.h>
#include "aliyun_mqtt.h"
//需要安装crypto库

#define WIFI_SSID        "esp8266"//替换自己的WIFI
#define WIFI_PASSWD      "cyy020814"//替换自己的WIFI

#define PRODUCT_KEY      "a1946HaV1CI" //替换自己的PRODUCT_KEY
#define DEVICE_NAME      "STM32" //替换自己的DEVICE_NAME
#define DEVICE_SECRET    "5d83432285ec664841350841e3cf5529"//替换自己的DEVICE_SECRET

#define DEV_VERSION       "S-TH-WIFI-v1.0-20190220"        //固件版本信息
#define ALINK_BODY_FORMAT         "{\"id\":\"123\",\"version\":\"1.0\",\"method\":\"%s\",\"params\":%s}"

//*************  
#define ALINK_TOPIC_PROP_POSTRSP  "/sys/" PRODUCT_KEY "/" DEVICE_NAME "/thing/event/property/post_reply"
                          
#define ALINK_METHOD_PROP_POST    "thing.service.property.set"                        
#define ALINK_TOPIC_DEV_INFO      "/ota/device/inform/" PRODUCT_KEY "/" DEVICE_NAME ""    
#define ALINK_VERSION_FROMA      "{\"id\": 123,\"params\": {\"version\": \"%s\"}}"
//-----------------------------接收消息的TOPIC -------------------------------------------
#define ALINK_TOPIC_PROP_SET      "/"PRODUCT_KEY "/" DEVICE_NAME"/user/esp8266" //这个就是我们接收消息的TOPIC        
//------------------------------接收消息的TOPIC ------------------------------------------ 

//***************************这个是上传数据的TOPIC******************************************
#define ALINK_TOPIC_PROP_POST       "/"PRODUCT_KEY "/" DEVICE_NAME"/a1946HaV1CI/STM32/user/deng" //这个是上传数据的TOPIC/a1qNrNN1l9s/esp8266/user/esp8266Fankun
//****************************这个是上传数据的TOPIC****************************************   
unsigned long lastMs = 0;
int pos = 90;
char order;

WiFiClient   espClient;
PubSubClient mqttClient(espClient);
Servo myServoD1,myServoD2,myServoD3,myServoD4;//这个是4个伺服电机的名字

void init_wifi(const char *ssid, const char *password)      //连接WiFi
{
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED)
    {
        Serial.println("WiFi does not connect, try again ...");
        delay(500);
    }

    Serial.println("Wifi is connected.");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
}

void mqtt_callback(char *topic, byte *payload, unsigned int length) //mqtt回调函数“byte *payload”这东西是个指针
{
    Serial.print("Message arrived [");
    Serial.print(topic);
    Serial.print("] ");
    payload[length] = '\0';

   // https://arduinojson.org/v5/assistant/  json数据解析网站

    
 
  Serial.println((char *)payload);
  
       char a = *payload;                        //这样我们就可以把指针里的东西拿出来给a
       order = a;
        Serial.print("接收到的a:");
       Serial.println(a);
       int b = *payload;                          
       Serial.print("接收到的b:");
       Serial.println(b);
       //#############################这里这里看这里##########################
       IO(order);                                               //这个是PWM功能
       //PWM(order);                                           //这个是PWM功能
      // servo(order);                                         //这个是PWM功能
      //#############################这里这里看这里##########################
       }
 

void mqtt_check_connect(){                                        
    while (!mqttClient.connected())
    {
        while (connect_aliyun_mqtt(mqttClient, PRODUCT_KEY, DEVICE_NAME, DEVICE_SECRET))
        {
            Serial.println("MQTT connect succeed!");
            mqttClient.subscribe(ALINK_TOPIC_PROP_SET);          //这个就是引用开始定义的topic
           Serial.println("subscribe done");
           
        }
    }
}

void mqtt_interval_post(int a)
{
    char param[512];
    char jsonBuf[1024];
    
 
   sprintf(param, "{\"a\":%d,\"read\":%d,\"currentTemperature\":%d}",a,analogRead(A0),random(0,55));
    /*sprintf(param, "{\"LightSwitch\":%d,\"CurrentTemperature\":%d}", digitalRead(D4),random(0,55));
    sprintf(jsonBuf, ALINK_BODY_FORMAT, ALINK_METHOD_PROP_POST, param);
    Serial.println(jsonBuf);*/
    mqttClient.publish(ALINK_TOPIC_PROP_POST, param); //这个是上传数据的topic,jsonBuf这个是上传的数据
}


void setup()
{

   
    myServoD1.attach(D1);                          //四个私服电机的引脚
    myServoD2.attach(D2);
    myServoD3.attach(D3);
    myServoD4.attach(D4);

  int pos =  myServoD2.read();                      //设置4个电机的角度和D2这个电机相同
  myServoD1.write(pos);                                
                       
  myServoD2.write(pos);

  myServoD3.write(pos);

  myServoD4.write(pos);
  
      pinMode(D0, OUTPUT);
      pinMode(D1, OUTPUT);
      pinMode(D2, OUTPUT);
      pinMode(D3, OUTPUT);
      pinMode(D4, OUTPUT);
      pinMode(D5, OUTPUT);
      pinMode(D6, OUTPUT);
      pinMode(D7, OUTPUT);
      pinMode(D8, OUTPUT);

      digitalWrite(D0,LOW);
      digitalWrite(D1,LOW);
      digitalWrite(D2,LOW);
      digitalWrite(D3,LOW);
      digitalWrite(D4,LOW);
      digitalWrite(D5,LOW);
      digitalWrite(D6,LOW);
      digitalWrite(D7,LOW);
      digitalWrite(D8,LOW);
     
    Serial.begin(115200);

    Serial.println("Demo Start");

    init_wifi(WIFI_SSID, WIFI_PASSWD);

    mqttClient.setCallback(mqtt_callback);
}



void loop()
{
   if (millis() - lastMs >= 5000)  
    {
        lastMs = millis();
        mqtt_check_connect();

        if(order == '"'){
          mqtt_interval_post(0);
          }

 
    }
    mqttClient.loop();

   
}

void IO(char a){
  switch(a){
  case'0':if(digitalRead(D0)){digitalWrite(D0,LOW);}else{digitalWrite(D0,HIGH);}mqtt_interval_post(digitalRead(D0));break;
  case'1':if(digitalRead(D1)){digitalWrite(D1,LOW);}else{digitalWrite(D1,HIGH);}mqtt_interval_post(digitalRead(D1));break;
  case'2':if(digitalRead(D2)){digitalWrite(D2,LOW);}else{digitalWrite(D2,HIGH);}mqtt_interval_post(digitalRead(D2));break;
  case'3':if(digitalRead(D3)){digitalWrite(D3,LOW);}else{digitalWrite(D3,HIGH);}mqtt_interval_post(digitalRead(D3));break;
  case'4':if(digitalRead(D4)){digitalWrite(D4,LOW);}else{digitalWrite(D4,HIGH);}mqtt_interval_post(digitalRead(D4));break;
  case'5':if(digitalRead(D5)){digitalWrite(D5,LOW);}else{digitalWrite(D5,HIGH);}mqtt_interval_post(digitalRead(D5));break;
  case'6':if(digitalRead(D6)){digitalWrite(D6,LOW);}else{digitalWrite(D6,HIGH);}mqtt_interval_post(digitalRead(D6));break;
  case'7':if(digitalRead(D7)){digitalWrite(D7,LOW);}else{digitalWrite(D7,HIGH);}mqtt_interval_post(digitalRead(D7));break;
  case'8':if(digitalRead(D8)){digitalWrite(D8,LOW);}else{digitalWrite(D8,HIGH);}mqtt_interval_post(digitalRead(D8));break;
 // case'9':mqtt_interval_post();break;
   }
  }
 void PWM(int a){
  int b = (a-48)*28*4;
  analogWrite(D1,b); 
  analogWrite(D2,b); 
  analogWrite(D3,b); 
  analogWrite(D4,b); 
  mqtt_interval_post(b);
  }
 
 void servo(int a){
  int b = (a-48)*20;
  int c = myServoD2.read();
  int pos;
  if(b>c){
  for(pos = c;pos<=b;pos++){
  myServoD1.write(pos);
  myServoD2.write(pos);
  myServoD3.write(pos);
  myServoD4.write(pos);
  mqtt_interval_post(pos);

  }}else {
    for(pos = c;pos>=b;pos--){
  myServoD1.write(pos);
  myServoD2.write(pos);
  myServoD3.write(pos);
  myServoD4.write(pos);
  mqtt_interval_post(pos);
 
    }
  }
  }
  
