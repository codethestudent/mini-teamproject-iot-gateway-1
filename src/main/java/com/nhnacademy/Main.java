package com.nhnacademy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
            String[] sensors = { "temperature", "humidity" };
            List<String> sensorList = Arrays.asList(sensors);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new String(msg.getPayload()));
            JSONObject jsonObj = (JSONObject) obj;

            if (jsonObj.containsKey("object")) {
                // if (((JSONObject) jsonObj.get("deviceInfo")).get("tenantName").equals("NHN
                // Academy 경남")) {
                HashMap<String, String> deviceinfo = new HashMap<>();
                HashMap<String, String> value = new HashMap<>();
                for (Object key : ((JSONObject) jsonObj.get("deviceInfo")).keySet())
                    deviceinfo.put(key.toString(), ((JSONObject) jsonObj.get("deviceInfo")).get(key).toString());
                for (Object key : ((JSONObject) jsonObj.get("object")).keySet())
                    value.put(key.toString(), ((JSONObject) jsonObj.get("object")).get(key).toString());
                // System.out.println("deviceInfo : " + deviceinfo);
                // System.out.println("value : " + value);
                System.out.println("지사 : " + ((JSONObject) jsonObj.get("deviceInfo")).get("tenantName").toString());
                for (String key : value.keySet()) {
                    if (sensorList.contains(key)) {
                        System.out.println(key.toString() + " : " + value.get(key));
                    }
                }
                // }
            }
        });
        while (!Thread.currentThread().isInterrupted()) {
            Thread.sleep(100);
        }
        publisher.disconnect();
        publisher.close();
    }
}
