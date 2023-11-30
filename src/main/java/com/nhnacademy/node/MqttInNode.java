package com.nhnacademy.node;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.system.SystemOption;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttInNode extends InputNode {
    private SystemOption sOptions;
    private IMqttClient client;
    private String[] args;

    public MqttInNode(int count, String[] args) {
        super(count);
        this.args = args;
    }

    public MqttInNode(String[] args) {
        this(1, args);
    }

    @Override
    void preprocess() {
        String publisherId;
        publisherId = UUID.randomUUID().toString();

        sOptions = SystemOption.getSystemOption(args);
        try {
            client = new MqttClient(sOptions.getInputServerUri(), publisherId,
                    new MqttDefaultFilePersistence("./target/trash"));

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

            client.subscribe(topicDirectory + "/+/device/+/event/up/#", (topic, msg) -> {
                // Msg.getpayload() : 바이트 배열을 얻음
                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(new String(msg.getPayload()));
                    JSONObject jsonObj = (JSONObject) obj;
                    JsonMessage messageObject = new JsonMessage(jsonObj);
                    output(messageObject);

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
