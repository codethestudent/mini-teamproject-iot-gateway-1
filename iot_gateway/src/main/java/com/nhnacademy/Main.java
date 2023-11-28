package com.nhnacademy;

import java.util.HashMap;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {
    public static void main(String[] args) throws MqttException, InterruptedException {
        String publisherId = UUID.randomUUID().toString();
        IMqttClient publisher = new MqttClient("tcp://ems.nhnacademy.com", publisherId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setWill("application/will", "Disconnected".getBytes(), 1, false);
        options.setKeepAliveInterval(10000);
        options.setExecutorServiceTimeout(0);
        publisher.connect(options);

        MqttMessage message = new MqttMessage("Hello".getBytes());
        publisher.publish("data", message);
        String application_name = "application";
        publisher.subscribe(application_name + "/#", (topic, msg) -> {
            String[] topics = topic.split("/");
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new String(msg.getPayload()));
            JSONObject jsonObj = (JSONObject) obj;

            if (jsonObj.containsKey("object"))
                if (((JSONObject) jsonObj.get("deviceInfo")).get("tenantName").equals("NHN Academy 경남")) {
                    System.out.println("deviceinfo : " + jsonObj.get("deviceInfo"));
                    System.out.println("object : " + jsonObj.get("object"));
                    HashMap<String,String> value = (HashMap) jsonObj.get("object");
                    if(value.containsKey("temperature"))
                    System.out.println(value.get("temperature"));
                }
        });
        while (!Thread.currentThread().isInterrupted()) {
            Thread.sleep(100);
        }
        publisher.disconnect();
        publisher.close();
    }
}
