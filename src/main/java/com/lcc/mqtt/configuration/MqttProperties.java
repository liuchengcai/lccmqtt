package com.lcc.mqtt.configuration;

import lombok.Data;
import lombok.experimental.Accessors;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.net.ssl.HostnameVerifier;
import java.util.Properties;

/**
 * @Description: mqtt配置
 * @Author: chengcai
 * @Date: 2019-07-03 15:29
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "iot.mqtt")
public class MqttProperties {

    /**
     * 如果没有指定，默认的保持活动间隔(以秒为单位)
     */
    public static final int KEEP_ALIVE_INTERVAL_DEFAULT = 60;
    /**
     * 如果没有指定，则默认连接超时(以秒为单位)
     */
    public static final int CONNECTION_TIMEOUT_DEFAULT = 30;
    /**
     * 如果没有指定，则默认max inflight
     */
    public static final int MAX_INFLIGHT_DEFAULT = 10;
    /**
     * 如果没有指定，则默认的clean会话设置
     */
    public static final boolean CLEAN_SESSION_DEFAULT = true;
    /**
     * 默认的MqttVersion首先是3.1.1，如果失败，则回落到3.1
     */
    public static final int MQTT_VERSION_DEFAULT = 0;
    /**
     * Mqtt Version 3.1
     */
    public static final int MQTT_VERSION_3_1 = 3;
    /**
     * Mqtt Version 3.1.1
     */
    public static final int MQTT_VERSION_3_1_1 = 4;

    /**
     *MQTT客户端向服务器发起CONNECT请求时，通过KeepAlive参数设置保活周期。
     * 客户端在无报文发送时，按KeepAlive周期定时发送2字节的PINGREQ心跳报文，服务端收到PINGREQ报文后，回复2字节的PINGRESP报文。
     * 服务端在1.5个心跳周期内，既没有收到客户端发布订阅报文，也没有收到PINGREQ心跳报文时，主动心跳超时断开客户端TCP连接。
     * emqttd消息服务器默认按最长2.5心跳周期超时设计。
     */
    private int keepAliveInterval = KEEP_ALIVE_INTERVAL_DEFAULT;
    /**
     * 正在进行过多的发布 (32202)异常
     * 发布消息的时候qos=1,需要等待broker会ack，由于客户端发送量太大，未能及时抽到broker回复的ack（这一点可能因为网络有延时），
     * 但是客户端维持的一个变量—maxinflight,默认值为10，如果以qos=1发送的消息，maxinflight就会+1，
     * 当抽到broker回复的ack时，maxinflight-1,所以当客户端发送量太大时，又未能及时收到broker回复的ack,导致达到了maxinflght的值，出现问题。
     * 临时解决方法如下：
     * （1） 改qos=0
     * （2）调整maxinflight值的上限
     * （3）自己做流量控制
     */
    private int maxInflight = MAX_INFLIGHT_DEFAULT;
    /**
     * 遗愿消息TOPIC
     */
    private String willTopic = null;
    /**
     * 遗愿消息MESSAGE
     */
    private MqttMessage willMessage = null;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     *
     */
    private boolean httpsHostnameVerificationEnabled = true;
    /**
     *
     */
    private HostnameVerifier sslHostnameVerifier = null;
    /**
     * MQTT客户端向服务器发起CONNECT请求时，可以通过’Clean Session’标志设置会话。
     * ‘Clean Session’设置为0，表示创建一个持久会话，在客户端断开连接时，会话仍然保持并保存离线消息，直到会话超时注销。
     * ‘Clean Session’设置为1，表示创建一个新的临时会话，在客户端断开时，会话自动销毁。
     * TRUE:1
     * FALSE:0
     */
    private boolean cleanSession = CLEAN_SESSION_DEFAULT;
    /**
     * 连接超时时间(以秒为单位)
     */
    private int connectionTimeout = CONNECTION_TIMEOUT_DEFAULT;
    /**
     * BROKER SERVER URI列表
     */
    private String[] serverURIs = null;
    /**
     *
     */
    private int mqttVersion = MQTT_VERSION_DEFAULT;
    /**
     * 若想要在发布“遗嘱”消息时拥有retain选项，则为true
     * MQTT客户端向服务器发布(PUBLISH)消息时，可以设置保留消息(Retained Message)标志。保留消息(Retained Message)会驻留在消息服务器，
     * 后来的订阅者订阅主题时仍可以接收该消息。
     */
    private boolean automaticReconnect = false;
    /**
     * 重连接间隔毫秒数，默认为128000ms
     */
    private int maxReconnectDelay = 128000;
    /**
     * 连接的最大重试次数
     */
    private int connectAttemptsMax=3;
    /**
     *
     */
    private Properties customWebSocketHeaders = null;

    /**
     * 终止executor服务时等待的时间(以秒为单位)。
     */
    private int executorServiceTimeout = 1;
    /**
     * 域名/IP
     */
    private String host = "localhost";
    /**
     * 端口
     */
    private int port = 1883;
    /**
     * 协议
     */
    private String protocol = "tcp";
    /**
     * @NotNull
     * 客户端唯一标识
     */
    private String clientId;
    /**
     * @NotNull将消息临时存储在其中，直到消息被发送到服务器为止
     */
    private String directory;
    /**
     * 公钥证书路径
     */
    private String crtPath;

}
