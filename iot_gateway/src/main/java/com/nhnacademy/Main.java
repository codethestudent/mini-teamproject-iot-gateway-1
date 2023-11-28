package com.nhnacademy;


import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.wire.Wire;
import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {
        // 데이터 생성
        JSONObject payload = new JSONObject();
        payload.put("time", 1701153589801L);
        payload.put("value", 20);

        // 전체 JSON 데이터 생성
        JSONObject jsonMessageObject = new JSONObject();
        jsonMessageObject.put("topic", "data/d/24e124136d151547/e/temperature/창고");
        jsonMessageObject.put("payload", payload);
        jsonMessageObject.put("_msgid", "94918c412493c803");

        // JsonMessage 객체 생성
        JsonMessage jsonMessage = new JsonMessage(jsonMessageObject);

        // MqttOutNode 객체 생성
        MqttOutNode mqttOut = new MqttOutNode("mqttOutNode", 1, "tcp://localhost", "telegraf/topic");

        // Wire 생성 및 연결
        Wire wire = new Wire();
        mqttOut.connectInputWire(0, wire);

        // Wire에 데이터 추가
        wire.put(jsonMessage);

        // MqttOutNode를 통해 Telegraf로 전송
        mqttOut.sendToTelegraf();
    }
}
