package com.lcc.mqtt.client;

import com.lcc.mqtt.constant.MqttQos;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.util.Assert;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-04 18:14
 */
public class MqttMessageBuilder {

    private Object payload;
    private int qos = 1;
    private boolean retained = false;
    private int messageId;

    public MqttMessage build(){
        Assert.notNull(payload,"payload is null");
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setPayload(MqttMessageConverter.toByteArray(payload));
        message.setRetained(retained);
        message.setId(messageId);
        return message;
    }

    public MqttMessageBuilder payload(Object payload){
        this.payload = payload;
        return this;
    }
    public MqttMessageBuilder qos(MqttQos qos){
        this.qos = qos.get();
        return this;
    }
    public MqttMessageBuilder retained(boolean retained){
        this.retained = retained;
        return this;
    }
    public MqttMessageBuilder messageId(int messageId){
        this.messageId = messageId;
        return this;
    }

}
