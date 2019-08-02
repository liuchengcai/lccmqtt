package com.lcc.mqtt.configuration;

import com.lcc.mqtt.annotations.EnableMqttRpc;
import com.lcc.mqtt.annotations.MqttRpc;
import com.lcc.mqtt.annotations.MqttRpcClient;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-10 15:02
 */
public class MqttRpcBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware,
        EnvironmentAware {

    ResourceLoader resourceLoader;
    ClassLoader classLoader;
    Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Set<String> basePackages = getBasePackages(metadata);
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(MqttRpcClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        for(String basePackage : basePackages){
            Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(basePackage);
            for(BeanDefinition candidateComponent : beanDefinitions){
                if(candidateComponent instanceof AnnotatedBeanDefinition){
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(), "@MqttRpcClient can only be specified on an interface");
                    Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(MqttRpcClient.class.getCanonicalName());

                    registerMqttClient(registry, annotationMetadata, attributes);
                }
            }
        }
    }

    private void registerMqttClient(BeanDefinitionRegistry registry,
                                     AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(MqttRpcFactoryBean.class);
        definition.addPropertyValue("type", className);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                null);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private static class ListenerMethod {

        final Method method; // NOSONAR

        final MqttRpc annotation; // NOSONAR


        ListenerMethod(Method method, MqttRpc annotation) { // NOSONAR
            this.method = method;
            this.annotation = annotation;
        }

    }

    private MqttRpc findListenerAnnotations(Method method) {
        MqttRpc ann = AnnotationUtils.findAnnotation(method, MqttRpc.class);
        return ann;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata()
                            .getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition
                            .getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(
                                    beanDefinition.getMetadata().getClassName(),
                                    MqttRpcBeanDefinitionRegistrar.this.classLoader);
                            return !target.isAnnotation();
                        }
                        catch (Exception ex) {
                            this.logger.error(
                                    "Could not load target class: "
                                            + beanDefinition.getMetadata().getClassName(),
                                    ex);

                        }
                    }
                    return true;
                }
                return false;

            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableMqttRpc.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
