package com.lcc.mqtt.configuration;

import com.lcc.mqtt.annotations.EnableMqtt;
import com.lcc.mqtt.client.MqttClientCallback;
import com.lcc.mqtt.client.MqttTemplate;
import com.lcc.mqtt.util.SslUtil;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * @Description: TODO
 * @Author: chengcai
 * @Date: 2019-07-03 17:55
 */
@EnableMqtt
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttAutoConfiguration {

    static Logger logger = LoggerFactory.getLogger(MqttAutoConfiguration.class);

    @Bean
    public MqttTemplate mqttTemplate(MqttProperties properties) throws Exception {
        Assert.notNull(properties.getClientId(),"iot.mqtt.clientId is null");
        Assert.notNull(properties.getDirectory(),"iot.mqtt.directory is null");
        String brokerUrl = properties.getProtocol()+"://" + properties.getHost() + ":" + properties.getPort();
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(properties.getDirectory());
        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(properties.isCleanSession());
        if(properties.getPassword() != null ) {
            conOpt.setPassword(properties.getPassword().toCharArray());
        }
        if(properties.getUserName() != null) {
            conOpt.setUserName(properties.getUserName());
        }
        if(properties.getProtocol().equals("ssl")){
            conOpt.setSocketFactory(SslUtil.getSSLSocktet(properties.getCrtPath()));
        }
        MqttClient client = new MqttClient(brokerUrl,properties.getClientId(), dataStore);
        client.setCallback(new MqttClientCallback());
        client.connect(conOpt);
        logger.info("Connected to borker : {}",brokerUrl);
        MqttTemplate template = new MqttTemplate();
        template.setClient(client);
        return template;
    }

}
