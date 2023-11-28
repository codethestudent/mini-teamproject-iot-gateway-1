package com.nhnacademy;


import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.wire.Wire;
import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;

public class Main {

    public static void main(String[] args) {
        
        String broker = "tcp://localhost:1883";
        String topic = "data/d/24e124128c067999/e/temperature/강의실 A 입구";

        
        MqttOutNode mqttOutNode = new MqttOutNode("MqttOutNode", 1, broker, topic);

        
        Wire inputWire = new Wire();
        mqttOutNode.connectInputWire(0, inputWire);

        
        JSONObject payload = new JSONObject();
        payload.put("time", 1701155929717L);
        payload.put("value", 9);

        JsonMessage jsonMessage = new JsonMessage(payload);

        
        inputWire.put(jsonMessage);

        
        mqttOutNode.sendToTelegraf();

        
        try {
            MqttClient client = new MqttClient(broker, MqttClient.generateClientId());

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost to the MQTT broker.");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("Received message from Telegraf: " + topic + " " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            client.connect();
            client.subscribe(topic);

           
            Thread.sleep(2000);

            
            client.disconnect();
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
