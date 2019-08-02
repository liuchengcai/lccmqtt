package com.lcc.mqtt.annotations;

import com.lcc.mqtt.configuration.MqttBootstrapConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MqttBootstrapConfiguration.class)
public @interface EnableMqtt {
}
