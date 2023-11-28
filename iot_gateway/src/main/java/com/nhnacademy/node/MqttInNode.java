package com.nhnacademy.node;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.system.SystemOption;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttInNode extends InputNode {
    private String publisherId;
    private JSONObject object;
    private String[] args;
    private SystemOption sOptions;

    public MqttInNode(int count) {
        super(count);
    }

    public MqttInNode() {
        this(1);
    }

    public MqttInNode(String[] args) {
        this(1);
        this.args = args;
    }

    @Override
    void preprocess() {
        publisherId = UUID.randomUUID().toString();
        object = new JSONObject();

        sOptions = SystemOption.getSystemOption(args);
    }

    @Override
    void process() {

        try (IMqttClient client = new MqttClient("tcp://ems.nhnacademy.com:1883", publisherId)) {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            // 연결 상태 확인
            if (!(client.isConnected())) {
                client.connect(options);
            }

            String topicDirectory = (sOptions.getApplicationNamme() != null ? sOptions.getApplicationNamme()
                    : "application");
            String[] sensorType = (sOptions.getSensors() != null ? sOptions.getSensors()
                    : new String[] { "temperature", "humidity", "co2" });

            client.subscribe(topicDirectory + "/#", (topic, msg) -> {
                log.info("Message received : " + topic + msg);
                object.put("topic", topic);
                object.put("payload", msg);
                JsonMessage messageObject = new JsonMessage(object);
                log.info(object.toString());
                output(messageObject);
            });
        } catch (MqttException e) {
            if (e.getReasonCode() == 32100) { // 이미 연결된 경우
                log.info("Client is already connected.");
            } else {
                e.printStackTrace();
            }
        }
    }

    @Override
    void postprocess() {

    }
}
