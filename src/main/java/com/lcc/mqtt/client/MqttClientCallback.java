package com.lcc.mqtt.client;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-04 09:40
 */
public class MqttClientCallback  implements MqttCallback {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    /**
     * 当与服务器的连接丢失时调用。
     * 此时，应用程序可以选择实现重连接逻辑。这个示例简单地退出。
     */
    public void connectionLost(Throwable cause) {
        log("Connection  lost!" + cause);
    }

    @Override
    /**
     * 当来自服务器的消息与客户机的任何订阅匹配时调用
     */
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String time = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println("Time:\t" +time +
                "  Topic:\t" + topic +
                "  Message:\t" + new String(message.getPayload()) +
                "  QoS:\t" + message.getQos());
    }

    @Override
    /**
     * 当消息被发送到服务器时调用。这里传递的令牌与传递给发布的原始调用或从发布调用返回的令牌相同。这允许应用程序在交付完成之前执行异步交付而不阻塞。
     * 这个示例演示了异步交付，并在主线程中使用token.waitforcompletion()调用，该调用将阻塞直到交付完成。此外，如果在客户机上设置回调，那么将调用deliveryComplete方法
     * 如果到服务器的连接在交付完成之前中断，则消息的交付将在客户机重新连接之后完成。
     * getPendingTokens方法将为仍然要传递的任何消息提供令牌。
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
        for (String topic : token.getTopics()) {
            logger.info("send mqtt message success ,topic:{} ,messageId:{}",topic,token.getMessageId());
        }
    }

    void log(String text){
        logger.info(text);
    }
}
