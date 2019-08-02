package com.lcc.mqtt.annotations;

import com.lcc.mqtt.constant.MqttQos;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttListener {
    /**
     * topic
     * @return String topic
     */
    String topic();

    /**
     * qos 注意默认初始化-1,不合法
     * @return int qos
     */
    MqttQos qos();

}
