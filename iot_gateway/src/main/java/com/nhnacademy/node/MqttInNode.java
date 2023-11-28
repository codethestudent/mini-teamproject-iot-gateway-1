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

public class MqttInNode extends InputNode {
    private List<String> sensorList;
    private String publisherId;
    private JSONObject object;

    public MqttInNode(int count) {
        super(count);
    }

    public MqttInNode() {
        this(1);
    }

    @Override
    void preprocess() {
        sensorList = new ArrayList<>();
        publisherId = UUID.randomUUID().toString();
        object = new JSONObject();

    }

    @Override
    void process() {

        try (IMqttClient client = new MqttClient("tcp://localhost:1883", publisherId)) {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            client.connect(options);
            client.subscribe("#", (topic, msg) -> {
                System.out.println("Message received : " + topic + msg);
                object.put("topic", topic);
                object.put("payload", msg);
                JsonMessage messageObject = new JsonMessage(object);
                output(messageObject);
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    void postprocess() {

    }
}
