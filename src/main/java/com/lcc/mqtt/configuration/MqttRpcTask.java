package com.lcc.mqtt.configuration;

import com.lcc.mqtt.annotations.MqttRpc;
import com.lcc.mqtt.client.MqttMessageBuilder;
import com.lcc.mqtt.client.MqttMessageConverter;
import com.lcc.mqtt.client.MqttTemplate;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-11 10:50
 */
public class MqttRpcTask {

    Logger logger = LoggerFactory.getLogger(getClass());

    MqttTemplate template;
    Method method;
    ReentrantLock lock;
    Condition condition;
    Object result;
    boolean isDone = false;

    public MqttRpcTask execute(Object[] args) throws MqttException {
        MqttRpc rpc = method.getAnnotation(MqttRpc.class);
        if (null == rpc) {
            isDone = true;
            condition.signalAll();
            return this;
        }
        //发布消息
        MqttMessage message = new MqttMessageBuilder().payload(MqttMessageConverter.toByteArray(args[0])).qos(rpc.publishQos()).build();
        logger.info("publish message:{} to topic:{} ", args[0], rpc.publishTopic());
        template.publish(rpc.publishTopic(), message);
        logger.info("publish message to topic:{} success", rpc.publishTopic());
        //监听消息
        template.subscribe(rpc.subcribeTopic(), rpc.subcribeQos().get(), (topic, response) -> {
            lock.lock();
            try {
                logger.info("receive message:{} from topic:{}", response, topic);
                result = MqttMessageConverter.convert2T(method.getReturnType(), response);
                isDone = true;
                condition.signalAll();
                logger.info("receive message from topic:{},notify wait processor", topic);
            } finally {
                lock.unlock();
            }
        });
        logger.info("subcribe topic:{} success,wait for response",rpc.subcribeTopic());
        return this;
    }

    public Object get() throws InterruptedException {
        lock.lock();
        try {
            while(!isDone){
                logger.info("wait for invoke method:{} result.....",method.getName());
                condition.await();
            }
        } finally {
            lock.unlock();
        }
        logger.info("get method:{} result:{}",method.getName(),result);
        return result;
    }

    public MqttRpcTask(Method method,MqttTemplate template) {
        this.template = template;
        this.method = method;
        this.lock = new ReentrantLock();
        condition = this.lock.newCondition();
    }
}
