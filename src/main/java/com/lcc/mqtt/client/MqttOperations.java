package com.lcc.mqtt.client;

import org.eclipse.paho.client.mqttv3.*;

public interface MqttOperations {

    /**
     * Subscribe to a topic, which may include wildcards.
     *
     *
     * @param topicFilter the topic to subscribe to, which can include wildcards.
     * @param qos the maximum quality of service at which to subscribe. Messages
     * published at a lower quality of service will be received at the published
     * QoS.  Messages published at a higher quality of service will be received using
     * the QoS specified on the subscribe.
     * @param messageListener a callback to handle incoming messages
     * @throws MqttException if there was an error registering the subscription.
     */
    void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener) throws MqttException;


    /**
     * Publishes a message to a topic on the server.
     * <p>
     * Delivers a message to the server at the requested quality of service and returns control
     * once the message has been delivered. In the event the connection fails or the client
     * stops, any messages that are in the process of being delivered will be delivered once
     * a connection is re-established to the server on condition that:</p>
     * <ul>
     * <li>The connection is re-established with the same clientID</li>
     * <li>The original connection was made with (@link MqttConnectOptions#setCleanSession(boolean)}
     * set to false</li>
     * <li>The connection is re-established with (@link MqttConnectOptions#setCleanSession(boolean)}
     * set to false</li>
     * </ul>
     * <p>In the event that the connection breaks or the client stops it is still possible to determine
     * when the delivery of the message completes. Prior to re-establishing the connection to the server:</p>
     * <ul>
     * <li>Register a {@link #setCallback(MqttCallback)} callback on the client and the delivery complete
     * callback will be notified once a delivery of a message completes
     * <li>or call {@link #getPendingDeliveryTokens()} which will return a token for each message that
     * is in-flight.  The token can be used to wait for delivery to complete.
     * </ul>
     *
     * <p>When building an application,
     * the design of the topic tree should take into account the following principles
     * of topic name syntax and semantics:</p>
     *
     * <ul>
     * 	<li>A topic must be at least one character long.</li>
     * 	<li>Topic names are case sensitive.  For example, <em>ACCOUNTS</em> and <em>Accounts</em> are
     * 	two different topics.</li>
     * 	<li>Topic names can include the space character.  For example, <em>Accounts
     * 	payable</em> is a valid topic.</li>
     * 	<li>A leading "/" creates a distinct topic.  For example, <em>/finance</em> is
     * 	different from <em>finance</em>. <em>/finance</em> matches "+/+" and "/+", but
     * 	not "+".</li>
     * 	<li>Do not include the null character (Unicode<pre> \x0000</pre>) in
     * 	any topic.</li>
     * </ul>
     *
     * <p>The following principles apply to the construction and content of a topic
     * tree:</p>
     *
     * <ul>
     * 	<li>The length is limited to 64k but within that there are no limits to the
     * 	number of levels in a topic tree.</li>
     * 	<li>There can be any number of root nodes; that is, there can be any number
     * 	of topic trees.</li>
     * 	</ul>
     *
     *
     * <p>This is a blocking method that returns once publish completes</p>	 *
     *
     * @param topic  to deliver the message to, for example "finance/stock/ibm".
     * @param message to delivery to the server
     * @throws MqttPersistenceException when a problem with storing the message
     * @throws MqttException for other errors encountered while publishing the message.
     * For instance client not connected.
     */
    void publish(String topic, MqttMessage message) throws MqttException, MqttPersistenceException;

    /**
     * Determines if this client is currently connected to the server.
     *
     * @return <code>true</code> if connected, <code>false</code> otherwise.
     */
    boolean isConnected();

    /**
     * Will attempt to reconnect to the server after the client has lost connection.
     * @throws MqttException if an error occurs attempting to reconnect
     */
    void reconnect() throws MqttException;

    /**
     * Close the client
     * Releases all resource associated with the client. After the client has
     * been closed it cannot be reused. For instance attempts to connect will fail.
     * @throws MqttException  if the client is not disconnected.
     */
    void close() throws MqttException;
}
