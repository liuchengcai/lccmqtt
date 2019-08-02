package com.lcc.mqtt.annotations;

import com.lcc.mqtt.constant.MqttQos;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttRpc {

    String publishTopic() default "";

    MqttQos publishQos() default MqttQos.TWO;

    String subcribeTopic() default "";

    MqttQos subcribeQos() default MqttQos.TWO;

    int timeout() default -1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
