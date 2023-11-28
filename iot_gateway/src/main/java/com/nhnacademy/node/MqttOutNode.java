package com.nhnacademy.node;

import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.nhnacademy.exception.AlreadyExistsException;
import com.nhnacademy.exception.InvalidArgumentException;
import com.nhnacademy.exception.OutOfBoundsException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;
import org.json.simple.JSONObject;

public class MqttOutNode extends OutputNode {

    private String broker;
    private String topic;


    public MqttOutNode(String name, int count, String broker, String topic) {
        super(name, count);
        this.broker = broker;
        this.topic = topic;
    }


    public void connectInputWire(int index, Wire wire) {
        super.connectInputWire(index, wire);
    }


    public void sendToTelegraf() {
        try {

            MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.connect(options);


            Wire inputWire = getInputWire(0);
            if (inputWire != null) {
                JSONObject jsonObject = new JSONObject();
                while (inputWire.hasMessage()) {
                    Message message = inputWire.get();


                    if (message instanceof JsonMessage) {
                        JsonMessage jsonMessage = (JsonMessage) message;
                        JSONObject messageJsonObject = jsonMessage.getJsonObject();

                        
                        for (Object key : messageJsonObject.keySet()) {
                            Object value = messageJsonObject.get(key);
                            jsonObject.put(key.toString(), value);
                        }
                    }
                }
                MqttMessage message = new MqttMessage(jsonObject.toJSONString().getBytes());
                client.publish(topic, message);
            }


            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
