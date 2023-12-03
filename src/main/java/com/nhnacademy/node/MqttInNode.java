package com.nhnacademy.node;

import java.net.UnknownHostException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nhnacademy.exception.AlreadyExistsException;
import com.nhnacademy.exception.OutOfBoundsException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;

public class MqttInNode extends InputNode {
    String topic;
    int qos;
    Broker broker;

    public MqttInNode(String id, int outWireCount, String topic, int qos, Broker broker) {
        super(id, 1, outWireCount);
        this.topic = topic;
        this.qos = qos;
        this.broker = broker;
    }

    public MqttInNode(int outWireCount, String topic, int qos, Broker broker) {
        super(1, outWireCount);
        this.topic = topic;
        this.qos = qos;
        this.broker = broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public void connectOutputWire(int outWireIndex, Wire wire) {
        if (getOutputWireCount() <= outWireIndex || outWireIndex < 0) {
            throw new OutOfBoundsException();
        }
        if (outputWires[0][outWireIndex] != null) {
            throw new AlreadyExistsException();
        }

        outputWires[0][outWireIndex] = wire;
    }

    @Override
    public void connectOutputWire(int outWireIndex, int index, Wire wire) {
        connectOutputWire(outWireIndex, wire);
    }

    public void disconnectOutputWire(int outWireIndex) {
        if (getOutputWireCount() <= outWireIndex || outWireIndex < 0) {
            throw new OutOfBoundsException();
        }
        if (outputWires[0][outWireIndex] == null)
            throw new AlreadyExistsException();

        outputWires[0][outWireIndex] = null;
    }

    @Override
    public void disconnectOutputWire(int outWireIndex, int index) {
        disconnectOutputWire(outWireIndex);
    }

    void output(Message message) {
        for (int i = 0; i < getOutputWireCount(); i++) {
            if (outputWires[0][i] != null) {
                outputWires[0][i].put(message);
            }
        }
    }

    @Override
    void output(Message message, int index) {
        output(message);
    }

    @Override
    void preprocess() {
        try {
            broker.getClient().subscribe(topic, qos, (topic, msg) -> {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("topic", topic.toString());
                Object payload = parser.parse(new String(msg.getPayload(), "UTF-8"));
                // payload == jsonobject
                if (payload instanceof JSONObject) {
                    jsonObject.put("payload", (JSONObject) payload);
                    output(new JsonMessage(jsonObject));
                }

            });
        } catch (Exception e) {
            if (e.getCause() instanceof UnknownHostException) {
                System.out.println("Unknown Host");
            }
            System.out.println(e.getMessage());
        }
    }
}
