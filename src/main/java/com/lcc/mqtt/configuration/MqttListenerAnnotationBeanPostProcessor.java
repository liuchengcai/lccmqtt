package com.lcc.mqtt.configuration;

import com.lcc.mqtt.annotations.MqttListener;
import com.lcc.mqtt.client.MqttMessageConverter;
import com.lcc.mqtt.client.MqttTemplate;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-04 10:37
 */
public class MqttListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, ApplicationContextAware, ApplicationListener<ApplicationStartedEvent> {

    private Logger logger = LoggerFactory.getLogger(MqttListenerAnnotationBeanPostProcessor.class);
    private final ConcurrentMap<Object, TypeMetadata> typeCache = new ConcurrentHashMap<>();
    ApplicationContext applicationContext;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        this.typeCache.computeIfAbsent(bean, this::buildMetadata);
        return bean;
    }

    private TypeMetadata buildMetadata(Object bean) {
        try {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            final List<ListenerMethod> methods = new ArrayList<>();
            ReflectionUtils.doWithMethods(targetClass, method -> {
                MqttListener listenerAnnotation = findListenerAnnotations(method);
                if (null != listenerAnnotation) {
                    methods.add(new ListenerMethod(method,
                            listenerAnnotation));
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
            if (methods.isEmpty()) {
                return null;
            }
            return new TypeMetadata(
                    methods.toArray(new ListenerMethod[methods.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * AnnotationUtils.getRepeatableAnnotations does not look at interfaces
     */
    private MqttListener findListenerAnnotations(Method method) {
        MqttListener ann = AnnotationUtils.findAnnotation(method, MqttListener.class);
        return ann;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if(typeCache.isEmpty()){
            return;
        }
        MqttTemplate template = applicationContext.getBean(MqttTemplate.class);
        Assert.notNull(template,"template is null");
        typeCache.forEach((key,value)-> Arrays.stream(value.listenerMethods).forEach(method -> {
            try {
                if(method.targetMethod.getParameterTypes().length>0){
                    template.subscribe(method.annotation.topic(),method.annotation.qos().get(), (topic, message)->{
                        method.targetMethod.invoke(key, MqttMessageConverter.convert2T(method.targetMethod.getParameterTypes()[0],message));
                    });
                    logger.info("mqtt client subscribe topic:{},qos:{}",method.annotation.topic(),method.annotation.qos());
                }else{
                    logger.warn("mqtt client method:{} don`t have any args",method.targetMethod.getName());
                }
            } catch (MqttException e) {
                logger.error("mqtt subscribe faild ",e);
            }
        }));
        this.typeCache.clear();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * The metadata holder of the class with {@link MqttListener} annotations.
     */
    private static class TypeMetadata {

        /**
         * Methods annotated with {@link MqttListener}.
         */
        final ListenerMethod[] listenerMethods;

        static final TypeMetadata EMPTY = new TypeMetadata();

        private TypeMetadata() {
            this.listenerMethods = new ListenerMethod[0];
        }

        TypeMetadata(ListenerMethod[] methods) {
            this.listenerMethods = methods;
        }

    }

    /**
     * A method annotated with {@link MqttListener}, together with the annotations.
     */
    private static class ListenerMethod {

        final Method targetMethod; // NOSONAR

        final MqttListener annotation; // NOSONAR


        ListenerMethod(Method targetMethod, MqttListener annotation) { // NOSONAR
            this.targetMethod = targetMethod;
            this.annotation = annotation;
        }

    }

}
