package com.nhnacademy.node;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.system.SystemOption;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttInNode extends InputNode {
    private JSONObject jsonObject;
    private String[] args;
    private SystemOption sOptions;
    private IMqttClient client;

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
        String publisherId;
        publisherId = UUID.randomUUID().toString();
        jsonObject = new JSONObject();

        sOptions = SystemOption.getSystemOption(args);
        try {
            client = new MqttClient("tcp://ems.nhnacademy.com:1883", publisherId);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    void process() {

        try {
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

            client.subscribe(topicDirectory + "/+/device/+/event/up/#", (topic, msg) -> {
                // Msg.getpayload() : 바이트 배열을 얻음
                String messageStr = new String(msg.getPayload(), StandardCharsets.UTF_8);

                try {
                    JsonObject json = JsonParser.parseString(messageStr).getAsJsonObject();
                    for (String sensor : sensorType) {
                        // json.getAsJsonObject("토픽") : 아래 json 값을 가져오고 .has()는 키들을 추출함
                        if (json.has("object") && json.getAsJsonObject("object").has(sensor)) {
                            double value = json.getAsJsonObject("object").get(sensor).getAsDouble();

                            log.info(sensor + " : " + value);

                            jsonObject.put("topic", topic);
                            jsonObject.put("payload", msg);
                            JsonMessage messageObject = new JsonMessage(jsonObject);
                            output(messageObject);

                            log.trace(jsonObject.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                log.info(topic + " : " + messageStr);
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
        try {
            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
