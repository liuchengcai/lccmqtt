package com.lcc.mqtt.client;

import lombok.Data;
import lombok.experimental.Accessors;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-03 17:52
 */
@Data
@Accessors(chain = true)
public class MqttTemplate implements MqttOperations, DisposableBean {

    static Logger logger = LoggerFactory.getLogger(MqttTemplate.class);

    MqttClient client;

    @Override
    public void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener) throws MqttException {
        client.subscribe(topicFilter,qos,messageListener);
    }

    @Override
    public void publish(String topic, MqttMessage message) throws MqttException {
        client.publish(topic,message);
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public void reconnect() throws MqttException {
        client.reconnect();
    }

    @Override
    public void close() throws MqttException {
        client.close();
    }

    @Override
    public void destroy() throws Exception {
        if (null != client ){
            logger.info("mqtt client try to disconnect...");
            client.disconnect();
            logger.info("mqtt client disconnected...");
        }
    }
}
