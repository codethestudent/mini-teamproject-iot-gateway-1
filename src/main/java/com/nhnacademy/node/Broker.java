package com.nhnacademy.node;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;


public class Broker {
    private String id;
    private String broker;
    private int port;
    private boolean autoConnect;
    private int keepAlive;
    private boolean cleansession;

    private IMqttClient client;
    private MqttConnectOptions options = new MqttConnectOptions();

    public Broker(String id, String broker, int port, boolean autoConnect, int keepAlive, boolean cleansession) {
        super();
        this.id = id;
        this.broker = broker;
        this.port = port;
        this.autoConnect = autoConnect;
        this.keepAlive = keepAlive;
        this.cleansession = cleansession;
        setOption();
        try {
            connect();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void setOption() {
        options.setAutomaticReconnect(autoConnect);
        options.setCleanSession(cleansession);
        options.setKeepAliveInterval(keepAlive);
    }

    public String getId(){
        return id;
    }

    public void connect() throws MqttException {
        client = new MqttClient("tcp://" + broker + ":" + port, id, new MqttDefaultFilePersistence("./target/trash"));
        client.connect(options);
    }

    public void disconnect() throws MqttException {
        client.disconnect();
    }

    public IMqttClient getClient() {
        return client;
    }
}
