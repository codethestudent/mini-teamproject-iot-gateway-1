package com.nhnacademy.node;

import java.net.UnknownHostException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttInNode extends InputNode {
    private String topic;
    private int qos;
    private Broker broker;

    public MqttInNode(String id, String topic, int qos, Broker broker) {
        super(id, 1);
        this.topic = topic;
        this.qos = qos;
        this.broker = broker;
    }

    public MqttInNode(String topic, int qos, Broker broker) {
        super(1);
        this.topic = topic;
        this.qos = qos;
        this.broker = broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    @Override
    public void connectOutputWire(int index, Wire wire) {
        connectOutputWire(0, wire);
    }

    public void connectOutputWire(Wire wire) {
        connectOutputWire(0, wire);
    }

    public void disconnectOutputWire(Wire wire) {
        super.disconnectOutputWire(0, wire);
    }

    @Override
    public void disconnectOutputWire(int index, Wire wire) {
        throw new UnsupportedOperationException();
    }

    void output(Message message) {
        super.output(message, 0);
    }

    @Override
    void output(Message message, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    void preprocess() {
        try {
            broker.getClient().subscribe(topic, qos, (clientTopic, clientMsg) -> {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("topic", clientTopic);
                Object payload = parser.parse(new String(clientMsg.getPayload(), "UTF-8"));
                // payload == jsonobject
                if (payload instanceof JSONObject) {
                    jsonObject.put("payload", (JSONObject) payload);
                    output(new JsonMessage(jsonObject));
                }

            });
        } catch (Exception e) {
            if (e.getCause() instanceof UnknownHostException) {
                System.out.println("Unknown Host");
            } else {
                log.error("Exception", e);
            }
        }
    }
}
