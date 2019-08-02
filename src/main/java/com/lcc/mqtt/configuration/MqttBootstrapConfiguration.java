package com.lcc.mqtt.configuration;

import com.lcc.mqtt.client.MqttTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-04 10:28
 */
@Configuration
public class MqttBootstrapConfiguration {

    private static final String MQTT_LISTENER_ANNOTATION_BEAN_POSTPROCESSOR_NAME="mqtt.starter.internalMqttListenerAnnotationBeanPostProcessor";

    @Bean(name=MQTT_LISTENER_ANNOTATION_BEAN_POSTPROCESSOR_NAME)
    public MqttListenerAnnotationBeanPostProcessor mqttListenerAnnotationBeanPostProcessor(MqttTemplate template){
        MqttListenerAnnotationBeanPostProcessor processor = new MqttListenerAnnotationBeanPostProcessor();
        return processor;
    }

}
