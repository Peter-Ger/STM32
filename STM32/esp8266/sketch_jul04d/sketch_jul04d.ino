#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <String.h>
#include <stdlib.h>
#include <base64.h>

float h;
float t;
long s;
// 连接WIFI和密码 
#define WIFI_SSID         "Mi 11"
#define WIFI_PASSWD       "66668888"


//设备的三元组信息
#define PRODUCT_KEY       "a1946HaV1CI"
#define DEVICE_NAME       "STM32"
#define DEVICE_SECRET     "5d83432285ec664841350841e3cf5529"
#define REGION_ID         "cn-shanghai"

//不需要改 
#define MQTT_SERVER       PRODUCT_KEY ".iot-as-mqtt." REGION_ID ".aliyuncs.com"
#define MQTT_PORT         1883
#define MQTT_USRNAME      DEVICE_NAME "&" PRODUCT_KEY

#define CLIENT_ID     "a1946HaV1CI.STM32|securemode=2,signmethod=hmacsha256,timestamp=1688453306816|"
#define MQTT_PASSWD       "43fcd5d072aad7c2c54564a2720724e11f349f8fe9af4cee84a97f5c36484860"

#define ALINK_BODY_FORMAT         "{\"id\":\"ESP8266\",\"version\":\"1.0\",\"params%d\":%s}"
#define ALINK_TOPIC_PROP_POST_HUMIDITY     "/a1946HaV1CI/STM32/user/huanjingshidu"
#define ALINK_TOPIC_PROP_POST_TEMPERATURE  "/a1946HaV1CI/STM32/user/huanjingwendu"
#define ALINK_TOPIC_PROP_POST_PICTURE      "/a1946HaV1CI/STM32/user/picture"
#define ALINK_TOPIC_PROP_POST_GUANGZHAO      "/a1946HaV1CI/STM32/user/guangzhao"

#define ALINK_TOPIC_PROP_POST_FENGSHAN     "/a1946HaV1CI/STM32/user/fengshan"
#define ALINK_TOPIC_PROP_POST_DENG         "/a1946HaV1CI/STM32/user/deng"
#define ALINK_TOPIC_PROP_POST_DING      "/a1946HaV1CI/STM32/user//ding"
#define ALINK_TOPIC_PROP_POST_SHUIBENG     "/a1946HaV1CI/STM32/user//shuibeng"

#define MAX_JSON_SIZE 256

unsigned long lastMs = 0;
int Serial_num=0;
String msg_data;
int length=0;
unsigned int USART_RX_STA=0;
WiFiClient espClient;
PubSubClient  client(espClient);


//连接wifi
void wifiInit()
{
    char data;
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASSWD);
    while (WiFi.status() != WL_CONNECTED)
    {
        delay(1000);
        Serial.println("WiFi not Connect");
    }
    while (Serial.available() > 0)//串口接收到数据
  {
    data = Serial.read();//清空数据
  }
  
    client.setServer(MQTT_SERVER, MQTT_PORT);   //连接MQTT服务器 
    client.setCallback(callback);   //设定回调方式，当ESP8266收到订阅消息时会调用此方法
    Serial.println("WiFiOn");
}

//mqtt连接
void mqttCheckConnect()
{
    while (!client.connected())
    {
      if(client.connect(CLIENT_ID, MQTT_USRNAME, MQTT_PASSWD)){
        //client.subscribe(ALINK_TOPIC_PROP_POST_TEMPERATURE);
        //client.subscribe(ALINK_TOPIC_PROP_POST_HUMIDITY);
        client.subscribe(ALINK_TOPIC_PROP_POST_FENGSHAN);
        client.subscribe(ALINK_TOPIC_PROP_POST_DENG);
        client.subscribe(ALINK_TOPIC_PROP_POST_DING );
        client.subscribe(ALINK_TOPIC_PROP_POST_SHUIBENG);
      }
      delay(10);
    }
}


void setup() 
{
    Serial.begin(115200);
    Serial.setRxBufferSize(1024);
    wifiInit();
    //Serial.println('\r');
    //Serial.println('\n');
}

void read_data()
{
  mqttCheckConnect();
  char data;
  while (Serial.available() > 0)//串口接收到数据,接受head
  {
    data = Serial.read();//获取串口接收到的数据
    //Serial.println(data);
    if(USART_RX_STA&0x4000){
      if(data!='\n'){
        USART_RX_STA=0;
        //Serial.println("n\r");
        msg_data="";
      }
        else {
          USART_RX_STA|=0x8000;
          //Serial.println("\n");
          break; 
        }
    }
    else{
      if(data=='\r'){
        USART_RX_STA|=0x4000;
        //Serial.println("\r");
      }
        else
        {
          msg_data+=data;
          USART_RX_STA++;
          if(USART_RX_STA>(512-1)){
            USART_RX_STA=0;//接收数据错误,重新开始接收
            msg_data="";    
          }
        } 
    }
    //Serial.println("message");
    //Serial.println(msg_data);
  }
  String last_data;
  int last_length=0;
  int last_count=0;
  int mqtt_num=0;
  if(USART_RX_STA&0x8000){
    //Serial.println(msg_data.substring(0, 5));
    //Serial.println(msg_data.substring(5));
    //Serial.println("jingru");
    if(msg_data.substring(0, 5)=="photo"){
       length=msg_data.substring(5).toInt();
       Serial.println("p");
       //sprintf(param, "{\"whole\":%d}", length);
       //sprintf(jsonBuf, ALINK_BODY_FORMAT, mqtt_num,param);
       //client.publish(ALINK_TOPIC_PROP_POST_TEMPERATURE, jsonBuf);  
       while(last_length<=length){
        if(Serial.available() > 0){
          data = Serial.read();
          last_data+=data;
          last_count++;
          last_length++;
          //等于600
          if(last_count==600){
             String new_data="{\"id\":\"ESP8266\",\"version\":\"1.0\",\"params\":{\"Length\":Wlen,\"loc\":Num,\"Seial\":SeN,\"Picture\":\"Name\"}}";
             new_data.replace("Num",String(mqtt_num));
             new_data.replace("SeN",String(Serial_num));
             new_data.replace("Wlen",String(length));
             new_data.replace("Name",base64::encode(last_data));
             char* charPtr = new char[new_data.length() + 1];
             new_data.toCharArray(charPtr,new_data.length() + 1);
             client.publish(ALINK_TOPIC_PROP_POST_PICTURE,charPtr);
             free(charPtr);
             last_count=0;
             mqtt_num++;
             last_data="";
             Serial.println("OK");
          }
          //等于结束
          else if(last_length==length){
            //Serial.println(last_data.length());
             String new_data="{\"id\":\"ESP8266\",\"version\":\"1.0\",\"params\":{\"Length\":Wlen,\"loc\":Num,\"Seial\":SeN,\"Picture\":\"Name\"}}";
             new_data.replace("Num",String(mqtt_num));
             new_data.replace("SeN",String(Serial_num));
             new_data.replace("Wlen",String(length));
             new_data.replace("Name",base64::encode(last_data));
             
             char* charPtr = new char[new_data.length() + 1];
             new_data.toCharArray(charPtr,new_data.length() + 1);
             client.publish(ALINK_TOPIC_PROP_POST_PICTURE,charPtr);
             //Serial.println(new_data.length());
             free(charPtr);
             last_count=0;
             mqtt_num++;
             last_data="";
             Serial.println("AllOK");
             Serial_num++;          
          }     
        }
       }
       while(Serial.available() > 0)
          data = Serial.read();
    }
    else if(msg_data.substring(0, 4)=="temp"){
      int hum_index=msg_data.indexOf('h');
      int temperature=msg_data.substring(4,hum_index).toInt();
      int humidity=msg_data.substring(hum_index+4).toInt();
      //环境温度
      String new_data="{\"id\":\"ESP8266\",\"version\":\"1.0\",\"params\":{\"temperature\":wildcard_tem}}"; 
      new_data.replace("wildcard_tem",String(temperature));
      char* charPtr = new char[new_data.length() + 1];
      new_data.toCharArray(charPtr,new_data.length() + 1);
      client.publish(ALINK_TOPIC_PROP_POST_TEMPERATURE,charPtr);
      free(charPtr);
      //环境湿度
      new_data="{\"id\":\"ESP8266\",\"version\":\"1.0\",\"params\":{\"humidity\":wildcard_hum}}"; 
      new_data.replace("wildcard_hum",String(humidity));
      char* charPtr1 = new char[new_data.length() + 1];
      new_data.toCharArray(charPtr1,new_data.length() + 1);
      client.publish(ALINK_TOPIC_PROP_POST_HUMIDITY,charPtr1);
      free(charPtr1);
      //Serial.println("tOK");
    }
    else if(msg_data.substring(0, 9)=="intensity"){
      int intensity=msg_data.substring(9).toInt();
      String new_data="{\"id\":\"ESP8266\",\"version\":\"1.0\",\"params\":{\"intensity\":wildcard_int}}";
      new_data.replace("wildcard_int",String(intensity)); 
      char* charPtr = new char[new_data.length() + 1];
      new_data.toCharArray(charPtr,new_data.length() + 1);
      client.publish(ALINK_TOPIC_PROP_POST_GUANGZHAO,charPtr);
      free(charPtr);
    }
    msg_data="";
    USART_RX_STA=0;
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  //Serial.print("Message arrived [");
  //Serial.print(topic);
  //Serial.print("] ");
  for (int i = 0; i < length; i++) {
    //Serial.print((char)payload[i]);
  }
      // 解析JSON数据
    StaticJsonDocument<MAX_JSON_SIZE> doc;
    DeserializationError error = deserializeJson(doc, payload);

    if (error) {
      //Serial.print("Error parsing JSON data: ");
      //Serial.println(error.c_str());
      return;
    }

    // 获取params中的字段值
    int deng = doc["params"]["deng"];
    int fengshan = doc["params"]["fengshan"];
    int ding = doc["params"]["ding"];
    int shuibeng = doc["params"]["shuibeng"];

    // 构建相应的字符串
    char dengStr[8];
    if(deng != 0){
      sprintf(dengStr, "deng%d", deng);
      }

    char fengshanStr[12];
    if(fengshan != 0){
       sprintf(fengshanStr, "fengshan%d", fengshan);
      }
  
    char dingStr[16];
    if(ding != 0){
      sprintf(dingStr, "ding%d", ding);
      }
      
    char shuibengStr[16];
    if(shuibeng != 0){
      sprintf(shuibengStr, "shuibeng%d", shuibeng);
      }


    // 打印结果，调试使用
    //Serial.print("deng: ");
    Serial.println(dengStr);
    //Serial.print("fengshan: ");
    Serial.println(fengshanStr);
    //Serial.print("ding: ");
    Serial.println(dingStr);
    //Serial.print("shuibeng: ");
    Serial.println(shuibengStr);
  
}


void loop()
{
  read_data();
  client.loop();
  delay(10);
}
