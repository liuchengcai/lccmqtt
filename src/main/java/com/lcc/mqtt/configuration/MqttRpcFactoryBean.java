package com.lcc.mqtt.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-10 16:27
 */
public class MqttRpcFactoryBean implements FactoryBean, InitializingBean, ApplicationContextAware {

    private Class<?> type;
    ApplicationContext applicationContext;


    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},new MqttRpcInvocationHandler(applicationContext));
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
