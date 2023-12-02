package com.nhnacademy;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nhnacademy.node.Broker;
import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.SwitchNode;
import com.nhnacademy.node.DebugNode.targetType;
import com.nhnacademy.wire.Wire;

public class Main {
    public static void main(String[] args) {
        Broker broker = new Broker("1", "ems.nhnacademy.com", 1883, true, 60, true);
        MqttInNode mqttInNode = new MqttInNode("test", 1, "application/#", 2, broker);
        try {
            broker.connect();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mqttInNode.start();
        JSONArray rules = new JSONArray();
        rules.add(new JSONObject() {
            {
                put("t", "eq");
                put("v", "NHN Academy 경남");
                put("vt", "str");
            }
        });
        DebugNode debugNode = new DebugNode("debug", 1, true, true, true, true, targetType.full,
                "payload.deviceInfo.tenantName");
        SwitchNode switchNode = new SwitchNode("switch", 1, 2, 2, "payload.deviceInfo.tenantName",
                SwitchNode.propertyType.msg, rules, true, 1);
        Wire wire1 = new Wire();
        Wire wire2 = new Wire();
        mqttInNode.connectOutputWire(0, wire1);
        switchNode.connectInputWire(0, wire1);
        switchNode.connectOutputWire(0, 0, wire2);
        debugNode.connectInputWire(0, wire2);
        switchNode.start();
        debugNode.start();
    }
}
