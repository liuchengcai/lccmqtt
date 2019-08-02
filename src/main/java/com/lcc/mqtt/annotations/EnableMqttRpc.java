package com.lcc.mqtt.annotations;

import com.lcc.mqtt.configuration.MqttRpcBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MqttRpcBeanDefinitionRegistrar.class)
public @interface EnableMqttRpc {

    String[] basePackages() default {};

}
