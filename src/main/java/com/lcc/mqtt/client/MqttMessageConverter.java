package com.lcc.mqtt.client;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-04 17:38
 */
public class MqttMessageConverter {

    public static byte[] toByteArray(String message){
        return message.getBytes();
    }

    public static  byte[] toByteArray(Object message){
        if(message instanceof byte[]){
            return (byte[]) message;
        }else if(message instanceof  String){
            return toByteArray((String) message);
        }else{
            return toByteArray(JSONObject.toJSONString(message));
        }
    }

    public static String convert2String(byte[] message){
        return new String(message);
    }

    public static <T> T convert2T(Class<T> c,MqttMessage message){
        if(c == byte[].class){
            return (T) message.getPayload();
        }else if(c == String.class){
            return (T) convert2String(message.getPayload());
        }else if(c == MqttMessage.class){
            return (T)message;
        }else {
            String content = new String(message.getPayload());
            if(content.startsWith("{") && content.endsWith("}")){
                return JSONObject.parseObject(content,c);
            }else{
                throw new RuntimeException(c.getName()+" is not support");
            }
        }
    }
}
