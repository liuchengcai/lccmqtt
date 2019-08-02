package com.lcc.mqtt.configuration;

import com.lcc.mqtt.client.MqttTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-10 17:01
 */
public class MqttRpcInvocationHandler implements InvocationHandler {

    ApplicationContext applicationContext;
    MqttTemplate template;

    public MqttRpcInvocationHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        template = applicationContext.getBean(MqttTemplate.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        }
        Assert.isTrue(null != template,"template is null");
        Object result = new MqttRpcTask(method,template).execute(args).get();
        return result;
    }
}
