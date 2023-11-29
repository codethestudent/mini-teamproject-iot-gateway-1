package com.nhnacademy.node;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;
import org.json.simple.JSONObject;

public class MqttOutNode extends OutputNode {

    private String broker;
    private MqttClient client;

    public MqttOutNode(String name, int count, String broker) {
        super(name, count);
        this.broker = broker;
    }

    @Override
    void preprocess() {
        try {
            client = new MqttClient(broker, MqttClient.generateClientId());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    void process() {
        sendToTelegraf();
    }

    public void sendToTelegraf() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.connect(options);

            Wire inputWire = getInputWire(0);
            if (inputWire != null) {
                while (inputWire.hasMessage()) {
                    Message message = inputWire.get();

                    if (message instanceof JsonMessage) {
                        JsonMessage jsonMessage = (JsonMessage) message;
                        JSONObject messageJsonObject = jsonMessage.getJsonObject();

                        client.publish(messageJsonObject.get("topic").toString(),
                                new MqttMessage(messageJsonObject.get("payload").toString().getBytes()));
                    }
                }

            }

            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void postprocess() {
        try {
            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
