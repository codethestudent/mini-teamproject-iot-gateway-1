package com.nhnacademy.node;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;

public class MqttOutNode extends OutputNode {
    private Broker broker;
    private String topic;
    private int qos;

    public MqttOutNode(String id, int wireCount, Broker broker, String topic, int qos) {
        super(id, wireCount);
        this.broker = broker;
        this.topic = topic;
        this.qos = qos;
    }

    public MqttOutNode(int wireCount, Broker broker, String topic, int qos) {
        super(wireCount);
        this.broker = broker;
        this.topic = topic;
        this.qos = qos;
    }

    @Override
    void preprocess() {
        // TODO Auto-generated method stub

    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire inputWire = getInputWire(i);
            if (inputWire == null)
                continue;
            Message message = inputWire.get();
            if (!(message instanceof JsonMessage))
                continue;
            JsonMessage jsonMessage = (JsonMessage) message;
            try {
                broker.getClient().publish(topic,
                        new MqttMessage(jsonMessage.getJsonObject().toJSONString().getBytes()));
            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
