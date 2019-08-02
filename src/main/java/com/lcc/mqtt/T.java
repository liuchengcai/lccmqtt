package com.lcc.mqtt;

import com.lcc.mqtt.annotations.MqttListener;
import com.lcc.mqtt.constant.MqttQos;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-08-02 10:48
 */
public class T {
    @MqttListener(topic = "dd",qos= MqttQos.ONE)
    public void test(){

    }
}
